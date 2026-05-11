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
        respond(['ok' => false, 'error' => 'Método HTTP inválido. Use POST.'], 405);
    }

    // Accept both JSON body and form POST
    $input = $_POST;
    if (empty($input)) {
        $raw = file_get_contents('php://input');
        $json = json_decode($raw, true);
        if (is_array($json)) $input = $json;
    }

    $name = trim((string)($input['name'] ?? ''));
    $email = trim(strtolower((string)($input['email'] ?? '')));
    $password = (string)($input['password'] ?? '');
    $restauranteNome = trim((string)($input['restauranteNome'] ?? ''));
    $restauranteMorada = trim((string)($input['restauranteMorada'] ?? ''));
    $restaurantCode = trim((string)($input['restaurantCode'] ?? '')); // NIF/CNPJ

    if ($name === '' || $email === '' || $password === '' || $restauranteNome === '' || $restauranteMorada === '' || $restaurantCode === '') {
        respond(['ok' => false, 'error' => 'Preencha todos os campos obrigatórios.'], 422);
    }

    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        respond(['ok' => false, 'error' => 'Formato de email inválido.'], 422);
    }

    // --- Integração nif.pt ---
    require_once __DIR__ . '/../config/env_loader.php';
    $nifApiKey = env('NIF_API_KEY');

    // Validar apenas se a API Key estiver configurada e o NIF parecer português (9 dígitos)
    if (!empty($nifApiKey) && preg_match('/^[0-9]{9}$/', $restaurantCode)) {
        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "https://www.nif.pt/?json=1&q=" . urlencode($restaurantCode) . "&key=" . urlencode($nifApiKey));
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($httpCode === 200 && $response) {
            $data = json_decode($response, true);
            if (isset($data['result']) && $data['result'] === 'success') {
                $nifInfo = $data['records'][$restaurantCode] ?? null;
                // Se a API indicar que não existe, bloquear o registo.
                if (!$nifInfo) {
                     respond(['ok' => false, 'error' => 'O NIF fornecido não é válido segundo a base de dados do NIF.pt.'], 422);
                } else {
                    // Auto-fill nome se existir na resposta e for valido
                    if (isset($nifInfo['title']) && !empty(trim($nifInfo['title']))) {
                        $title = trim($nifInfo['title']);
                        // Ignorar os erros standard da API para free tiers
                        if (strpos($title, 'Key necessary') === false) {
                            $restauranteNome = $title;
                        }
                    }
                }
            }
        }
        // Se a API falhar (timeout/500), ignoramos e seguimos com o registo para não bloquear os utilizadores.
    }
    // -------------------------

    $pdo = db();

    $restColumn = null;
    $candidates = ['NIF', 'CNPJ', 'Codigo', 'RestaurantCode'];
    $placeholders = implode(',', array_fill(0, count($candidates), '?'));
    $stmt = $pdo->prepare("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'Restaurante' AND COLUMN_NAME IN ($placeholders)");
    $stmt->execute($candidates);
    $found = $stmt->fetchAll(PDO::FETCH_COLUMN);
    $foundLower = array_map('strtolower', $found);

    foreach ($candidates as $cand) {
        if (in_array(strtolower($cand), $foundLower)) {
            // Find the actual column name from the DB result to preserve case if needed by DB
            $index = array_search(strtolower($cand), $foundLower);
            $restColumn = $found[$index];
            break;
        }
    }

    if (!$restColumn) {
        respond(['ok' => false, 'error' => 'Erro de configuração: coluna NIF/CNPJ não encontrada na tabela Restaurante.'], 500);
    }

    $st = $pdo->prepare("SELECT 1 FROM dbo.Restaurante WHERE [$restColumn] = :restaurantCode");
    $st->execute(['restaurantCode' => $restaurantCode]);
    if ($st->fetch()) {
        respond(['ok' => false, 'error' => 'Já existe um restaurante cadastrado com este NIF/CNPJ.'], 409);
    }

    $st = $pdo->prepare('SELECT IdUser FROM dbo.Users WHERE Email = :email');
    $st->execute(['email' => $email]);
    if ($st->fetch()) {
        respond(['ok' => false, 'error' => 'Já existe uma conta com este email.'], 409);
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
        respond(['ok' => false, 'error' => 'Já existe um restaurante com este nome.'], 409);
    }

    $st = $pdo->prepare("INSERT INTO dbo.Restaurante (Nome, Morada, [$restColumn]) VALUES (:nome, :morada, :restaurantCode)");
    $st->execute(['nome' => $restauranteNome, 'morada' => $restauranteMorada, 'restaurantCode' => $restaurantCode]);
    $restauranteId = (int)$pdo->lastInsertId();

    $st = $pdo->prepare('INSERT INTO dbo.RestauranteUser (UserId, RestauranteId) VALUES (:userId, :restauranteId)');
    $st->execute(['userId' => $userId, 'restauranteId' => $restauranteId]);

    $pdo->commit();

    respond(['ok' => true, 'message' => 'Restaurante registado com sucesso! Faça login para começar.', 'userId' => $userId, 'restauranteId' => $restauranteId]);

} catch (Throwable $e) {
    if (isset($pdo) && $pdo->inTransaction()) {
        $pdo->rollBack();
    }
    respond(['ok' => false, 'error' => 'Erro interno do servidor. Tente novamente mais tarde.', 'message' => $e->getMessage()], 500);
}
