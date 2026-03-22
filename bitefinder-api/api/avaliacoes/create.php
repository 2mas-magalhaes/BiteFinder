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

    $pratoId = isset($body['pratoId']) ? (int)$body['pratoId'] : 0;
    $userId = isset($body['userId']) ? (int)$body['userId'] : 0;
    $classificacao = isset($body['classificacao']) ? (int)$body['classificacao'] : 0;
    $comentario = trim((string)($body['comentario'] ?? ''));

    if ($pratoId <= 0) {
        respond(['ok' => false, 'error' => 'pratoId inválido'], 400);
    }

    if ($userId <= 0) {
        respond(['ok' => false, 'error' => 'userId inválido'], 400);
    }

    if ($classificacao < 1 || $classificacao > 5) {
        respond(['ok' => false, 'error' => 'A classificação tem de estar entre 1 e 5'], 400);
    }

    if ($comentario === '') {
        respond(['ok' => false, 'error' => 'O comentário é obrigatório'], 400);
    }

    // Validar utilizador
    $stmtUser = $pdo->prepare("
        SELECT IdUser, Role
        FROM dbo.Users
        WHERE IdUser = :userId
    ");
    $stmtUser->execute(['userId' => $userId]);
    $user = $stmtUser->fetch(PDO::FETCH_ASSOC);

    if (!$user) {
        respond(['ok' => false, 'error' => 'Utilizador não encontrado'], 404);
    }

    // Regra de negócio: restaurante não avalia pratos
    if (($user['Role'] ?? '') !== 'cliente') {
        respond(['ok' => false, 'error' => 'Apenas clientes podem avaliar pratos'], 403);
    }

    // Validar prato
    $stmtPrato = $pdo->prepare("
        SELECT IdPrato
        FROM dbo.Prato
        WHERE IdPrato = :pratoId
          AND Disponivel = 1
    ");
    $stmtPrato->execute(['pratoId' => $pratoId]);
    $prato = $stmtPrato->fetch(PDO::FETCH_ASSOC);

    if (!$prato) {
        respond(['ok' => false, 'error' => 'Prato não encontrado ou indisponível'], 404);
    }

    // Ver se já existe avaliação deste user para este prato
    $stmtCheck = $pdo->prepare("
        SELECT IdAvaliacao
        FROM dbo.AvaliacaoPrato
        WHERE UserId = :userId
          AND PratoId = :pratoId
    ");
    $stmtCheck->execute([
        'userId' => $userId,
        'pratoId' => $pratoId
    ]);
    $existing = $stmtCheck->fetch(PDO::FETCH_ASSOC);

    if ($existing) {
        // Atualizar = editar comentário/opinião do autor
        $stmtUpdate = $pdo->prepare("
            UPDATE dbo.AvaliacaoPrato
            SET
                Classificacao = :classificacao,
                Comentario = :comentario,
                UpdatedAt = SYSUTCDATETIME()
            WHERE IdAvaliacao = :idAvaliacao
        ");
        $stmtUpdate->execute([
            'classificacao' => $classificacao,
            'comentario' => $comentario,
            'idAvaliacao' => (int)$existing['IdAvaliacao']
        ]);

        respond([
            'ok' => true,
            'message' => 'Avaliação atualizada com sucesso',
            'idAvaliacao' => (int)$existing['IdAvaliacao'],
            'mode' => 'update'
        ]);
    }

    // Criar nova avaliação
    $stmtInsert = $pdo->prepare("
        INSERT INTO dbo.AvaliacaoPrato (
            UserId,
            PratoId,
            Classificacao,
            Comentario,
            CreatedAt,
            UpdatedAt
        )
        VALUES (
            :userId,
            :pratoId,
            :classificacao,
            :comentario,
            SYSUTCDATETIME(),
            SYSUTCDATETIME()
        )
    ");
    $stmtInsert->execute([
        'userId' => $userId,
        'pratoId' => $pratoId,
        'classificacao' => $classificacao,
        'comentario' => $comentario
    ]);

    $idAvaliacao = (int)$pdo->lastInsertId();

    respond([
        'ok' => true,
        'message' => 'Avaliação criada com sucesso',
        'idAvaliacao' => $idAvaliacao,
        'mode' => 'insert'
    ], 201);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage()
    ], 500);
}