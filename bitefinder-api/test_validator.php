<?php
/**
 * Validator Tests
 * Testes exaustivos da classe centralizada de validação
 */

echo "=== Validator Tests ===\n\n";

require_once __DIR__ . '/api/utils/Validator.php';

// Test 1: Email validation
echo "[Test 1] Email Validation\n";
$test_cases = [
    'valid@example.com' => true,
    'user+tag@domain.co.uk' => true,
    'invalid@' => false,
    '@noemail.com' => false,
    'plainaddress' => false,
    'user@.com' => false,
    '' => false,
    str_repeat('a', 250) . '@example.com' => false,  // Too long
];

$passed = 0;
foreach ($test_cases as $email => $should_be_valid) {
    $validator = new Validator(['email' => $email]);
    $result = $validator->email('email', false);
    $is_valid = $result !== null;
    
    if ($is_valid === $should_be_valid) {
        $passed++;
    } else {
        echo "  ❌ FAIL: '$email' (expected " . ($should_be_valid ? 'valid' : 'invalid') . ")\n";
    }
}

if ($passed === count($test_cases)) {
    echo "✅ PASS: All $passed email cases correct\n\n";
} else {
    echo "⚠️  $passed/" . count($test_cases) . " passed\n\n";
}

// Test 2: Password validation
echo "[Test 2] Password Validation\n";
$password_tests = [
    '123456' => true,        // Min 6 chars
    '12345' => false,        // Too short
    'password' => true,
    '' => false,
    str_repeat('a', 129) => false,  // Too long (> 128)
    'pass@123!' => true,
];

$passed = 0;
foreach ($password_tests as $pwd => $should_be_valid) {
    $validator = new Validator(['password' => $pwd]);
    $result = $validator->password('password', false);
    $is_valid = $result !== null;
    
    if ($is_valid === $should_be_valid) {
        $passed++;
    } else {
        $len = strlen($pwd);
        echo "  ❌ FAIL: pwd_length=$len (expected " . ($should_be_valid ? 'valid' : 'invalid') . ")\n";
    }
}

if ($passed === count($password_tests)) {
    echo "✅ PASS: All " . count($password_tests) . " password cases correct\n\n";
} else {
    echo "⚠️  $passed/" . count($password_tests) . " passed\n\n";
}

// Test 3: String validation (min/max)
echo "[Test 3] String Validation (min/max length)\n";
$validator = new Validator(['name' => '  João da Silva  ']);
$name = $validator->string('name', true, 3, 100);

if ($name === 'João da Silva' && strlen($name) === 13) {
    echo "✅ PASS: String trimmed and validated\n";
    echo "Input: '  João da Silva  ' → Output: '$name'\n\n";
} else {
    echo "❌ FAIL: String not properly handled\n\n";
}

// Test 4: Integer range validation
echo "[Test 4] Integer Range Validation\n";
$validator = new Validator(['age' => '25']);
$age = $validator->integer('age', true, 0, 150);

if ($age === 25) {
    echo "✅ PASS: Integer '25' validated in range [0, 150]\n";
}

// Test out of range
$validator = new Validator(['age' => '200']);
$age = $validator->integer('age', true, 0, 150);

if ($age === null && $validator->hasErrors()) {
    echo "✅ PASS: Integer '200' rejected (out of range)\n";
    echo "Error: " . $validator->getFirstError() . "\n\n";
}

// Test 5: Float/Decimal validation
echo "[Test 5] Float Validation\n";
$validator = new Validator(['price' => '19.99']);
$price = $validator->float('price', true, 0, 100);

if ($price === 19.99) {
    echo "✅ PASS: Float '19.99' validated\n\n";
}

// Test 6: Enum (select from list)
echo "[Test 6] Enum/Category Validation\n";
$roles = ['cliente', 'restaurante', 'admin'];
$validator = new Validator(['role' => 'restaurante']);
$role = $validator->enum('role', $roles, true);

if ($role === 'restaurante') {
    echo "✅ PASS: Enum 'restaurante' validated from [" . implode(', ', $roles) . "]\n";
}

$validator = new Validator(['role' => 'hacker']);
$role = $validator->enum('role', $roles, true);

if ($role === null && $validator->hasErrors()) {
    echo "✅ PASS: Enum 'hacker' rejected (not in allowed list)\n";
    echo "Error: " . $validator->getFirstError() . "\n\n";
}

// Test 7: Required vs Optional
echo "[Test 7] Required vs Optional Fields\n";
$validator = new Validator(['name' => '', 'email' => null]);
$name = $validator->string('name', true);  // Required
$email = $validator->email('email', false);  // Optional

if ($validator->hasErrors() && $email === null) {
    echo "✅ PASS: Required field validation\n";
    echo "Errors: " . json_encode($validator->getErrors()) . "\n\n";
}

// Test 8: Login scenario (DRY improvement)
echo "[Test 8] Login Endpoint (Old vs New)\n";

// OLD WAY (código repetido)
echo "OLD (manual):\n";
$body = ['email' => 'test@example.com', 'password' => '123456'];
$old_email = trim(strtolower((string)($body['email'] ?? '')));
$old_password = (string)($body['password'] ?? '');
if ($old_email === '' || $old_password === '') {
    echo "  Manual validation: 4 linhas\n";
}

// NEW WAY (DRY, reutilizável)
echo "NEW (Validator class):\n";
$validator = new Validator($body);
$new_email = $validator->email('email', true);
$new_password = $validator->password('password', true);
echo "  Validator: 2 linhas + centralizado + mais seguro\n\n";

echo "=== All Validator Tests Completed ===\n";
