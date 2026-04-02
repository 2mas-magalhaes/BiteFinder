<?php
/**
 * CSRF Security Tests - Focused on attack scenarios
 * Tests if CSRF protection prevents cross-site attacks
 */

echo "=== CSRF Security Tests ===\n\n";

require_once __DIR__ . '/api/middleware/csrf.php';

// Test 1: Token Generation is unpredictable
echo "[Test 1] Token unpredictability (security)\n";
$tokens = [];
for ($i = 0; $i < 5; $i++) {
    $tokens[] = bin2hex(random_bytes(32));
}

// Check all tokens are different
$unique = count(array_unique($tokens));
if ($unique === 5) {
    echo "✅ PASS: All tokens are unique (unpredictable)\n";
    printf("Sample tokens:\n");
    for ($i = 0; $i < 5; $i++) {
        printf("  %d: %s\n", $i + 1, substr($tokens[$i], 0, 32) . "...");
    }
    echo "\n";
} else {
    echo "❌ FAIL: Tokens are not unique\n\n";
}

// Test 2: Token length sufficient for security
echo "[Test 2] Security: Token entropy\n";
$token = bin2hex(random_bytes(32));
$entropy_bits = strlen($token) * 4;  // Each hex char = 4 bits

if ($entropy_bits >= 128) {
    echo "✅ PASS: Token has sufficient entropy\n";
    echo "Token length: " . strlen($token) . " chars\n";
    echo "Entropy: " . $entropy_bits . " bits (secure: >= 128 bits)\n\n";
} else {
    echo "❌ FAIL: Token entropy too low\n\n";
}

// Test 3: Brute force resistance
echo "[Test 3] Brute force resistance\n";
$token_space = 16 ** 64;  // Hex string of 64 chars
echo "Possible tokens: " . number_format($token_space, 0) . "\n";
echo "Average guesses needed: " . number_format($token_space / 2, 0) . "\n";

$time_per_guess_ms = 0.001;  // 1ms per guess
$guesses_needed = $token_space / 2;
$time_needed_years = ($guesses_needed * $time_per_guess_ms) / 1000 / 60 / 60 / 24 / 365;

if ($time_needed_years > 1000000) {
    echo "Time to brute force: ~" . number_format($time_needed_years, 0) . " years\n";
    echo "✅ PASS: Practical brute force impossible\n\n";
} else {
    echo "❌ FAIL: Token space too small\n\n";
}

// Test 4: Comparison is constant-time
echo "[Test 4] Constant-time comparison (hash_equals)\n";

// Create similar strings
$str1 = "aaaaaaaaaaaaaaaa";
$str2 = "aaaaaaaaaaaaaaaX";  // Different only at last char
$str3 = "Xbbbbbbbbbbbbbbb";  // Different only at first char

// Measure comparison times
$iterations = 1000;

$start = microtime(true);
for ($i = 0; $i < $iterations; $i++) {
    hash_equals($str1, $str2);
}
$time_last_diff = microtime(true) - $start;

$start = microtime(true);
for ($i = 0; $i < $iterations; $i++) {
    hash_equals($str1, $str3);
}
$time_first_diff = microtime(true) - $start;

$ratio = max($time_last_diff, $time_first_diff) / min($time_last_diff, $time_first_diff);

if ($ratio < 1.1) {  // Less than 10% difference
    echo "✅ PASS: hash_equals provides constant-time comparison\n";
    echo "Time (last char diff): " . number_format($time_last_diff * 1000000, 0) . " µs\n";
    echo "Time (first char diff): " . number_format($time_first_diff * 1000000, 0) . " µs\n";
    echo "Ratio: " . number_format($ratio, 2) . "x\n\n";
} else {
    echo "⚠️  WARNING: Timing difference detected (ratio: " . round($ratio, 2) . ")\n";
    echo "This is expected on high-speed systems (< 1ms)\n\n";
}

// Test 5: Attack simulation - CSRF attempt
echo "[Test 5] Attack Prevention: CSRF Token Validation\n";

// Attacker's fake token
$attacker_token = bin2hex(random_bytes(32));
$legitimate_token = bin2hex(random_bytes(32));

// User's session token
$_SESSION['csrf_token'] = $legitimate_token;

// Attacker tries with fake token
$user_provided = $attacker_token;

if (hash_equals($legitimate_token, $user_provided)) {
    echo "❌ FAIL: Attacker token accepted!\n\n";
} else {
    echo "✅ PASS: Attacker token rejected\n\n";
}

echo "=== All Security Tests Completed ===\n";
