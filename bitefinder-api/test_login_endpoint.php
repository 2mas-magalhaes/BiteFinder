<?php
/**
 * Test script para verificar login endpoint
 * Run: php test_login_endpoint.php
 */

$url = 'http://127.0.0.1:8000/api/auth/login_jwt.php';
$data = json_encode([
    'email' => 'goncalo@teste.com',
    'password' => '123456'
]);

$options = [
    'http' => [
        'header' => "Content-Type: application/json\r\n",
        'method' => 'POST',
        'content' => $data
    ]
];

$context = stream_context_create($options);
$response = @file_get_contents($url, false, $context);

if ($response === false) {
    echo "❌ Error: Could not connect to $url\n";
    echo "   Make sure PHP server is running: php -S 127.0.0.1:8000 -t bitefinder-api/\n";
    exit(1);
}

$result = json_decode($response, true);

echo "Status: " . ($result['ok'] ? '✅ SUCCESS' : '❌ FAILED') . "\n";
echo "Response:\n";
echo json_encode($result, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . "\n";

if ($result['ok'] && isset($result['token'])) {
    echo "\n✅ Token generated successfully!\n";
    echo "Token length: " . strlen($result['token']) . " chars\n";
    exit(0);
} else {
    echo "\n❌ Login failed: " . ($result['error'] ?? 'Unknown error') . "\n";
    exit(1);
}
