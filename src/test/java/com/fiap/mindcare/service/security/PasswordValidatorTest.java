package com.fiap.mindcare.service.security;

import com.fiap.mindcare.service.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    void validate_shouldRejectBlankOrShortPasswords() {
        assertThrows(BusinessException.class, () -> validator.validate(null));
        assertThrows(BusinessException.class, () -> validator.validate(""));
        assertThrows(BusinessException.class, () -> validator.validate("1234567"));
    }

    @Test
    void validate_shouldRejectCommonPasswords() {
        assertThrows(BusinessException.class, () -> validator.validate("senha"));
        assertThrows(BusinessException.class, () -> validator.validate("Password"));
        assertThrows(BusinessException.class, () -> validator.validate("12345678"));
    }

    @Test
    void validate_shouldAcceptStrongPasswords() {
        assertDoesNotThrow(() -> validator.validate("MinhaSenha9"));
    }
}
