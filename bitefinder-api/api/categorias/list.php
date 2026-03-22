<?php
require_once __DIR__ . '/../config/db.php';

header('Content-Type: application/json; charset=utf-8');

function respond($arr, int $code = 200): void {
    http_response_code($code);
    echo json_encode($arr, JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    $pdo = db();

    $sql = "
        SELECT DISTINCT Categoria
        FROM dbo.Prato
        WHERE Disponivel = 1
          AND Categoria IS NOT NULL
          AND LTRIM(RTRIM(Categoria)) <> ''
        ORDER BY Categoria ASC
    ";

    $stmt = $pdo->query($sql);
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $items = array_map(fn($r) => (string)$r['Categoria'], $rows);

    respond([
        'ok' => true,
        'items' => $items
    ]);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], 500);
}