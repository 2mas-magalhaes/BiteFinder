<?php
header('Content-Type: text/plain; charset=utf-8');
echo password_hash("123456", PASSWORD_DEFAULT);