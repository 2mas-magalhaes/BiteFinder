<?php
// C:\bitefinder-api\api\auth\register_restaurante.php
ob_start();
require_once __DIR__ . '/../config/db.php';

header('Content-Type: application/json; charset=utf-8');

function respond($arr, int $code = 200): void {
    if (ob_get_length()) ob_clean();
    http_response_code($code);
    echo json_encode($arr, JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        respond(['ok' => false, 'error' => 'Método inválido'], 405);
    }

    $name = trim((string)($_POST['name'] ?? ''));
    $email = trim(strtolower((string)($_POST['email'] ?? '')));
    $password = (string)($_POST['password'] ?? '');
    $restauranteNome = trim((string)($_POST['restauranteNome'] ?? ''));
    $restauranteMorada = trim((string)($_POST['restauranteMorada'] ?? ''));
    $restaurantCode = trim((string)($_POST['restaurantCode'] ?? '')); // NIF/CNPJ

    if ($name === '' || $email === '' || $password === '' || $restauranteNome === '' || $restauranteMorada === '' || $restaurantCode === '') {
        respond(['ok' => false, 'error' => 'Campos obrigatórios em falta'], 422);
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        respond(['ok' => false, 'error' => 'Email inválido'], 422);
    }

    $pdo = db();

    $restColumn = null;
    $candidates = ['NIF', 'CNPJ', 'Codigo', 'RestaurantCode'];
    $stmt = $pdo->prepare("SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Restaurante' AND COLUMN_NAME = :col");
    foreach ($candidates as $cand) {
        $stmt->execute(['col' => $cand]);
        if ($stmt->fetch()) {
            $restColumn = $cand;
            break;
        }
    }

    if (!$restColumn) {
        respond(['ok' => false, 'error' => 'Tabela Restaurante não contém coluna NIF/CNPJ'], 500);
    }

    $st = $pdo->prepare("SELECT 1 FROM dbo.Restaurante WHERE [$restColumn] = :restaurantCode");
    $st->execute(['restaurantCode' => $restaurantCode]);
    if ($st->fetch()) {
        respond(['ok' => false, 'error' => 'NIF/CNPJ já cadastrado'], 409);
    }

    $st = $pdo->prepare('SELECT IdUser FROM dbo.Users WHERE Email = :email');
    $st->execute(['email' => $email]);
    if ($st->fetch()) {
        respond(['ok' => false, 'error' => 'Email já usado'], 409);
    }

    $passwordHash = password_hash($password, PASSWORD_BCRYPT);

    $pdo->beginTransaction();

    $st = $pdo->prepare('INSERT INTO dbo.Users (Nome, Email, PasswordHash, Role) VALUES (:nome, :email, :passwordHash, :role)');
    $st->execute(['nome' => $name, 'email' => $email, 'passwordHash' => $passwordHash, 'role' => 'restaurante']);
    $userId = (int)$pdo->lastInsertId();

    $st = $pdo->prepare('SELECT 1 FROM dbo.Restaurante WHERE Nome = :restauranteNome');
    $st->execute(['restauranteNome' => $restauranteNome]);
    if ($st->fetch()) {
        $pdo->rollBack();
        respond(['ok' => false, 'error' => 'Restaurante com este nome já existe'], 409);
    }

    $st = $pdo->prepare("INSERT INTO dbo.Restaurante (Nome, Morada, [$restColumn]) VALUES (:nome, :morada, :restaurantCode)");
    $st->execute(['nome' => $restauranteNome, 'morada' => $restauranteMorada, 'restaurantCode' => $restaurantCode]);
    $restauranteId = (int)$pdo->lastInsertId();

    $st = $pdo->prepare('INSERT INTO dbo.RestauranteUser (UserId, RestauranteId) VALUES (:userId, :restauranteId)');
    $st->execute(['userId' => $userId, 'restauranteId' => $restauranteId]);

    $pdo->commit();

    respond(['ok' => true, 'message' => 'Restaurante registado com sucesso', 'userId' => $userId, 'restauranteId' => $restauranteId]);

} catch (Throwable $e) {
    if (isset($pdo) && $pdo->inTransaction()) {
        $pdo->rollBack();
    }
    respond(['ok' => false, 'error' => 'Erro interno', 'message' => $e->getMessage()], 500);
}
