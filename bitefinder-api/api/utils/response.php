<?php
function json_response($data, int $status = 200): void {
    http_response_code($status);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($data, JSON_UNESCAPED_UNICODE);
    exit;
}

function json_error(string $message, int $status = 400, $extra = null): void {
    $payload = ['ok' => false, 'error' => $message];
    if ($extra !== null) $payload['details'] = $extra;
    json_response($payload, $status);
}
