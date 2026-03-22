<?php
// C:\bitefinder-api\api\pratos\create.php
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
    require_once __DIR__ . '/../middleware/auth_jwt.php';
    $jwtPayload = require_jwt();

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        respond(['ok' => false, 'error' => 'Método inválido'], 405);
    }

    $raw = file_get_contents('php://input');
    $body = json_decode($raw, true);

    if (!is_array($body)) {
        respond(['ok' => false, 'error' => 'JSON inválido'], 400);
    }

    $userId = (int)($body['userId'] ?? 0);
    $restauranteId = (int)($body['restauranteId'] ?? 0);

    if (isset($jwtPayload['sub'])) {
        $userId = (int)$jwtPayload['sub'];
        if (isset($jwtPayload['restaurantes'][0])) {
            $restauranteId = (int)$jwtPayload['restaurantes'][0];
        }
    }
    $nome = trim((string)($body['nome'] ?? ''));
    $descricao = trim((string)($body['descricao'] ?? ''));
    $categoria = trim((string)($body['categoria'] ?? ''));
    $preco = isset($body['preco']) && is_numeric($body['preco']) ? (float)$body['preco'] : null;
    $imagemUrl = trim((string)($body['imagemUrl'] ?? ''));

    if ($userId <= 0 || $restauranteId <= 0 || $nome === '') {
        respond(['ok' => false, 'error' => 'Dados obrigatórios em falta'], 422);
    }

    $pdo = db();

    // autoriza só dono do restaurante
    $stmt = $pdo->prepare('SELECT 1 FROM dbo.RestauranteUser WHERE UserId = :userId AND RestauranteId = :restauranteId');
    $stmt->execute(['userId' => $userId, 'restauranteId' => $restauranteId]);
    if (!$stmt->fetch()) {
        respond(['ok' => false, 'error' => 'Acesso negado'], 403);
    }

    $stmt = $pdo->prepare('INSERT INTO dbo.Prato (NomePrato, Categoria, Descricao, Preco, ImagemUrl, RestauranteId, Disponivel) VALUES (:nome, :categoria, :descricao, :preco, :imagemUrl, :restauranteId, 1)');
    $stmt->bindValue(':nome', $nome);
    $stmt->bindValue(':categoria', $categoria !== '' ? $categoria : null, PDO::PARAM_STR);
    $stmt->bindValue(':descricao', $descricao !== '' ? $descricao : null, PDO::PARAM_STR);
    $stmt->bindValue(':preco', $preco !== null ? $preco : null, PDO::PARAM_STR);
    $stmt->bindValue(':imagemUrl', $imagemUrl !== '' ? $imagemUrl : null, PDO::PARAM_STR);
    $stmt->bindValue(':restauranteId', $restauranteId, PDO::PARAM_INT);
    $stmt->execute();

    respond(['ok' => true, 'message' => 'Prato criado com sucesso', 'idPrato' => (int)$pdo->lastInsertId()]);

} catch (Throwable $e) {
    respond(['ok' => false, 'error' => 'Erro interno', 'message' => $e->getMessage()], 500);
}
