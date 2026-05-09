<?php
ob_start();
require_once __DIR__ . '/../config/db.php';

header('Content-Type: application/json; charset=utf-8');

function respond($arr, int $code = 200): void {
    if (ob_get_length()) {
        ob_clean();
    }
    http_response_code($code);
    echo json_encode($arr, JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    $pdo = db();

    $lat = isset($_GET['lat']) ? (float)$_GET['lat'] : null;
    $lng = isset($_GET['lng']) ? (float)$_GET['lng'] : null;
    $radiusKm = isset($_GET['radius']) ? (float)$_GET['radius'] : 5.0; // Default 5km
    $categoria = trim((string)($_GET['categoria'] ?? ''));

    if ($lat === null || $lng === null) {
        respond(['ok' => false, 'error' => 'Latitude e Longitude são obrigatórios.'], 400);
    }

    $limit = (int)($_GET['limit'] ?? 30);
    if ($limit < 1) $limit = 1;
    if ($limit > 100) $limit = 100;

    $where = [];
    $params = [];

    // Filtro por categoria (ex: "Pratos Tradicionais")
    if ($categoria !== '') {
        $where[] = "p.Categoria = :categoria";
        $params['categoria'] = $categoria;
    }

    $where[] = "p.Disponivel = 1";
    // Garantir que temos coordenadas válidas no restaurante
    $where[] = "r.Latitude IS NOT NULL AND r.Longitude IS NOT NULL";

    $whereSql = count($where) ? ('WHERE ' . implode(' AND ', $where)) : '';

    // Fórmula de Haversine para Sql Server
    // NOTA: SQL Server PDO não suporta reutilização de named params,
    // por isso usamos nomes únicos (:lat1, :lat2, etc.)
    $sql = "
        SELECT
            p.IdPrato AS id,
            p.NomePrato AS nome,
            p.Categoria AS categoria,
            p.Descricao AS descricao,
            CAST(p.Preco AS FLOAT) AS preco,
            p.ImagemUrl AS imagemUrl,
            r.IdRestaurante AS restauranteId,
            r.Nome AS restauranteNome,
            CAST(ISNULL(AVG(CAST(a.Classificacao AS FLOAT)), 0) AS FLOAT) AS ratingMedio,
            COUNT(a.IdAvaliacao) AS totalAvaliacoes,
            -- Cálculo da distância em Km
            (6371 * ACOS(
                COS(RADIANS(:lat1)) * COS(RADIANS(r.Latitude)) *
                COS(RADIANS(r.Longitude) - RADIANS(:lng1)) +
                SIN(RADIANS(:lat2)) * SIN(RADIANS(r.Latitude))
            )) AS distanciaKm
        FROM dbo.Prato p
        JOIN dbo.Restaurante r
            ON r.IdRestaurante = p.RestauranteId
        LEFT JOIN dbo.AvaliacaoPrato a
            ON a.PratoId = p.IdPrato
        $whereSql
        GROUP BY
            p.IdPrato,
            p.NomePrato,
            p.Categoria,
            p.Descricao,
            p.Preco,
            p.ImagemUrl,
            r.IdRestaurante,
            r.Nome,
            r.Latitude,
            r.Longitude
        -- Filtrar pela distância e evitar Nans
        HAVING (6371 * ACOS(
                COS(RADIANS(:lat3)) * COS(RADIANS(r.Latitude)) *
                COS(RADIANS(r.Longitude) - RADIANS(:lng2)) +
                SIN(RADIANS(:lat4)) * SIN(RADIANS(r.Latitude))
            )) <= :radiusKm
        ORDER BY
            distanciaKm ASC,
            ratingMedio DESC
        OFFSET 0 ROWS
        FETCH NEXT :limit ROWS ONLY;
    ";

    $stmt = $pdo->prepare($sql);

    // Bind com nomes únicos para SQL Server
    $stmt->bindValue(':lat1', $lat);
    $stmt->bindValue(':lat2', $lat);
    $stmt->bindValue(':lat3', $lat);
    $stmt->bindValue(':lat4', $lat);
    $stmt->bindValue(':lng1', $lng);
    $stmt->bindValue(':lng2', $lng);
    $stmt->bindValue(':radiusKm', $radiusKm);
    $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);

    foreach ($params as $k => $v) {
        $stmt->bindValue(':' . $k, $v);
    }

    $stmt->execute();
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($rows as &$r) {
        $r['id'] = (int)$r['id'];
        $r['restauranteId'] = (int)$r['restauranteId'];
        $r['preco'] = $r['preco'] !== null ? (float)$r['preco'] : null;
        $r['ratingMedio'] = $r['ratingMedio'] !== null ? (float)$r['ratingMedio'] : 0.0;
        $r['totalAvaliacoes'] = (int)$r['totalAvaliacoes'];
        $r['distanciaKm'] = (float)$r['distanciaKm'];
    }
    unset($r);

    respond([
        'ok' => true,
        'count' => count($rows),
        'items' => $rows
    ]);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], 500);
}
