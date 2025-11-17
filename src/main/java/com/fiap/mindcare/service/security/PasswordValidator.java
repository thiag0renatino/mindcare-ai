package com.fiap.mindcare.service.security;

import com.fiap.mindcare.service.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PasswordValidator {

    private static final int MINIMUM_LENGTH = 8;
    private static final Set<String> FORBIDDEN_PASSWORDS = Set.of(
            "senha", "senha123", "123456", "12345678",
            "teste", "password", "admin", "qwerty", "senhateste123",
            "testeteste"
    );

    public void validate(String rawPassword) {
        if (StringUtils.isBlank(rawPassword) || rawPassword.length() < MINIMUM_LENGTH) {
            throw new BusinessException("A senha deve conter ao menos " + MINIMUM_LENGTH + " caracteres.");
        }

        String normalized = rawPassword.toLowerCase().trim();
        if (FORBIDDEN_PASSWORDS.contains(normalized)) {
            throw new BusinessException("A senha informada é muito fraca. Escolha outra combinação.");
        }
    }
}
