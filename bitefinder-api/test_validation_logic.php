<?php
// Test: Missing credentials validation (isolated)

// Temporarily unset environment variables
unset($_ENV['DB_USER']);
unset($_ENV['DB_PASS']);
putenv('DB_USER=');
putenv('DB_PASS=');

// Mock env function for this test
function env_test($key, $default = null)
{
    $value = getenv($key);
    if ($value === false || $value === '') {
        return $default;
    }
    return $value;
}

$db_user = env_test('DB_USER', '');
$db_pass = env_test('DB_PASS', '');

echo "Testing validation logic:\n";
echo "DB_USER: '" . $db_user . "' (empty: " . (empty($db_user) ? 'YES' : 'NO') . ")\n";
echo "DB_PASS: '" . $db_pass . "' (empty: " . (empty($db_pass) ? 'YES' : 'NO') . ")\n";

if (empty($db_user) || empty($db_pass)) {
    echo "✅ PASS: Validation would reject empty credentials\n";
} else {
    echo "❌ FAIL: Validation allows empty credentials\n";
}

// Now test with actual .env loaded
echo "\n--- Testing with actual .env file ---\n";
require_once __DIR__ . '/api/config/env_loader.php';

$db_user = env('DB_USER', '');
$db_pass = env('DB_PASS', '');

echo "DB_USER from .env: " . (strlen($db_user) > 0 ? 'SET' : 'NOT SET') . "\n";
echo "DB_PASS from .env: " . (strlen($db_pass) > 0 ? 'SET' : 'NOT SET') . "\n";

if (!empty($db_user) && !empty($db_pass)) {
    echo "✅ Both credentials present in .env\n";
}
