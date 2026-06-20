<?php
require_once __DIR__ . '/env_loader.php';

function db(): PDO {
    $DB_HOST = trim((string)env('DB_HOST', ''));
    $DB_NAME = trim((string)env('DB_NAME', ''));
    $DB_USER = trim((string)env('DB_USER', ''));
    $DB_PASS = (string)env('DB_PASS', '');

    if ($DB_HOST === '' || $DB_NAME === '' || $DB_USER === '' || $DB_PASS === '') {
        throw new RuntimeException('Missing database configuration in environment (.env).');
    }

    $dsn = "sqlsrv:Server=$DB_HOST;Database=$DB_NAME;Encrypt=yes;TrustServerCertificate=no";

    return new PDO($dsn, $DB_USER, $DB_PASS, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_PERSISTENT => true,
    ]);
}
