<?php
/**
 * Test script to verify database connection using environment variables
 * Run: php test_db_connection.php
 */

require_once __DIR__ . '/api/config/env_loader.php';
require_once __DIR__ . '/api/config/db.php';

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n";
echo "BiteFinder Database Connection Test\n";
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n";

// Check environment variables loaded
echo "1. Environment Variables Loaded:\n";
$env_vars = ['DB_HOST', 'DB_NAME', 'DB_USER', 'DB_PASS'];
foreach ($env_vars as $var) {
    $value = getenv($var);
    if ($value) {
        if ($var === 'DB_PASS') {
            echo "   ✅ $var = " . str_repeat('*', strlen($value)) . " (hidden)\n";
        } else {
            echo "   ✅ $var = $value\n";
        }
    } else {
        echo "   ❌ $var = NOT SET\n";
    }
}

echo "\n2. Attempting Database Connection...\n";
try {
    $pdo = db();
    echo "   ✅ Connected successfully to Azure SQL Server!\n";
    
    // Test query
    echo "\n3. Testing Query Execution...\n";
    $stmt = $pdo->query("SELECT TOP 1 IdUser, Email, Nome FROM dbo.Users");
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    if ($result) {
        echo "   ✅ Test user found: {$result['Nome']} ({$result['Email']})\n";
    } else {
        echo "   ⚠️  No users in database\n";
    }
    
    echo "\n✅ Database connection test PASSED!\n";
    exit(0);
    
} catch (Throwable $e) {
    echo "   ❌ Connection failed!\n";
    echo "   Error: " . $e->getMessage() . "\n";
    echo "\n❌ Database connection test FAILED!\n";
    exit(1);
}
