package com.fiap.mindcare.repository;

import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.config.security.PasswordConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(PasswordConfig.class)
class EmpresaRepositoryTest {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Test
    void findByCnpj_shouldReturnEmpresaWhenExists() {
        Empresa empresa = new Empresa(null, "12345678901234", "MindCare Solutions", null);
        empresaRepository.save(empresa);

        Optional<Empresa> result = empresaRepository.findByCnpj("12345678901234");

        assertTrue(result.isPresent());
        assertEquals("MindCare Solutions", result.get().getNome());
    }

    @Test
    void existsByCnpj_shouldReturnTrueWhenExists() {
        Empresa empresa = new Empresa(null, "99999999000100", "TechNova Corp", null);
        empresaRepository.save(empresa);

        assertTrue(empresaRepository.existsByCnpj("99999999000100"));
    }

    @Test
    void findByNomeContainingIgnoreCase_shouldFindByPartialName() {
        Empresa empresa = new Empresa(null, "11111111000111", "HealthSync Data", null);
        empresaRepository.save(empresa);

        Optional<Empresa> result = empresaRepository.findByNomeContainingIgnoreCase("healthsync");

        assertTrue(result.isPresent());
        assertEquals("HealthSync Data", result.get().getNome());
    }
}
