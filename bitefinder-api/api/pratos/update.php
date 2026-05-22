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
    $nome = trim((string)($body['nome'] ?? ''));
    $descricao = trim((string)($body['descricao'] ?? ''));
    $categoria = trim((string)($body['categoria'] ?? ''));
    $preco = isset($body['preco']) && is_numeric($body['preco']) ? (float)$body['preco'] : null;
    $imagemUrl = trim((string)($body['imagemUrl'] ?? ''));

    if ($idPrato <= 0 || $nome === '') {
        pratos_respond(['ok' => false, 'error' => 'Dados insuficientes para atualizar prato'], 422);
    }
    if ($preco !== null && $preco < 0) {
        pratos_respond(['ok' => false, 'error' => 'O preço não pode ser negativo'], 422);
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
        SET
            NomePrato = :nome,
            Descricao = :descricao,
            Categoria = :categoria,
            Preco = :preco,
            ImagemUrl = :imagemUrl
        WHERE IdPrato = :idPrato
          AND RestauranteId = :restauranteId
          AND Disponivel = 1
    ';
    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':nome', $nome);
    $stmt->bindValue(':descricao', $descricao !== '' ? $descricao : null, PDO::PARAM_STR);
    $stmt->bindValue(':categoria', $categoria !== '' ? $categoria : null, PDO::PARAM_STR);
    $stmt->bindValue(':preco', $preco !== null ? $preco : null, PDO::PARAM_STR);
    $stmt->bindValue(':imagemUrl', $imagemUrl !== '' ? $imagemUrl : null, PDO::PARAM_STR);
    $stmt->bindValue(':idPrato', $idPrato, PDO::PARAM_INT);
    $stmt->bindValue(':restauranteId', $restauranteId, PDO::PARAM_INT);
    $stmt->execute();

    $item = pratos_fetch_item($pdo, $idPrato, true);
    pratos_respond([
        'ok' => true,
        'message' => $stmt->rowCount() > 0 ? 'Prato atualizado com sucesso' : 'Prato guardado sem alterações',
        'item' => $item,
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
