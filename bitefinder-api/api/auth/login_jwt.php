<?php
ob_start();
require_once __DIR__ . '/../jwt_functions.php';
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

    $raw = file_get_contents('php://input');
    $body = json_decode($raw, true);
    if (!is_array($body)) {
        respond(['ok' => false, 'error' => 'JSON inválido'], 400);
    }

    $email = trim(strtolower((string)($body['email'] ?? '')));
    $password = (string)($body['password'] ?? '');

    if ($email === '' || $password === '') {
        respond(['ok' => false, 'error' => 'Email e password são obrigatórios'], 422);
    }

    $pdo = db();
    $st = $pdo->prepare('SELECT IdUser, Nome, Email, PasswordHash, Role FROM dbo.Users WHERE Email = :email');
    $st->execute(['email' => $email]);
    $u = $st->fetch(PDO::FETCH_ASSOC);

    if (!$u || !password_verify($password, $u['PasswordHash'])) {
        respond(['ok' => false, 'error' => 'Credenciais inválidas'], 401);
    }

    $restaurantes = [];
    if ($u['Role'] === 'restaurante') {
        $r = $pdo->prepare('SELECT RestauranteId FROM dbo.RestauranteUser WHERE UserId = :uid');
        $r->execute(['uid' => $u['IdUser']]);
        $restaurantes = array_column($r->fetchAll(PDO::FETCH_ASSOC), 'RestauranteId');
    }

    $token = jwt_encode([
        'sub' => $u['IdUser'],
        'email' => $u['Email'],
        'nome' => $u['Nome'],
        'role' => $u['Role'],
        'restaurantes' => $restaurantes
    ]);

    respond([
        'ok' => true,
        'token' => $token,
        'user' => [
            'id' => (int)$u['IdUser'],
            'nome' => $u['Nome'],
            'email' => $u['Email'],
            'role' => $u['Role'],
            'restaurantes' => $restaurantes
        ]
    ], 200);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno do servidor',
        'message' => $e->getMessage()
    ], 500);
}

