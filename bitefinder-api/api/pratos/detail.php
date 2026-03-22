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

    $id = isset($_GET['id']) ? (int)$_GET['id'] : 0;
    if ($id <= 0) {
        respond([
            'ok' => false,
            'error' => 'Id do prato inválido'
        ], 400);
    }

    $sql = "
        SELECT
            p.IdPrato AS id,
            p.NomePrato AS nome,
            p.Categoria AS categoria,
            p.Descricao AS descricao,
            CAST(p.Preco AS FLOAT) AS preco,
            p.ImagemUrl AS imagemUrl,
            p.Disponivel AS disponivel,
            r.IdRestaurante AS restauranteId,
            r.Nome AS restauranteNome,
            r.Morada AS restauranteMorada,
            r.Latitude AS restauranteLatitude,
            r.Longitude AS restauranteLongitude,
            CAST(ISNULL(AVG(CAST(a.Classificacao AS FLOAT)), 0) AS FLOAT) AS ratingMedio,
            COUNT(a.IdAvaliacao) AS totalAvaliacoes
        FROM dbo.Prato p
        JOIN dbo.Restaurante r
            ON r.IdRestaurante = p.RestauranteId
        LEFT JOIN dbo.AvaliacaoPrato a
            ON a.PratoId = p.IdPrato
        WHERE p.IdPrato = :id
        GROUP BY
            p.IdPrato,
            p.NomePrato,
            p.Categoria,
            p.Descricao,
            p.Preco,
            p.ImagemUrl,
            p.Disponivel,
            r.IdRestaurante,
            r.Nome,
            r.Morada,
            r.Latitude,
            r.Longitude
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':id', $id, PDO::PARAM_INT);
    $stmt->execute();

    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$row) {
        respond([
            'ok' => false,
            'error' => 'Prato não encontrado'
        ], 404);
    }

    $row['id'] = (int)$row['id'];
    $row['restauranteId'] = (int)$row['restauranteId'];
    $row['preco'] = $row['preco'] !== null ? (float)$row['preco'] : null;
    $row['ratingMedio'] = $row['ratingMedio'] !== null ? (float)$row['ratingMedio'] : 0.0;
    $row['totalAvaliacoes'] = (int)$row['totalAvaliacoes'];
    $row['disponivel'] = (bool)$row['disponivel'];
    $row['restauranteLatitude'] = $row['restauranteLatitude'] !== null ? (float)$row['restauranteLatitude'] : null;
    $row['restauranteLongitude'] = $row['restauranteLongitude'] !== null ? (float)$row['restauranteLongitude'] : null;

    respond([
        'ok' => true,
        'item' => $row
    ]);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], 500);
}