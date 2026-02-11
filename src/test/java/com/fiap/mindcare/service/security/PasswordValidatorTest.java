package com.fiap.mindcare.service.security;

import com.fiap.mindcare.service.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    void validate_shouldRejectBlankOrShortPasswords() {
        assertThrows(BusinessException.class, () -> validator.validate(null));
        assertThrows(BusinessException.class, () -> validator.validate(""));
        assertThrows(BusinessException.class, () -> validator.validate("Ab1@xyz"));
    }

    @Test
    void validate_shouldRejectPasswordExceedingMaxLength() {
        String longPassword = "A1@a" + "a".repeat(125);
        assertThrows(BusinessException.class, () -> validator.validate(longPassword));
    }

    @Test
    void validate_shouldRejectPasswordWithLeadingOrTrailingSpaces() {
        assertThrows(BusinessException.class, () -> validator.validate(" Abc1234@x"));
        assertThrows(BusinessException.class, () -> validator.validate("Abc1234@x "));
    }

    @Test
    void validate_shouldRejectPasswordWithoutUppercase() {
        assertThrows(BusinessException.class, () -> validator.validate("minhasenha9@"));
    }

    @Test
    void validate_shouldRejectPasswordWithoutLowercase() {
        assertThrows(BusinessException.class, () -> validator.validate("MINHASENHA9@"));
    }

    @Test
    void validate_shouldRejectPasswordWithoutDigit() {
        assertThrows(BusinessException.class, () -> validator.validate("MinhaSenha@abc"));
    }

    @Test
    void validate_shouldRejectPasswordWithoutSpecialChar() {
        assertThrows(BusinessException.class, () -> validator.validate("MinhaSenha9"));
    }

    @Test
    void validate_shouldRejectCommonPasswords() {
        assertThrows(BusinessException.class, () -> validator.validate("Senha123@"));
        assertThrows(BusinessException.class, () -> validator.validate("Admin123@"));
        assertThrows(BusinessException.class, () -> validator.validate("Qwerty123@"));
        assertThrows(BusinessException.class, () -> validator.validate("Abc123!!@"));
    }

    @Test
    void validate_shouldRejectExpandedForbiddenPasswords() {
        assertThrows(BusinessException.class, () -> validator.validate("Password@123"));
        assertThrows(BusinessException.class, () -> validator.validate("Qwerty@123"));
        assertThrows(BusinessException.class, () -> validator.validate("Mudar@123!"));
        assertThrows(BusinessException.class, () -> validator.validate("Admin@123!"));
    }

    @Test
    void validate_shouldAcceptStrongPasswords() {
        assertDoesNotThrow(() -> validator.validate("MinhaSenha9@"));
        assertDoesNotThrow(() -> validator.validate("C0mpl3x@Pass"));
        assertDoesNotThrow(() -> validator.validate("Segur@nca1"));
        assertDoesNotThrow(() -> validator.validate("T3st!ngPwd"));
    }
}
