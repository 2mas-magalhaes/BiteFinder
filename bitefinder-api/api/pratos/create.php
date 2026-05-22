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
    $restauranteId = (int)$owner['restauranteId'];

    $nome = trim((string)($body['nome'] ?? ''));
    $descricao = trim((string)($body['descricao'] ?? ''));
    $categoria = trim((string)($body['categoria'] ?? ''));
    $preco = isset($body['preco']) && is_numeric($body['preco']) ? (float)$body['preco'] : null;
    $imagemUrl = trim((string)($body['imagemUrl'] ?? ''));

    if ($nome === '') {
        pratos_respond(['ok' => false, 'error' => 'O nome do prato é obrigatório'], 422);
    }
    if ($preco !== null && $preco < 0) {
        pratos_respond(['ok' => false, 'error' => 'O preço não pode ser negativo'], 422);
    }

    $sql = '
        INSERT INTO dbo.Prato (
            NomePrato,
            Categoria,
            Descricao,
            Preco,
            ImagemUrl,
            RestauranteId,
            Disponivel
        )
        OUTPUT INSERTED.IdPrato
        VALUES (
            :nome,
            :categoria,
            :descricao,
            :preco,
            :imagemUrl,
            :restauranteId,
            1
        )
    ';

    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':nome', $nome);
    $stmt->bindValue(':categoria', $categoria !== '' ? $categoria : null, PDO::PARAM_STR);
    $stmt->bindValue(':descricao', $descricao !== '' ? $descricao : null, PDO::PARAM_STR);
    $stmt->bindValue(':preco', $preco !== null ? $preco : null, PDO::PARAM_STR);
    $stmt->bindValue(':imagemUrl', $imagemUrl !== '' ? $imagemUrl : null, PDO::PARAM_STR);
    $stmt->bindValue(':restauranteId', $restauranteId, PDO::PARAM_INT);
    $stmt->execute();
    $idPrato = (int)$stmt->fetchColumn();

    $item = pratos_fetch_item($pdo, $idPrato, true);
    pratos_respond([
        'ok' => true,
        'message' => 'Prato criado com sucesso',
        'idPrato' => $idPrato,
        'item' => $item,
    ], 201);

} catch (Throwable $e) {
    pratos_respond(['ok' => false, 'error' => 'Erro interno', 'message' => $e->getMessage()], 500);
}
