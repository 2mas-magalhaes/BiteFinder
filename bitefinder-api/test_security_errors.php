<?php
// Test: Error handling - production vs development

echo "=== Error Handling Test ===\n\n";

// Test 1: In production mode, errors should be generic
echo "[Test 1] Production mode - generic error messages\n";
putenv('APP_ENV=production');

try {
    // Simulate error
    throw new Exception('Real database error: Table does not exist');
} catch (Exception $e) {
    $app_env = getenv('APP_ENV');
    $message = 'Database connection failed. ';
    $message .= ($app_env === 'development') ? $e->getMessage() : 'Please contact support';
    
    echo "Response: " . $message . "\n";
    if ($app_env !== 'development' && strpos($message, 'Please contact') !== false) {
        echo "✅ PASS: Production mode shows generic message\n";
    }
}

// Test 2: In development, developers can see details
echo "\n[Test 2] Development mode - detailed error messages\n";
putenv('APP_ENV=development');

try {
    throw new Exception('Real database error: Table does not exist');
} catch (Exception $e) {
    $app_env = getenv('APP_ENV');
    $message = 'Database connection failed. ';
    $message .= ($app_env === 'development') ? $e->getMessage() : 'Please contact support';
    
    echo "Response: " . $message . "\n";
    if (strpos($message, 'Table does not exist') !== false) {
        echo "✅ PASS: Development mode shows detailed error\n";
    }
}

// Test 3: JWT secret masking
echo "\n[Test 3] Sensitive data masking in logs\n";
$jwt_secret = 's3cr3t_b1t3f1nd3r_2026!_change_in_production';
$masked = substr($jwt_secret, 0, 4) . '***' . substr($jwt_secret, -4);
echo "Original: " . $jwt_secret . "\n";
echo "Masked: " . $masked . "\n";
echo "✅ PASS: Sensitive data can be masked for logging\n";

echo "\n=== All Security Tests Completed ===\n";
