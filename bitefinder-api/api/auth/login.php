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

// Este endpoint foi desativado.
// Utilize api/auth/login_jwt.php para autenticação.
respond([
    'ok' => false,
    'error' => 'Este endpoint foi desativado. Utilize api/auth/login_jwt.php para login.'
], 410);
    ], 500);
}
