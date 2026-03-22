<?php
session_start();
require_once __DIR__ . '/../utils/response.php';

function require_auth(): void {
    $hdr = $_SERVER['HTTP_AUTHORIZATION'] ?? '';
    if (!preg_match('/Bearer\s+(.+)/', $hdr, $m)) {
        json_error('Token em falta', 401);
    }
    $token = $m[1];

    if (!isset($_SESSION['token']) || !hash_equals($_SESSION['token'], $token)) {
        json_error('Token inválido', 401);
    }
}

function require_role(string $role): void {
    if (($_SESSION['role'] ?? null) !== $role) {
        json_error('Sem permissões', 403);
    }
}

function current_user_id(): int {
    return (int)($_SESSION['user_id'] ?? 0);
}
