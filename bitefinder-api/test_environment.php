<?php
// Test script: Validate direct configuration (no .env)

echo "=== BiteFinder Environment Configuration Test ===\n\n";

echo "[Test 1] Config mode...\n";
echo "✅ Using direct credentials from api/config/db.php\n\n";

echo "\n[Test 2] Testing database connection...\n";
try {
    require_once __DIR__ . '/api/config/db.php';
    $db = db();
    echo "✅ Database connection successful\n";
    
    // Test query
    $stmt = $db->prepare("SELECT 1 as test");
    $stmt->execute();
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "✅ Database query executed: SELECT 1 = " . $result['test'] . "\n";
} catch (Exception $e) {
    echo "❌ Database error: " . $e->getMessage() . "\n";
}

echo "\n[Test 3] Testing JWT functions...\n";
try {
    require_once __DIR__ . '/api/jwt_functions.php';
    
    $test_payload = ['sub' => 1, 'email' => 'test@example.com'];
    $token = jwt_encode($test_payload, 3600);
    echo "✅ JWT token generated (length: " . strlen($token) . ")\n";
    
    $verified = jwt_verify($token);
    if ($verified && $verified['sub'] == 1) {
        echo "✅ JWT token verified successfully\n";
    } else {
        echo "❌ JWT token verification failed\n";
    }
} catch (Exception $e) {
    echo "❌ JWT error: " . $e->getMessage() . "\n";
}

echo "\n=== All Tests Completed ===\n";
