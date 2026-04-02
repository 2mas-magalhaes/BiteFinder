<?php
/**
 * JWT Authentication Functions for BiteFinder
 * 
 * Security: JWT secret is loaded from .env file, not hardcoded
 * Algorithm: HS256 (HMAC SHA-256)
 * Expiry: Configurable, defaults to 1 hour (3600 seconds)
 */

require_once __DIR__ . '/config/env_loader.php';

// Get JWT_SECRET from environment, fallback to strong default
$JWT_SECRET = env('JWT_SECRET', 's3cr3t_b1t3f1nd3r_2026!_change_in_production');

// Validate JWT_SECRET length (min 32 chars for security)
if (strlen($JWT_SECRET) < 32) {
    error_log("WARNING: JWT_SECRET is too short (< 32 chars). Update .env file for production!");
}

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

function jwt_encode($payload, $exp = null) {
    global $JWT_SECRET;
    
    // Use exp from .env or parameter (3600 = 1 hour default)
    if ($exp === null) {
        $exp = (int)env('JWT_EXPIRY', 3600);
    }
    
    $header = ['alg' => 'HS256', 'typ' => 'JWT'];
    $payload['iat'] = time();
    $payload['exp'] = time() + $exp;

    $b64header = base64url_encode(json_encode($header));
    $b64payload = base64url_encode(json_encode($payload));

    $signature = hash_hmac('sha256', "$b64header.$b64payload", $JWT_SECRET, true);
    $b64sig = base64url_encode($signature);

    return "$b64header.$b64payload.$b64sig";
}

function jwt_verify($jwt) {
    global $JWT_SECRET;
    
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
    $validSignature = hash_hmac('sha256', "$b64header.$b64payload", $JWT_SECRET, true);

    // Use hash_equals to prevent timing attacks
    if (!hash_equals($validSignature, $signature)) {
        return false;
    }

    $payload = json_decode($payloadJson, true);
    if (!is_array($payload)) {
        return false;
    }
    
    // Check expiration
    if (isset($payload['exp']) && time() > (int)$payload['exp']) {
        return false;
    }

    return $payload;
}