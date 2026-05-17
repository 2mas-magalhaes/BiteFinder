<?php
require_once __DIR__ . '/env_loader.php';

function db(): PDO {
    $DB_HOST = env('DB_HOST', 'bitefinderapp.database.windows.net');
    $DB_NAME = env('DB_NAME', 'free-sql-db-6399592');
    $DB_USER = env('DB_USER', 'borges');
    $DB_PASS = env('DB_PASS', '***REMOVED***');

    $dsn = "sqlsrv:Server=$DB_HOST;Database=$DB_NAME;Encrypt=yes;TrustServerCertificate=no";

    return new PDO($dsn, $DB_USER, $DB_PASS, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_PERSISTENT => true,
    ]);
}
