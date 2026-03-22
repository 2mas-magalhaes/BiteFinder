<?php
// Simple JWT functions for Jelly

const JWT_SECRET = 's3cr3t_b1t3f1nd3r_2026!';

function base64url_encode($data) {
    return rtrim(strtr(base64_encode($data), '+/', '-_'), '=');
}

function base64url_decode($data) {
    $pad = 4 - (strlen($data) % 4);
    if ($pad < 4) {
        $data .= str_repeat('=', $pad);
    }
    return base64_decode(strtr($data, '-_', '+/'));
}

function jwt_encode($payload, $exp = 3600) {
    $header = ['alg' => 'HS256', 'typ' => 'JWT'];
    $payload['iat'] = time();
    $payload['exp'] = time() + $exp;

    $b64header = base64url_encode(json_encode($header));
    $b64payload = base64url_encode(json_encode($payload));

    $signature = hash_hmac('sha256', "$b64header.$b64payload", JWT_SECRET, true);
    $b64sig = base64url_encode($signature);

    return "$b64header.$b64payload.$b64sig";
}

function jwt_verify($jwt) {
    $parts = explode('.', $jwt);
    if (count($parts) !== 3) {
        return false;
    }

    list($b64header, $b64payload, $b64sig) = $parts;
    $headerJson = base64url_decode($b64header);
    $payloadJson = base64url_decode($b64payload);

    if ($headerJson === false || $payloadJson === false) {
        return false;
    }

    $signature = base64url_decode($b64sig);
    $validSignature = hash_hmac('sha256', "$b64header.$b64payload", JWT_SECRET, true);

    if (!hash_equals($validSignature, $signature)) {
        return false;
    }

    $payload = json_decode($payloadJson, true);
    if (!is_array($payload)) {
        return false;
    }
    if (isset($payload['exp']) && time() > (int)$payload['exp']) {
        return false;
    }

    return $payload;
}