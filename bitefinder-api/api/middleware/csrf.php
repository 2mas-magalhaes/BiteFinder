<?php
/**
 * CSRF (Cross-Site Request Forgery) Protection Middleware
 * 
 * Strategy: Token-based validation
 * - Generate unique token per session
 * - Require token in POST/PUT/DELETE requests
 * - Client sends in X-CSRF-Token header or form field
 * 
 * Usage:
 *   session_start();
 *   require_once __DIR__ . '/csrf.php';
 *   
 *   // Generate token for forms
 *   $token = csrf_token();
 *   
 *   // Validate in POST endpoint
 *   csrf_verify();
 */

/**
 * Generate or retrieve CSRF token
 * Called once per session, token stored in $_SESSION
 */
function csrf_token(): string
{
    // Initialize session if needed
    if (session_status() === PHP_SESSION_NONE) {
        session_start();
    }

    // Generate token if not exists
    if (empty($_SESSION['csrf_token'])) {
        // Use random_bytes for cryptographic security
        $_SESSION['csrf_token'] = bin2hex(random_bytes(32));
    }

    return $_SESSION['csrf_token'];
}

/**
 * Validate CSRF token from request
 * Checks X-CSRF-Token header or _csrf form parameter
 * 
 * @throws Exception if token invalid or missing
 */
function csrf_verify(): void
{
    // Initialize session if needed
    if (session_status() === PHP_SESSION_NONE) {
        session_start();
    }

    // Only validate for state-changing requests
    $method = strtoupper($_SERVER['REQUEST_METHOD'] ?? 'GET');
    $state_changing = in_array($method, ['POST', 'PUT', 'DELETE', 'PATCH']);

    if (!$state_changing) {
        return; // GET requests don't change state, no CSRF risk
    }

    // Get token from request (X-CSRF-Token header or _csrf form/JSON field)
    $token_from_request = null;

    // Check header first (X-CSRF-Token)
    if (!empty($_SERVER['HTTP_X_CSRF_TOKEN'])) {
        $token_from_request = $_SERVER['HTTP_X_CSRF_TOKEN'];
    }
    // Check form data (application/x-www-form-urlencoded)
    elseif (!empty($_POST['_csrf'])) {
        $token_from_request = $_POST['_csrf'];
    }
    // Check JSON body
    else {
        $json = json_decode(file_get_contents('php://input'), true);
        if (is_array($json) && !empty($json['_csrf'])) {
            $token_from_request = $json['_csrf'];
        }
    }

    // Token must be present
    if (empty($token_from_request)) {
        http_response_code(403);
        echo json_encode([
            'ok' => false,
            'error' => 'CSRF token missing',
            'message' => 'Request rejected: Security token not found'
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }

    // Validate token matches session
    $token_in_session = $_SESSION['csrf_token'] ?? null;
    
    // Use hash_equals to prevent timing attacks
    if (empty($token_in_session) || !hash_equals($token_in_session, $token_from_request)) {
        http_response_code(403);
        echo json_encode([
            'ok' => false,
            'error' => 'CSRF token invalid',
            'message' => 'Request rejected: Invalid security token'
        ], JSON_UNESCAPED_UNICODE);
        exit;
    }
}

/**
 * Get CSRF HTML meta tag (for JavaScript/AJAX)
 * Insert in <head> of HTML forms
 * JavaScript will read and send in X-CSRF-Token header
 */
function csrf_meta_tag(): string
{
    $token = csrf_token();
    return sprintf(
        '<meta name="csrf-token" content="%s">',
        htmlspecialchars($token, ENT_QUOTES, 'UTF-8')
    );
}

/**
 * Get CSRF hidden form field
 * Include in HTML forms as hidden input
 */
function csrf_form_field(): string
{
    $token = csrf_token();
    return sprintf(
        '<input type="hidden" name="_csrf" value="%s">',
        htmlspecialchars($token, ENT_QUOTES, 'UTF-8')
    );
}

/**
 * Get current CSRF token for API responses
 * Return token to client for next request
 */
function csrf_get_token(): ?string
{
    return $_SESSION['csrf_token'] ?? null;
}
