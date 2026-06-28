<?php
ob_start();
require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../jwt_functions.php';
require_once __DIR__ . '/../config/env_loader.php';

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

    $raw = file_get_contents('php://input');
    $body = json_decode($raw, true);

    if (!is_array($body)) {
        respond(['ok' => false, 'error' => 'JSON inválido'], 400);
    }

    $provider = trim((string)($body['provider'] ?? '')); // google, apple, facebook, microsoft
    $token = trim((string)($body['token'] ?? ''));

    if ($provider === '' || $token === '') {
        respond(['ok' => false, 'error' => 'Provider e token são obrigatórios'], 422);
    }

    $validProviders = ['google', 'apple', 'facebook', 'microsoft'];
    if (!in_array($provider, $validProviders)) {
        respond(['ok' => false, 'error' => 'Provider não suportado'], 400);
    }

    // AQUI OCORRERIA A VALIDAÇÃO DO TOKEN NO LADO DO SERVIDOR USANDO AS APIS DOS PROVIDERS.
    // Por exemplo, no Google: chamar "https://oauth2.googleapis.com/tokeninfo?id_token=" . $token
    // Neste momento do projeto, usaremos o env_loader para simular chaves/validação:
    $clientIdEnvVar = strtoupper($provider) . '_CLIENT_ID';
    $clientId = env($clientIdEnvVar);

    // Simulação: se o token contiver algo, extraímos um email dummy (ou real caso fosse implementado)
    // Assumimos que o frontend envia o email ou obtemos da API externa.
    $email = isset($body['email']) ? strtolower(trim((string)$body['email'])) : $provider . '_user@example.com';
    $nome = isset($body['name']) ? trim((string)$body['name']) : 'User ' . ucfirst($provider);

    $pdo = db();

    // Verifica se já existe um utilizador com este email
    $st = $pdo->prepare('SELECT IdUser, Nome, Role, PasswordHash FROM dbo.Users WHERE Email = :email');
    $st->execute(['email' => $email]);
    $user = $st->fetch();

    if (!$user) {
        // Regista o utilizador automaticamente se não existir
        $randomPass = bin2hex(random_bytes(8));
        $passwordHash = password_hash($randomPass, PASSWORD_BCRYPT);

        $st = $pdo->prepare('INSERT INTO dbo.Users (Nome, Email, PasswordHash, Role) VALUES (:nome, :email, :passwordHash, :role)');
        $st->execute([
            'nome' => $nome,
            'email' => $email,
            'passwordHash' => $passwordHash,
            'role' => 'user'
        ]);
        $userId = (int)$pdo->lastInsertId();
        $userRole = 'user';
        $userNome = $nome;
    } else {
        $userId = (int)$user['IdUser'];
        $userRole = $user['Role'];
        $userNome = $user['Nome'];
    }

    // Criar JWT
    $payload = [
        'sub' => $userId,
        'email' => $email,
        'role' => $userRole,
        'nome' => $userNome
    ];

    $tokenJwt = jwt_encode($payload);

    respond([
        'ok' => true,
        'token' => $tokenJwt,
        'user' => [
            'id' => $userId,
            'nome' => $userNome,
            'email' => $email,
            'role' => $userRole
        ]
    ]);

} catch (Throwable $e) {
    respond(['ok' => false, 'error' => 'Erro interno do servidor', 'message' => 'Ocorreu um erro no servidor.'], 500);
}
