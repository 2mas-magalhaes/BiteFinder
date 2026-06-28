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

    $search = trim((string)($_GET['search'] ?? ''));
    $categoria = trim((string)($_GET['categoria'] ?? ''));
    $restauranteId = (string)($_GET['restauranteId'] ?? '');

    $limit = (int)($_GET['limit'] ?? 30);
    $offset = (int)($_GET['offset'] ?? 0);

    if ($limit < 1) $limit = 1;
    if ($limit > 100) $limit = 100;
    if ($offset < 0) $offset = 0;

    $where = [];
    $params = [];

    // Pesquisa só por nome do prato
    if ($search !== '') {
        $where[] = "p.NomePrato LIKE :search";
        $params['search'] = '%' . $search . '%';
    }

    // Filtro por categoria
    if ($categoria !== '') {
        $where[] = "p.Categoria = :categoria";
        $params['categoria'] = $categoria;
    }

    // Filtro por restaurante
    if ($restauranteId !== '' && ctype_digit($restauranteId)) {
        $where[] = "p.RestauranteId = :restauranteId";
        $params['restauranteId'] = (int)$restauranteId;
    }

    // Só pratos disponíveis
    $where[] = "p.Disponivel = 1";

    $whereSql = count($where) ? ('WHERE ' . implode(' AND ', $where)) : '';

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
            COUNT(a.IdAvaliacao) AS totalAvaliacoes
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
            r.Nome
        ORDER BY
            totalAvaliacoes DESC,
            ratingMedio DESC,
            p.NomePrato ASC
        OFFSET :offset ROWS
        FETCH NEXT :limit ROWS ONLY;
    ";

    $stmt = $pdo->prepare($sql);

    foreach ($params as $k => $v) {
        $stmt->bindValue(':' . $k, $v);
    }

    $stmt->bindValue(':offset', $offset, PDO::PARAM_INT);
    $stmt->bindValue(':limit', $limit, PDO::PARAM_INT);

    $stmt->execute();
    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Converter tipos para JSON correto para Android/Kotlin
    foreach ($rows as &$r) {
        $r['id'] = (int)$r['id'];
        $r['restauranteId'] = (int)$r['restauranteId'];
        $r['preco'] = $r['preco'] !== null ? (float)$r['preco'] : null;
        $r['ratingMedio'] = $r['ratingMedio'] !== null ? (float)$r['ratingMedio'] : 0.0;
        $r['totalAvaliacoes'] = (int)$r['totalAvaliacoes'];

        $r['nome'] = (string)$r['nome'];
        $r['categoria'] = $r['categoria'] !== null ? (string)$r['categoria'] : null;
        $r['descricao'] = $r['descricao'] !== null ? (string)$r['descricao'] : null;
        $r['imagemUrl'] = $r['imagemUrl'] !== null ? (string)$r['imagemUrl'] : null;
        $r['restauranteNome'] = (string)$r['restauranteNome'];
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
        'message' => 'Ocorreu um erro no servidor.'
    ], 500);
}