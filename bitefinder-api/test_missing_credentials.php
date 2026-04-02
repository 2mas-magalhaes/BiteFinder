<?php
// Test: Missing credentials validation

// Simulate missing DB_PASS
putenv('DB_USER=testuser');
putenv('DB_PASS=');  // Empty!

require_once __DIR__ . '/api/config/db.php';

try {
    $db = db();
    echo "❌ FAIL: Should have thrown exception for missing DB_PASS\n";
} catch (Exception $e) {
    if (strpos($e->getMessage(), 'not configured') !== false) {
        echo "✅ PASS: Correctly rejected missing credentials\n";
        echo "Message: " . $e->getMessage() . "\n";
    } else {
        echo "❌ FAIL: Wrong error message: " . $e->getMessage() . "\n";
    }
}
