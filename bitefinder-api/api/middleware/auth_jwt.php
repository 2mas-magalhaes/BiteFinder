<?php
require_once __DIR__ . '/../jwt_functions.php';

function getAuthorizationHeader() {
    if (isset($_SERVER['HTTP_AUTHORIZATION'])) {
        return trim($_SERVER['HTTP_AUTHORIZATION']);
    }
    if (isset($_SERVER['REDIRECT_HTTP_AUTHORIZATION'])) {
        return trim($_SERVER['REDIRECT_HTTP_AUTHORIZATION']);
    }
    $headers = apache_request_headers() ?? [];
    foreach ($headers as $name => $value) {
        if (strcasecmp($name, 'Authorization') == 0) {
            return trim($value);
        }
    }
    return null;
}

function require_jwt() {
    $header = getAuthorizationHeader();
    if (!$header || !preg_match('/Bearer\s+(.*)$/i', $header, $matches)) {
        http_response_code(401);
        echo json_encode(['ok' => false, 'error' => 'Token ausente']);
        exit;
    }
    $payload = jwt_verify($matches[1]);
    if (!$payload) {
        http_response_code(401);
        echo json_encode(['ok' => false, 'error' => 'Token inválido ou expirado']);
        exit;
    }
    return $payload;
}
