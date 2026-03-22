<?php
// C:\bitefinder-api\api\auth\login.php

ob_start();
session_start();

header('Content-Type: application/json; charset=utf-8');

ini_set('display_errors', '0');
ini_set('display_startup_errors', '0');
error_reporting(E_ALL);

set_error_handler(function($severity, $message, $file, $line) {
    throw new ErrorException($message, 0, $severity, $file, $line);
});

require_once __DIR__ . '/../config/db.php';

function respond($arr, int $code = 200): void {
    if (ob_get_length()) { ob_clean(); }
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

    $nif = trim($body['nif'] ?? '');
    $password = (string)($body['password'] ?? '');

    if ($nif === '' || $password === '') {
        respond(['ok' => false, 'error' => 'NIF e password são obrigatórios'], 422);
    }

    $pdo = db();

    $restColumn = findRestaurantColumn($pdo);
    if (!$restColumn) {
        respond(['ok' => false, 'error' => 'Configuração de NIF/CNPJ não encontrada'], 500);
    }

    $st = $pdo->prepare("SELECT u.IdUser, u.Nome, u.Email, u.PasswordHash, u.Role FROM dbo.Users u JOIN dbo.RestauranteUser ru ON ru.UserId = u.IdUser JOIN dbo.Restaurante r ON r.IdRestaurante = ru.RestauranteId WHERE r.[{$restColumn}] = :nif");
    $st->execute(['nif' => $nif]);
    $u = $st->fetch(PDO::FETCH_ASSOC);

    if (!$u || !password_verify($password, $u['PasswordHash'])) {
        respond(['ok' => false, 'error' => 'Credenciais inválidas'], 401);
    }

    session_regenerate_id(true);

    $token = bin2hex(random_bytes(32));
    $_SESSION['token'] = $token;
    $_SESSION['user_id'] = (int)$u['IdUser'];
    $_SESSION['role'] = $u['Role'];

    $restaurantes = [];
    if ($u['Role'] === 'restaurante') {
        $r = $pdo->prepare('SELECT RestauranteId FROM dbo.RestauranteUser WHERE UserId = :uid');
        $r->execute(['uid' => (int)$u['IdUser']]);
        $restaurantes = array_map(fn($x) => (int)$x['RestauranteId'], $r->fetchAll(PDO::FETCH_ASSOC));
    }

    $primaryRestaurant = $restaurantes[0] ?? null;

    respond([
        'ok' => true,
        'token' => $token,
        'user' => [
            'id' => (int)$u['IdUser'],
            'nome' => $u['Nome'],
            'role' => $u['Role'],
            'restaurantes' => $restaurantes,
            'restauranteId' => $primaryRestaurant
        ]
    ], 200);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ], 500);
}
