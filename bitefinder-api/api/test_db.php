<?php
// C:\bitefinder-api\api\test_db.php

require_once __DIR__ . '/config/db.php';

header('Content-Type: application/json; charset=utf-8');

try {
    $pdo = db();

    // Nome da BD atual
    $dbName = $pdo->query("SELECT DB_NAME() AS db")->fetch();
    $dbName = $dbName['db'] ?? null;

    // Lista de tabelas (TOP 20)
    $stmt = $pdo->query("
        SELECT TOP 20 TABLE_SCHEMA, TABLE_NAME
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_TYPE = 'BASE TABLE'
        ORDER BY TABLE_SCHEMA, TABLE_NAME
    ");
    $tables = $stmt->fetchAll();

    echo json_encode([
        "ok" => true,
        "database" => $dbName,
        "tables" => $tables
    ], JSON_UNESCAPED_UNICODE);

} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode([
        "ok" => false,
        "error" => "DB connection failed"
    ], JSON_UNESCAPED_UNICODE);
}