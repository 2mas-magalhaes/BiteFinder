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

    $pratoId = isset($_GET['pratoId']) ? (int)$_GET['pratoId'] : 0;

    if ($pratoId <= 0) {
        respond([
            'ok' => false,
            'error' => 'pratoId inválido'
        ], 400);
    }

    $sql = "
        SELECT
            a.IdAvaliacao AS idAvaliacao,
            a.UserId AS userId,
            u.Nome AS autorNome,
            a.Classificacao AS classificacao,
            a.Comentario AS comentario,
            CONVERT(varchar(19), a.CreatedAt, 120) AS createdAt,
            CONVERT(varchar(19), a.UpdatedAt, 120) AS updatedAt,
            r.IdResposta AS idResposta,
            r.Texto AS respostaTexto,
            CONVERT(varchar(19), r.CreatedAt, 120) AS respostaCreatedAt
        FROM dbo.AvaliacaoPrato a
        JOIN dbo.Users u
            ON u.IdUser = a.UserId
        LEFT JOIN dbo.RespostaAvaliacao r
            ON r.AvaliacaoId = a.IdAvaliacao
        WHERE a.PratoId = :pratoId
        ORDER BY a.CreatedAt DESC
    ";

    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':pratoId', $pratoId, PDO::PARAM_INT);
    $stmt->execute();

    $rows = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($rows as &$r) {
        $r['idAvaliacao'] = (int)$r['idAvaliacao'];
        $r['userId'] = (int)$r['userId'];
        $r['classificacao'] = (int)$r['classificacao'];
        $r['idResposta'] = $r['idResposta'] !== null ? (int)$r['idResposta'] : null;

        $r['autorNome'] = $r['autorNome'] !== null ? (string)$r['autorNome'] : null;
        $r['comentario'] = $r['comentario'] !== null ? (string)$r['comentario'] : null;
        $r['createdAt'] = $r['createdAt'] !== null ? (string)$r['createdAt'] : null;
        $r['updatedAt'] = $r['updatedAt'] !== null ? (string)$r['updatedAt'] : null;
        $r['respostaTexto'] = $r['respostaTexto'] !== null ? (string)$r['respostaTexto'] : null;
        $r['respostaCreatedAt'] = $r['respostaCreatedAt'] !== null ? (string)$r['respostaCreatedAt'] : null;
    }
    unset($r);

    respond([
        'ok' => true,
        'items' => $rows
    ]);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], 500);
}