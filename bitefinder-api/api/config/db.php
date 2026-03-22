<?php
// C:\bitefinder-api\api\config\db.php

function db(): PDO {
    // Para dev local, mete as credenciais aqui (depois podes trocar para getenv)
    $DB_HOST = 'bitefinderapp.database.windows.net';
    $DB_NAME = 'free-sql-db-6399592';
    $DB_USER = 'borges';         // confirma se é mesmo este o login SQL
    $DB_PASS = '***REMOVED***'; // mete a password

    // Azure SQL normalmente exige Encrypt
    $dsn = "sqlsrv:Server=$DB_HOST;Database=$DB_NAME;Encrypt=yes;TrustServerCertificate=no";

    return new PDO($dsn, $DB_USER, $DB_PASS, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    ]);
}