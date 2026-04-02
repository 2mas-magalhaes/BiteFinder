<?php
// Test script: Validate environment configuration

echo "=== BiteFinder Environment Configuration Test ===\n\n";

// Test 1: Load env_loader
echo "[Test 1] Loading .env file...\n";
try {
    require_once __DIR__ . '/api/config/env_loader.php';
    echo "✅ env_loader.php loaded\n\n";
} catch (Exception $e) {
    echo "❌ Error loading env_loader: " . $e->getMessage() . "\n\n";
    exit(1);
}

// Test 2: Check env variables
echo "[Test 2] Checking environment variables...\n";
$vars = ['DB_HOST', 'DB_NAME', 'DB_USER', 'DB_PASS', 'JWT_SECRET', 'APP_ENV'];
$missing = [];

foreach ($vars as $var) {
    $value = env($var, null);
    if ($value === null) {
        echo "❌ $var: NOT SET\n";
        $missing[] = $var;
    } else {
        // Mask sensitive values
        if (in_array($var, ['DB_PASS', 'JWT_SECRET'])) {
            $display = substr($value, 0, 4) . '***' . substr($value, -4);
        } else {
            $display = $value;
        }
        echo "✅ $var: $display\n";
    }
}

if (!empty($missing)) {
    echo "\n⚠️  Missing variables: " . implode(', ', $missing) . "\n";
    echo "Please copy .env.example to .env and configure values\n\n";
}

echo "\n[Test 3] Testing database connection...\n";
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

echo "\n[Test 4] Testing JWT functions...\n";
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
