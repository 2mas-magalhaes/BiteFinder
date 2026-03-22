<?php
require __DIR__ . '/bitefinder-api/api/config/db.php';
$pdo = db();
$email = 'resto@teste.com';
$nome = 'Restaurante Demo';
$pass = '123456';
$hash = password_hash($pass, PASSWORD_DEFAULT);
$pdo->beginTransaction();
try {
    $sel = $pdo->prepare("SELECT IdUser FROM dbo.Users WHERE Email = :email");
    $sel->execute(['email' => $email]);
    $userId = (int)($sel->fetchColumn() ?: 0);

    if ($userId <= 0) {
        $ins = $pdo->prepare("INSERT INTO dbo.Users (Nome, Email, PasswordHash, Role) VALUES (:nome, :email, :hash, 'restaurante')");
        $ins->execute(['nome' => $nome, 'email' => $email, 'hash' => $hash]);
        $userId = (int)$pdo->lastInsertId();
    } else {
        $upd = $pdo->prepare("UPDATE dbo.Users SET Role = 'restaurante', PasswordHash = :hash, Nome = :nome WHERE IdUser = :uid");
        $upd->execute(['hash' => $hash, 'nome' => $nome, 'uid' => $userId]);
    }

    $mapSel = $pdo->prepare("SELECT COUNT(1) FROM dbo.RestauranteUser WHERE UserId = :uid AND RestauranteId = 2");
    $mapSel->execute(['uid' => $userId]);
    $exists = (int)$mapSel->fetchColumn();
    if ($exists === 0) {
        $mapIns = $pdo->prepare("INSERT INTO dbo.RestauranteUser (UserId, RestauranteId) VALUES (:uid, 2)");
        $mapIns->execute(['uid' => $userId]);
    }

    $pdo->commit();
    echo json_encode(['ok'=>true,'email'=>$email,'password'=>$pass,'userId'=>$userId], JSON_UNESCAPED_UNICODE);
} catch (Throwable $e) {
    if ($pdo->inTransaction()) $pdo->rollBack();
    echo json_encode(['ok'=>false,'error'=>$e->getMessage()], JSON_UNESCAPED_UNICODE);
}
