<?php
ob_start();
require_once __DIR__ . '/../config/db.php';

header('Content-Type: application/json; charset=utf-8');

function respond(array $payload, int $code = 200): void {
    if (ob_get_length()) {
        ob_clean();
    }
    http_response_code($code);
    echo json_encode($payload, JSON_UNESCAPED_UNICODE);
    exit;
}

function readFloat(string $key): ?float {
    $value = $_GET[$key] ?? null;
    return is_numeric($value) ? (float)$value : null;
}

try {
    $pdo = db();

    $lat = readFloat('lat');
    $lng = readFloat('lng');
    $radiusKm = readFloat('radius') ?? 5.0;
    $categoria = trim((string)($_GET['categoria'] ?? ''));

    if ($lat === null || $lng === null || $lat < -90 || $lat > 90 || $lng < -180 || $lng > 180) {
        respond(['ok' => false, 'error' => 'Latitude e longitude validas sao obrigatorias.'], 400);
    }

    if ($radiusKm <= 0) $radiusKm = 5.0;
    if ($radiusKm > 50) $radiusKm = 50.0;

    $limit = (int)($_GET['limit'] ?? 30);
    if ($limit < 1) $limit = 1;
    if ($limit > 100) $limit = 100;

    $where = [
        'p.Disponivel = 1',
        'r.Latitude BETWEEN -90 AND 90',
        'r.Longitude BETWEEN -180 AND 180',
    ];
    $params = [];

    if ($categoria !== '') {
        $where[] = 'p.Categoria = :categoria';
        $params[':categoria'] = $categoria;
    }

    $whereSql = 'WHERE ' . implode(' AND ', $where);

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
            CAST(r.Latitude AS FLOAT) AS restauranteLatitude,
            CAST(r.Longitude AS FLOAT) AS restauranteLongitude,
            CAST(ISNULL(AVG(CAST(a.Classificacao AS FLOAT)), 0) AS FLOAT) AS ratingMedio,
            COUNT(a.IdAvaliacao) AS totalAvaliacoes,
            CAST(d.distanciaKm AS FLOAT) AS distanciaKm
        FROM dbo.Prato p
        JOIN dbo.Restaurante r
            ON r.IdRestaurante = p.RestauranteId
        LEFT JOIN dbo.AvaliacaoPrato a
            ON a.PratoId = p.IdPrato
        CROSS APPLY (
            SELECT geography::Point(:lat, :lng, 4326)
                .STDistance(geography::Point(r.Latitude, r.Longitude, 4326)) / 1000.0 AS distanciaKm
        ) d
        $whereSql
          AND d.distanciaKm <= :radiusKm
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
            r.Longitude,
            d.distanciaKm
        ORDER BY
            distanciaKm ASC,
            ratingMedio DESC,
            totalAvaliacoes DESC
        OFFSET 0 ROWS
        FETCH NEXT :limit ROWS ONLY;
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':lat', $lat);
    $stmt->bindValue(':lng', $lng);
    $stmt->bindValue(':radiusKm', $radiusKm);
    $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);

    foreach ($params as $name => $value) {
        $stmt->bindValue($name, $value);
    }

    $stmt->execute();
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($rows as &$row) {
        $row['id'] = (int)$row['id'];
        $row['restauranteId'] = (int)$row['restauranteId'];
        $row['preco'] = $row['preco'] !== null ? (float)$row['preco'] : null;
        $row['ratingMedio'] = $row['ratingMedio'] !== null ? (float)$row['ratingMedio'] : 0.0;
        $row['totalAvaliacoes'] = (int)$row['totalAvaliacoes'];
        $row['restauranteLatitude'] = $row['restauranteLatitude'] !== null ? (float)$row['restauranteLatitude'] : null;
        $row['restauranteLongitude'] = $row['restauranteLongitude'] !== null ? (float)$row['restauranteLongitude'] : null;
        $row['distanciaKm'] = round((float)$row['distanciaKm'], 2);
    }
    unset($row);

    respond([
        'ok' => true,
        'count' => count($rows),
        'items' => $rows,
    ]);
} catch (Throwable $e) {
    respond(['ok' => false, 'error' => 'Erro interno'], 500);
}
