package com.fiap.mindcare.service.security;

import com.fiap.mindcare.service.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    private static final int MINIMUM_LENGTH = 8;
    private static final int MAXIMUM_LENGTH = 128;

    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[@#$%^&+=!*()]");

    private static final Set<String> FORBIDDEN_PASSWORDS = Set.of(
            "senha", "senha123", "123456", "12345678",
            "teste", "password", "admin", "qwerty", "senhateste123",
            "testeteste", "abc123", "password123", "qwerty123",
            "letmein", "welcome", "admin123", "123456789",
            "1234567890", "mudaaqui", "mudar123"
    );

    public void validate(String rawPassword) {
        if (StringUtils.isBlank(rawPassword)) {
            throw new BusinessException("A senha não pode ser vazia.");
        }

        if (rawPassword.length() < MINIMUM_LENGTH) {
            throw new BusinessException("A senha deve conter ao menos " + MINIMUM_LENGTH + " caracteres.");
        }

        if (rawPassword.length() > MAXIMUM_LENGTH) {
            throw new BusinessException("A senha não pode exceder " + MAXIMUM_LENGTH + " caracteres.");
        }

        if (!rawPassword.equals(rawPassword.trim())) {
            throw new BusinessException("A senha não pode conter espaços no início ou no fim.");
        }

        if (!UPPERCASE_PATTERN.matcher(rawPassword).find()) {
            throw new BusinessException("A senha deve conter ao menos uma letra maiúscula.");
        }

        if (!LOWERCASE_PATTERN.matcher(rawPassword).find()) {
            throw new BusinessException("A senha deve conter ao menos uma letra minúscula.");
        }

        if (!DIGIT_PATTERN.matcher(rawPassword).find()) {
            throw new BusinessException("A senha deve conter ao menos um número.");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(rawPassword).find()) {
            throw new BusinessException("A senha deve conter ao menos um caractere especial (@#$%^&+=!*()).");
        }

        String normalized = rawPassword.toLowerCase().trim();
        String alphanumericOnly = normalized.replaceAll("[^a-z0-9]", "");
        if (FORBIDDEN_PASSWORDS.contains(normalized) || FORBIDDEN_PASSWORDS.contains(alphanumericOnly)) {
            throw new BusinessException("A senha informada é muito fraca. Escolha outra combinação.");
        }
    }
}
