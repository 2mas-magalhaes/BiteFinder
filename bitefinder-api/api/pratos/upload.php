<?php
ob_start();
require_once __DIR__ . '/../config/db.php';
require_once __DIR__ . '/../middleware/auth_jwt.php';

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
    $payload = require_jwt();

    if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
        respond(['ok' => false, 'error' => 'Método inválido'], 405);
    }

    if (!isset($_FILES['image']) || $_FILES['image']['error'] !== UPLOAD_ERR_OK) {
        respond(['ok' => false, 'error' => 'Upload inválido'], 400);
    }

    $file = $_FILES['image'];
    $ext = pathinfo($file['name'], PATHINFO_EXTENSION);
    $allowed = ['jpg','jpeg','png','gif'];

    if (!in_array(strtolower($ext), $allowed, true)) {
        respond(['ok' => false, 'error' => 'Tipo de ficheiro inválido'], 422);
    }

    $dir = __DIR__ . '/../../uploads';
    if (!is_dir($dir)) {
        mkdir($dir, 0777, true);
    }

    $name = 'img_' . time() . '_' . bin2hex(random_bytes(5)) . '.' . $ext;
    $path = $dir . '/' . $name;

    if (!move_uploaded_file($file['tmp_name'], $path)) {
        respond(['ok' => false, 'error' => 'Falha a gravar ficheiro'], 500);
    }

    $url = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? 'https' : 'http')
        . '://' . $_SERVER['HTTP_HOST'] . '/uploads/' . $name;

    respond(['ok' => true, 'url' => $url]);
} catch (Throwable $e) {
    respond(['ok' => false, 'error' => 'Erro interno', 'message' => $e->getMessage()], 500);
}
