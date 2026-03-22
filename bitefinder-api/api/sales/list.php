<?php
ob_start();
require_once __DIR__ . '/../middleware/auth_jwt.php';
header('Content-Type: application/json; charset=utf-8');

function respond($arr, int $code = 200): void {
    if (ob_get_length()) ob_clean();
    http_response_code($code);
    echo json_encode($arr, JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    $payload = require_jwt();
    $restauranteId = $payload['restaurantes'][0] ?? null;
    if (!$restauranteId) {
        respond(['ok' => false, 'error' => 'Restaurante inválido'], 400);
    }

    // Demo stats; substituir por consulta real de vendas
    $dados = [
        ['data' => '2026-03-20', 'vendas' => 18, 'receita' => 682.40],
        ['data' => '2026-03-21', 'vendas' => 24, 'receita' => 850.10],
        ['data' => '2026-03-22', 'vendas' => 21, 'receita' => 910.05],
    ];

    respond(['ok' => true, 'restauranteId' => $restauranteId, 'stats' => $dados]);
} catch (Throwable $e) {
    respond(['ok' => false, 'error' => 'Erro interno', 'message' => $e->getMessage()], 500);
}
