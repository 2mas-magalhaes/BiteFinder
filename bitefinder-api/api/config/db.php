<?php
/**
 * BiteFinder Database Configuration
 * Loads credentials from environment variables (.env file)
 * 
 * Security: Credentials are never hardcoded in source control
 * .env file is in .gitignore and should be configured per environment
 */

require_once __DIR__ . '/env_loader.php';

function db(): PDO {
    // Load configuration from .env file
    $db_host = env('DB_HOST', getenv('DB_HOST') ?: 'bitefinderapp.database.windows.net');
    $db_name = env('DB_NAME', getenv('DB_NAME') ?: 'free-sql-db-6399592');
    $db_user = env('DB_USER', getenv('DB_USER') ?: '');
    $db_pass = env('DB_PASS', getenv('DB_PASS') ?: '');

    // Validate required credentials
    if (empty($db_user) || empty($db_pass)) {
        throw new Exception(
            'Database credentials not configured. ' .
            'Please copy .env.example to .env and configure DB_USER and DB_PASS'
        );
    }

    // Build DSN for Azure SQL Server
    // Encrypt=yes: Required for Azure SQL
    // TrustServerCertificate=no: Forces proper SSL validation (production security)
    $dsn = sprintf(
        "sqlsrv:Server=%s;Database=%s;Encrypt=yes;TrustServerCertificate=no",
        $db_host,
        $db_name
    );

    try {
        $pdo = new PDO($dsn, $db_user, $db_pass, [
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
            PDO::SQLSRV_ATTR_ENCODING => PDO::SQLSRV_ENCODING_UTF8,
        ]);
        
        return $pdo;
    } catch (PDOException $e) {
        // Log detailed error (never expose to client)
        error_log("Database connection failed: " . $e->getMessage());
        
        // Return generic error to client
        throw new Exception(
            'Database connection failed. ' .
            (getenv('APP_ENV') === 'development' ? $e->getMessage() : 'Please contact support.')
        );
    }
}