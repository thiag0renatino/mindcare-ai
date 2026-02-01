package com.fiap.mindcare.repository;

import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.config.security.PasswordConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(PasswordConfig.class)
class UsuarioSistemaRepositoryTest {

    @Autowired
    private UsuarioSistemaRepository usuarioRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Test
    void findByEmail_shouldReturnUserWhenExists() {
        Empresa empresa = empresaRepository.save(new Empresa(null, "22222222000122", "InnovaCare Tech", null));
        UsuarioSistema usuario = new UsuarioSistema(null, "senha", empresa, TipoUsuario.USER, "ana@acme.com", "Ana");
        usuarioRepository.save(usuario);

        Optional<UsuarioSistema> result = usuarioRepository.findByEmail("ana@acme.com");

        assertTrue(result.isPresent());
        assertEquals("Ana", result.get().getNome());
    }

    @Test
    void existsByEmail_shouldReturnTrueWhenExists() {
        Empresa empresa = empresaRepository.save(new Empresa(null, "33333333000133", "MindCare Solutions", null));
        UsuarioSistema usuario = new UsuarioSistema(null, "senha", empresa, TipoUsuario.USER, "joao@acme.com", "Joao");
        usuarioRepository.save(usuario);

        assertTrue(usuarioRepository.existsByEmail("joao@acme.com"));
    }

    @Test
    void findByEmpresaId_shouldReturnOnlyEmpresaUsers() {
        Empresa empresaA = empresaRepository.save(new Empresa(null, "44444444000144", "Empresa A", null));
        Empresa empresaB = empresaRepository.save(new Empresa(null, "55555555000155", "Empresa B", null));

        usuarioRepository.save(new UsuarioSistema(null, "senha", empresaA, TipoUsuario.USER, "a1@acme.com", "A1"));
        usuarioRepository.save(new UsuarioSistema(null, "senha", empresaA, TipoUsuario.ADMIN, "a2@acme.com", "A2"));
        usuarioRepository.save(new UsuarioSistema(null, "senha", empresaB, TipoUsuario.USER, "b1@acme.com", "B1"));

        Page<UsuarioSistema> page = usuarioRepository.findByEmpresaId(empresaA.getId(), PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
    }

    @Test
    void findByTipo_shouldFilterByTipo() {
        Empresa empresa = empresaRepository.save(new Empresa(null, "66666666000166", "Empresa C", null));

        usuarioRepository.save(new UsuarioSistema(null, "senha", empresa, TipoUsuario.ADMIN, "admin@acme.com", "Admin"));
        usuarioRepository.save(new UsuarioSistema(null, "senha", empresa, TipoUsuario.USER, "user@acme.com", "User"));

        Page<UsuarioSistema> page = usuarioRepository.findByTipo(TipoUsuario.ADMIN, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("admin@acme.com", page.getContent().get(0).getEmail());
    }
}
