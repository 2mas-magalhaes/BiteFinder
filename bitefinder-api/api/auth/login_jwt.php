<?php
ob_start();
// DEBUG TEMPORÁRIO: mostrar todos os erros PHP no JSON de resposta
ini_set('display_errors', '1');
ini_set('display_startup_errors', '1');
error_reporting(E_ALL);
set_error_handler(function($severity, $message, $file, $line) {
    http_response_code(500);
    echo json_encode([
        'ok' => false,
        'error' => 'PHP ERROR',
        'message' => $message,
        'file' => $file,
        'line' => $line,
        'severity' => $severity
    ], JSON_UNESCAPED_UNICODE);
    exit;
});
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
        $r->execute(['uid' => (int)$u['IdUser']]);
        $restaurantes = array_map(fn($x) => (int)$x['RestauranteId'], $r->fetchAll(PDO::FETCH_ASSOC));
    }

    $token = jwt_encode([
        'sub' => (int)$u['IdUser'], 
        'role' => $u['Role'],
        'restaurantes' => $restaurantes
    ], 3600);

    respond(['ok' => true, 'token' => $token, 'user' => ['id' => (int)$u['IdUser'], 'nome' => $u['Nome'], 'role' => $u['Role'], 'restaurantes' => $restaurantes]]);
} catch (Throwable $e) {
        respond([
            'ok' => false,
            'error' => 'Erro interno do servidor. Tente novamente mais tarde.',
            'message' => $e->getMessage(),
        ], 500);
}
