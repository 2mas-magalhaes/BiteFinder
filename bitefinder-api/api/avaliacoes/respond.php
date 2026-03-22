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
    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        respond([
            'ok' => false,
            'error' => 'Método inválido'
        ], 405);
    }

    $pdo = db();

    $raw = file_get_contents('php://input');
    $body = json_decode($raw, true);

    if (!is_array($body)) {
        respond([
            'ok' => false,
            'error' => 'JSON inválido'
        ], 400);
    }

    $idAvaliacao = isset($body['idAvaliacao']) ? (int)$body['idAvaliacao'] : 0;
    $userId = isset($body['userId']) ? (int)$body['userId'] : 0;
    $texto = trim((string)($body['texto'] ?? ''));

    if ($idAvaliacao <= 0) {
        respond(['ok' => false, 'error' => 'idAvaliacao inválido'], 400);
    }

    if ($userId <= 0) {
        respond(['ok' => false, 'error' => 'userId inválido'], 400);
    }

    if ($texto === '') {
        respond(['ok' => false, 'error' => 'O texto da resposta é obrigatório'], 400);
    }

    if (strlen($texto) > 1200) {
        respond(['ok' => false, 'error' => 'A resposta excede o limite de 1200 caracteres'], 400);
    }

    $stmtUser = $pdo->prepare("SELECT IdUser, Role FROM dbo.Users WHERE IdUser = :userId");
    $stmtUser->execute(['userId' => $userId]);
    $user = $stmtUser->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        respond(['ok' => false, 'error' => 'Utilizador não encontrado'], 404);
    }

    if (($user['Role'] ?? '') !== 'restaurante') {
        respond(['ok' => false, 'error' => 'Apenas utilizadores de restaurante podem responder'], 403);
    }

    $stmtOwnership = $pdo->prepare(" 
        SELECT TOP 1
            a.IdAvaliacao AS idAvaliacao,
            p.RestauranteId AS restauranteId
        FROM dbo.AvaliacaoPrato a
        JOIN dbo.Prato p
            ON p.IdPrato = a.PratoId
        JOIN dbo.RestauranteUser ru
            ON ru.RestauranteId = p.RestauranteId
        WHERE a.IdAvaliacao = :idAvaliacao
          AND ru.UserId = :userId
    ");
    $stmtOwnership->execute([
        'idAvaliacao' => $idAvaliacao,
        'userId' => $userId
    ]);
    $ownership = $stmtOwnership->fetch(PDO::FETCH_ASSOC);

    if (!$ownership) {
        respond(['ok' => false, 'error' => 'Não tens permissões para responder a esta avaliação'], 403);
    }

    $stmtExisting = $pdo->prepare("SELECT IdResposta FROM dbo.RespostaAvaliacao WHERE AvaliacaoId = :idAvaliacao");
    $stmtExisting->execute(['idAvaliacao' => $idAvaliacao]);
    $existing = $stmtExisting->fetch(PDO::FETCH_ASSOC);

    if ($existing) {
        $stmtUpdate = $pdo->prepare(" 
            UPDATE dbo.RespostaAvaliacao
            SET Texto = :texto
            WHERE IdResposta = :idResposta
        ");
        $stmtUpdate->execute([
            'texto' => $texto,
            'idResposta' => (int)$existing['IdResposta']
        ]);

        respond([
            'ok' => true,
            'message' => 'Resposta atualizada com sucesso',
            'idResposta' => (int)$existing['IdResposta'],
            'mode' => 'update'
        ]);
    }

    $stmtInsert = $pdo->prepare(" 
        INSERT INTO dbo.RespostaAvaliacao (
            AvaliacaoId,
            Texto,
            CreatedAt
        )
        VALUES (
            :idAvaliacao,
            :texto,
            SYSUTCDATETIME()
        )
    ");
    $stmtInsert->execute([
        'idAvaliacao' => $idAvaliacao,
        'texto' => $texto
    ]);

    respond([
        'ok' => true,
        'message' => 'Resposta criada com sucesso',
        'mode' => 'insert'
    ], 201);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], 500);
}
