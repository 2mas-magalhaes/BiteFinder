<?php
ob_start();
require_once __DIR__ . '/_common.php';

header('Content-Type: application/json; charset=utf-8');

try {
    $pdo = db();

    $id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
    if ($id <= 0) {
        pratos_respond([
            'ok' => false,
            'error' => 'Id do prato inválido'
        ], 400);
    }

    $row = pratos_fetch_item($pdo, $id, false);
    if (!$row) {
        pratos_respond([
            'ok' => false,
            'error' => 'Prato não encontrado'
        ], 404);
    }

    pratos_respond([
        'ok' => true,
        'item' => $row
    ]);

} catch (Throwable $e) {
    pratos_respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], 500);
}
