<?php
/**
 * Simple .env file loader for BiteFinder
 * No external dependencies required
 * 
 * Usage: require_once __DIR__ . '/../config/env_loader.php';
 *        $db_host = getenv('DB_HOST');
 */

function load_env($path = null)
{
    if ($path === null) {
        $path = __DIR__ . '/../../.env';
    }

    if (!file_exists($path)) {
        throw new Exception(".env file not found at: $path");
    }

    $lines = file($path, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

    foreach ($lines as $line) {
        // Skip comments
        if (strpos(trim($line), '#') === 0) {
            continue;
        }

        // Parse KEY=VALUE
        if (strpos($line, '=') !== false) {
            list($key, $value) = explode('=', $line, 2);
            $key = trim($key);
            $value = trim($value);

            // Remove quotes if present
            if ((strpos($value, '"') === 0 && strrpos($value, '"') === strlen($value) - 1) ||
                (strpos($value, "'") === 0 && strrpos($value, "'") === strlen($value) - 1)) {
                $value = substr($value, 1, -1);
            }

            // Set environment variable
            putenv("$key=$value");
            $_ENV[$key] = $value;
            $_SERVER[$key] = $value;
        }
    }
}

// Auto-load on include
try {
    // Load from default location or custom
    $env_file = getenv('ENV_FILE') ?: __DIR__ . '/../../.env';
    if (file_exists($env_file)) {
        load_env($env_file);
    }
} catch (Exception $e) {
    // Log warning but don't fail if .env not found (use defaults)
    error_log("Warning: " . $e->getMessage());
}

/**
 * Helper function to get env variable with default fallback
 */
function env($key, $default = null)
{
    $value = getenv($key);
    if ($value === false) {
        return $default;
    }
    return $value;
}
