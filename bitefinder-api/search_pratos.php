<?php
require_once __DIR__ . '/api/config/db.php';

header('Content-Type: application/json; charset=utf-8');

$q = isset($_GET['q']) ? trim((string)$_GET['q']) : '';

if ($q === '' || mb_strlen($q) < 2) {
    echo json_encode([]);
    exit;
}

try {
    $pdo = db();

    $sql = "
        SELECT
            MIN(p.IdPrato) AS id_prato,
            p.NomePrato AS nome_prato,
            COUNT(DISTINCT p.RestauranteId) AS n_restaurantes
        FROM dbo.Prato p
        WHERE p.Disponivel = 1
          AND p.NomePrato LIKE :term
        GROUP BY p.NomePrato
        ORDER BY
            CASE
                WHEN p.NomePrato LIKE :prefix THEN 0
                ELSE 1
            END,
            p.NomePrato ASC
        OFFSET 0 ROWS FETCH NEXT 8 ROWS ONLY
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        'term' => '%' . $q . '%',
        'prefix' => $q . '%'
    ]);

    $resultados = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($resultados as &$r) {
        $r['id_prato'] = (int)$r['id_prato'];
        $r['nome_prato'] = (string)$r['nome_prato'];
        $r['n_restaurantes'] = (int)$r['n_restaurantes'];
    }
    unset($r);

    echo json_encode($resultados, JSON_UNESCAPED_UNICODE);
} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], JSON_UNESCAPED_UNICODE);
}