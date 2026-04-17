<?php

function db(): PDO {
    // Para dev local, credenciais fixas para Azure SQL.
    $DB_HOST = 'bitefinderapp.database.windows.net';
    $DB_NAME = 'free-sql-db-6399592';
    $DB_USER = 'borges';
    $DB_PASS = '***REMOVED***';

    // Azure SQL requer encriptacao.
    $dsn = "sqlsrv:Server=$DB_HOST;Database=$DB_NAME;Encrypt=yes;TrustServerCertificate=no";

    return new PDO($dsn, $DB_USER, $DB_PASS, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);
}