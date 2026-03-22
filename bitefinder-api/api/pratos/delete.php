<?php
ob_start();
require_once __DIR__ . '/../config/db.php';
header('Content-Type: application/json; charset=utf-8');

function respond($arr, int $code = 200): void {
    if (ob_get_length()) ob_clean();
    http_response_code($code);
    echo json_encode($arr, JSON_UNESCAPED_UNICODE);
    exit;
}

try {
    require_once __DIR__ . '/../middleware/auth_jwt.php';
    $jwtPayload = require_jwt();

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        respond(['ok' => false, 'error' => 'Método inválido'], 405);
    }

    $raw = file_get_contents('php://input');
    $body = json_decode($raw, true);
    if (!is_array($body)) {
        respond(['ok' => false, 'error' => 'JSON inválido'], 400);
    }

    $idPrato = (int)($body['idPrato'] ?? 0);
    $userId = (int)($body['userId'] ?? 0);
    $restauranteId = (int)($body['restauranteId'] ?? 0);
    if (isset($jwtPayload['sub'])) {
        $userId = (int)$jwtPayload['sub'];
        if (!empty($jwtPayload['restaurantes'][0])) {
            $restauranteId = (int)$jwtPayload['restaurantes'][0];
        }
    }

    if ($idPrato <= 0 || $userId <= 0 || $restauranteId <= 0) {
        respond(['ok' => false, 'error' => 'Dados insuficientes para remover prato'], 422);
    }

    $pdo = db();
    $stmt = $pdo->prepare('SELECT 1 FROM dbo.RestauranteUser WHERE UserId = :userId AND RestauranteId = :restauranteId');
    $stmt->execute(['userId' => $userId, 'restauranteId' => $restauranteId]);
    $owner = $stmt->fetchColumn();
    if (!$owner) {
        respond(['ok' => false, 'error' => 'Acesso negado. Utilizador não é proprietário deste restaurante.'], 403);
    }

    $sql = 'DELETE FROM dbo.Prato WHERE IdPrato = :idPrato AND RestauranteId = :restauranteId';
    $stmt = $pdo->prepare($sql);
    $stmt->bindValue(':idPrato', $idPrato, PDO::PARAM_INT);
    $stmt->bindValue(':restauranteId', $restauranteId, PDO::PARAM_INT);
    $stmt->execute();

    respond(['ok' => true, 'message' => 'Prato removido com sucesso']);

} catch (Throwable $e) {
    respond([
        'ok' => false,
        'error' => 'Erro interno',
        'message' => $e->getMessage(),
        'file' => $e->getFile(),
        'line' => $e->getLine()
    ], 500);
}
