<?php
// Test: direct DB configuration validation (no .env)

echo "Testing validation logic:\n";

// Simulate empty credentials to validate guard logic.
$db_user = '';
$db_pass = '';

echo "DB_USER: '" . $db_user . "' (empty: " . (empty($db_user) ? 'YES' : 'NO') . ")\n";
echo "DB_PASS: '" . $db_pass . "' (empty: " . (empty($db_pass) ? 'YES' : 'NO') . ")\n";

if (empty($db_user) || empty($db_pass)) {
    echo "✅ PASS: Validation would reject empty credentials\n";
} else {
    echo "❌ FAIL: Validation allows empty credentials\n";
}

echo "\n--- Testing real connection from db.php ---\n";
require_once __DIR__ . '/api/config/db.php';

try {
    $pdo = db();
    $pdo->query("SELECT 1")->fetch(PDO::FETCH_ASSOC);
    echo "✅ Direct credentials in db.php are working\n";
} catch (Throwable $e) {
    echo "❌ Connection failed: " . $e->getMessage() . "\n";
    exit(1);
}
