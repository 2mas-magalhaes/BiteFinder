<?php
require __DIR__ . '/bitefinder-api/api/config/db.php';
$pdo = db();
$rest = $pdo->query("SELECT TOP 10 IdRestaurante, Nome FROM dbo.Restaurante ORDER BY IdRestaurante")->fetchAll(PDO::FETCH_ASSOC);
$ru = $pdo->query("SELECT TOP 20 UserId, RestauranteId FROM dbo.RestauranteUser ORDER BY UserId")->fetchAll(PDO::FETCH_ASSOC);
echo json_encode(['restaurantes'=>$rest,'restauranteUsers'=>$ru], JSON_UNESCAPED_UNICODE);
