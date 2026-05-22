<?php
require_once __DIR__ . '/../config/db.php';

function pratos_respond(array $payload, int $code = 200): void {
    if (ob_get_length()) {
        ob_clean();
    }
    http_response_code($code);
    echo json_encode($payload, JSON_UNESCAPED_UNICODE);
    exit;
}

function pratos_read_json_body(): array {
    $raw = file_get_contents('php://input');
    $body = json_decode($raw, true);

    if (!is_array($body)) {
        pratos_respond(['ok' => false, 'error' => 'JSON inválido'], 400);
    }

    return $body;
}

function pratos_require_owner_context(PDO $pdo, array $jwtPayload, array $body): array {
    $userId = (int)($body['userId'] ?? 0);
    $restauranteId = (int)($body['restauranteId'] ?? 0);

    if (isset($jwtPayload['sub'])) {
        $userId = (int)$jwtPayload['sub'];
    }

    $jwtRestaurantes = array_values(array_map('intval', $jwtPayload['restaurantes'] ?? []));
    if (!empty($jwtRestaurantes)) {
        if ($restauranteId > 0) {
            if (!in_array($restauranteId, $jwtRestaurantes, true)) {
                pratos_respond(['ok' => false, 'error' => 'Restaurante inválido para este utilizador'], 403);
            }
        } else {
            $restauranteId = $jwtRestaurantes[0];
        }
    }

    if ($userId <= 0 || $restauranteId <= 0) {
        pratos_respond(['ok' => false, 'error' => 'Utilizador ou restaurante inválido'], 422);
    }

    $stmt = $pdo->prepare('
        SELECT 1
        FROM dbo.RestauranteUser
        WHERE UserId = :userId
          AND RestauranteId = :restauranteId
    ');
    $stmt->execute([
        'userId' => $userId,
        'restauranteId' => $restauranteId,
    ]);

    if (!$stmt->fetchColumn()) {
        pratos_respond(['ok' => false, 'error' => 'Acesso negado'], 403);
    }

    return [
        'userId' => $userId,
        'restauranteId' => $restauranteId,
    ];
}

function pratos_fetch_item(PDO $pdo, int $idPrato, bool $includeUnavailable = false): ?array {
    $availabilitySql = $includeUnavailable ? '' : 'AND p.Disponivel = 1';

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
            CAST(r.Latitude AS FLOAT) AS restauranteLatitude,
            CAST(r.Longitude AS FLOAT) AS restauranteLongitude,
            CAST(ISNULL(AVG(CAST(a.Classificacao AS FLOAT)), 0) AS FLOAT) AS ratingMedio,
            COUNT(a.IdAvaliacao) AS totalAvaliacoes
        FROM dbo.Prato p
        JOIN dbo.Restaurante r
            ON r.IdRestaurante = p.RestauranteId
        LEFT JOIN dbo.AvaliacaoPrato a
            ON a.PratoId = p.IdPrato
        WHERE p.IdPrato = :idPrato
          $availabilitySql
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
    $stmt->bindValue(':idPrato', $idPrato, PDO::PARAM_INT);
    $stmt->execute();

    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    if (!$row) {
        return null;
    }

    return pratos_normalize_item($row);
}

function pratos_normalize_item(array $row): array {
    $row['id'] = (int)$row['id'];
    $row['restauranteId'] = (int)$row['restauranteId'];
    $row['preco'] = $row['preco'] !== null ? (float)$row['preco'] : null;
    $row['disponivel'] = (bool)$row['disponivel'];
    $row['ratingMedio'] = $row['ratingMedio'] !== null ? (float)$row['ratingMedio'] : 0.0;
    $row['totalAvaliacoes'] = (int)$row['totalAvaliacoes'];
    $row['restauranteLatitude'] = $row['restauranteLatitude'] !== null ? (float)$row['restauranteLatitude'] : null;
    $row['restauranteLongitude'] = $row['restauranteLongitude'] !== null ? (float)$row['restauranteLongitude'] : null;
    $row['nome'] = (string)$row['nome'];
    $row['restauranteNome'] = (string)$row['restauranteNome'];
    $row['categoria'] = $row['categoria'] !== null ? (string)$row['categoria'] : null;
    $row['descricao'] = $row['descricao'] !== null ? (string)$row['descricao'] : null;
    $row['imagemUrl'] = $row['imagemUrl'] !== null ? (string)$row['imagemUrl'] : null;
    $row['restauranteMorada'] = $row['restauranteMorada'] !== null ? (string)$row['restauranteMorada'] : null;

    return $row;
}
