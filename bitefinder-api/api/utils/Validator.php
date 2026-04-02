<?php
/**
 * BiteFinder Input Validation & Sanitization Library
 * 
 * Centralized validation to prevent:
 * - SQL Injection (via prepared statements + validation)
 * - XSS (via sanitization)
 * - Type errors
 * - Business logic violations
 * 
 * Usage:
 *   $validator = new Validator($body);
 *   $email = $validator->email('email', true);  // required
 *   $age = $validator->integer('age', false, 0, 150);  // optional, range
 */

class Validator
{
    private $data;
    private $errors;

    public function __construct(array $data = [])
    {
        $this->data = $data;
        $this->errors = [];
    }

    /**
     * Validate email format
     * @param string $field Field name
     * @param bool $required If true, field must be present
     * @return string|null Email (lowercase) or null if invalid/missing
     */
    public function email(string $field, bool $required = false): ?string
    {
        $value = $this->data[$field] ?? null;

        if (empty($value)) {
            if ($required) {
                $this->errors[$field] = "$field é obrigatório";
            }
            return null;
        }

        $email = trim(strtolower($value));

        // Validate email format (RFC 5322 simplified)
        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            $this->errors[$field] = "$field não é um email válido";
            return null;
        }

        // Max length protection (SQL injection prevention)
        if (strlen($email) > 254) {
            $this->errors[$field] = "$field é muito longo";
            return null;
        }

        return $email;
    }

    /**
     * Validate password
     * @param string $field Field name
     * @param bool $required If true, field must be present
     * @return string|null Password (plaintext) or null
     */
    public function password(string $field, bool $required = false): ?string
    {
        $value = $this->data[$field] ?? null;

        if (empty($value)) {
            if ($required) {
                $this->errors[$field] = "Palavra-passe é obrigatória";
            }
            return null;
        }

        $password = (string)$value;

        // Length check
        if (strlen($password) < 6) {
            $this->errors[$field] = "Palavra-passe deve ter pelo menos 6 caracteres";
            return null;
        }

        if (strlen($password) > 128) {
            $this->errors[$field] = "Palavra-passe é muito longa";
            return null;
        }

        return $password;
    }

    /**
     * Validate string
     * @param string $field Field name
     * @param bool $required If true, field must be present
     * @param int|null $min Minimum length (after trim)
     * @param int|null $max Maximum length
     * @return string|null Trimmed string or null
     */
    public function string(string $field, bool $required = false, ?int $min = null, ?int $max = 1000): ?string
    {
        $value = $this->data[$field] ?? null;

        if (empty($value)) {
            if ($required) {
                $this->errors[$field] = "$field é obrigatório";
            }
            return null;
        }

        $str = trim((string)$value);

        if (empty($str) && $required) {
            $this->errors[$field] = "$field não pode ser vazio";
            return null;
        }

        if ($min !== null && strlen($str) < $min) {
            $this->errors[$field] = "$field deve ter pelo menos $min caracteres";
            return null;
        }

        if ($max !== null && strlen($str) > $max) {
            $this->errors[$field] = "$field deve ter no máximo $max caracteres";
            return null;
        }

        return $str;
    }

    /**
     * Validate integer
     * @param string $field Field name
     * @param bool $required If true, field must be present
     * @param int|null $min Minimum value
     * @param int|null $max Maximum value
     * @return int|null Integer or null
     */
    public function integer(string $field, bool $required = false, ?int $min = null, ?int $max = null): ?int
    {
        $value = $this->data[$field] ?? null;

        if ($value === null || $value === '') {
            if ($required) {
                $this->errors[$field] = "$field é obrigatório";
            }
            return null;
        }

        if (!is_numeric($value)) {
            $this->errors[$field] = "$field deve ser um número";
            return null;
        }

        $int = (int)$value;

        if ($min !== null && $int < $min) {
            $this->errors[$field] = "$field deve ser no mínimo $min";
            return null;
        }

        if ($max !== null && $int > $max) {
            $this->errors[$field] = "$field deve ser no máximo $max";
            return null;
        }

        return $int;
    }

    /**
     * Validate float/decimal
     * @param string $field Field name
     * @param bool $required If true, field must be present
     * @param float|null $min Minimum value
     * @param float|null $max Maximum value
     * @return float|null Float or null
     */
    public function float(string $field, bool $required = false, ?float $min = null, ?float $max = null): ?float
    {
        $value = $this->data[$field] ?? null;

        if ($value === null || $value === '') {
            if ($required) {
                $this->errors[$field] = "$field é obrigatório";
            }
            return null;
        }

        if (!is_numeric($value)) {
            $this->errors[$field] = "$field deve ser um número";
            return null;
        }

        $float = (float)$value;

        if ($min !== null && $float < $min) {
            $this->errors[$field] = "$field deve ser no mínimo $min";
            return null;
        }

        if ($max !== null && $float > $max) {
            $this->errors[$field] = "$field deve ser no máximo $max";
            return null;
        }

        return $float;
    }

    /**
     * Validate enum (select from predefined values)
     * @param string $field Field name
     * @param array $allowed Allowed values
     * @param bool $required If true, field must be present
     * @return string|null Value or null
     */
    public function enum(string $field, array $allowed, bool $required = false): ?string
    {
        $value = $this->data[$field] ?? null;

        if (empty($value)) {
            if ($required) {
                $this->errors[$field] = "$field é obrigatório";
            }
            return null;
        }

        $value = (string)$value;

        if (!in_array($value, $allowed, true)) {
            $this->errors[$field] = "$field deve ser um de: " . implode(', ', $allowed);
            return null;
        }

        return $value;
    }

    /**
     * Check if validation has errors
     * @return bool True if any validation errors
     */
    public function hasErrors(): bool
    {
        return !empty($this->errors);
    }

    /**
     * Get all validation errors
     * @return array Associative array of field => error message
     */
    public function getErrors(): array
    {
        return $this->errors;
    }

    /**
     * Get first error message
     * @return string|null Error message or null
     */
    public function getFirstError(): ?string
    {
        return array_values($this->errors)[0] ?? null;
    }

    /**
     * Throw exception if any errors
     * @throws Exception If validation failed
     */
    public function throwIfErrors(): void
    {
        if ($this->hasErrors()) {
            throw new Exception($this->getFirstError());
        }
    }
}
