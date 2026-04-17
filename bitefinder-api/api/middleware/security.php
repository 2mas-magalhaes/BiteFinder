<?php
/**
 * BiteFinder Global Security Middleware
 * Applied to all endpoints for consistency
 * 
 * - CSRF validation for state-changing requests
 * - Security headers (HSTS, CSP, X-Frame-Options)
 * - Content-Type validation
 */

require_once __DIR__ . '/csrf.php';

/**
 * Initialize security middleware
 * Call at start of every endpoint
 */
function init_security_headers(): void
{
    // Start session (needed for CSRF tokens)
    if (session_status() === PHP_SESSION_NONE) {
        session_start();
        
        // Re-generate session ID on each request (security best practice)
        session_regenerate_id(true);
    }

    // Set security headers
    header('X-Content-Type-Options: nosniff');                  // Prevent MIME sniffing
    header('X-Frame-Options: DENY');                             // Prevent clickjacking
    header('X-XSS-Protection: 1; mode=block');                   // XSS protection
    header('Strict-Transport-Security: max-age=31536000; includeSubDomains'); // HSTS
    header('Content-Security-Policy: default-src \'self\'');     // CSP
    header('Referrer-Policy: strict-origin-when-cross-origin');  // Referrer policy
    
    // JSON API headers
    header('Content-Type: application/json; charset=utf-8');
    header('Access-Control-Allow-Origin: *');                   // TODO: Configure for production
    header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, X-CSRF-Token, Authorization');

    // Handle CORS preflight
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        http_response_code(200);
        exit;
    }
}

/**
 * Validate request content type
 * Ensure request body is valid JSON
 */
function validate_request_content_type(): void
{
    $method = strtoupper($_SERVER['REQUEST_METHOD'] ?? 'GET');
    
    // Skip validation for GET requests
    if ($method === 'GET') {
        return;
    }

    $content_type = $_SERVER['CONTENT_TYPE'] ?? '';
    
    // JSON endpoints expect application/json
    if (strpos($content_type, 'application/json') === false && 
        strpos($content_type, 'application/x-www-form-urlencoded') === false) {
        
        http_response_code(415);
        echo json_encode([
            'ok' => false,
            'error' => 'Unsupported Media Type',
            'message' => 'Content-Type must be application/json'
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
}

/**
 * Rate limiting stub
 * TODO: Implement with Redis for production
 */
function check_rate_limit(): void
{
    // For now, just a placeholder
    // Production: Use Redis to track requests per IP per minute
    // Example: 100 requests per minute per IP address
}
