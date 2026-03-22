<?php
require __DIR__ . '/bitefinder-api/api/config/db.php';
$pdo = db();
$rows = $pdo->query("SELECT TOP 20 IdUser, Nome, Email, Role FROM dbo.Users ORDER BY IdUser")->fetchAll(PDO::FETCH_ASSOC);
echo json_encode($rows, JSON_UNESCAPED_UNICODE);
