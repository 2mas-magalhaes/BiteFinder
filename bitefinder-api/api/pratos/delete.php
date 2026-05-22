<?php
ob_start();
require_once __DIR__ . '/_common.php';
header('Content-Type: application/json; charset=utf-8');

try {
    require_once __DIR__ . '/../middleware/auth_jwt.php';
    $jwtPayload = require_jwt();

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        pratos_respond(['ok' => false, 'error' => 'Método inválido'], 405);
    }

    $body = pratos_read_json_body();
    $pdo = db();
    $owner = pratos_require_owner_context($pdo, $jwtPayload, $body);

    $idPrato = (int)($body['idPrato'] ?? 0);
    $restauranteId = (int)$owner['restauranteId'];

    if ($idPrato <= 0) {
        pratos_respond(['ok' => false, 'error' => 'Dados insuficientes para remover prato'], 422);
    }

    $existsStmt = $pdo->prepare('
        SELECT TOP 1 IdPrato
        FROM dbo.Prato
        WHERE IdPrato = :idPrato
          AND RestauranteId = :restauranteId
          AND Disponivel = 1
    ');
    $existsStmt->execute([
        'idPrato' => $idPrato,
        'restauranteId' => $restauranteId,
    ]);
    if (!$existsStmt->fetchColumn()) {
        pratos_respond(['ok' => false, 'error' => 'Prato não encontrado'], 404);
    }

    $sql = '
        UPDATE dbo.Prato
        SET Disponivel = 0
        WHERE IdPrato = :idPrato
          AND RestauranteId = :restauranteId
          AND Disponivel = 1
    ';
    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':idPrato', $idPrato, PDO::PARAM_INT);
    $stmt->bindValue(':restauranteId', $restauranteId, PDO::PARAM_INT);
    $stmt->execute();

    pratos_respond([
        'ok' => true,
        'message' => 'Prato removido com sucesso',
        'idPrato' => $idPrato,
    ]);

} catch (Throwable $e) {
    pratos_respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ], 500);
}
