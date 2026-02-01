package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.EmpresaResponseDTO;
import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.dto.UsuarioResponseDTO;
import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.mapper.UsuarioMapper;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.service.exception.BusinessException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import com.fiap.mindcare.service.security.PasswordValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioSistemaRepository usuarioRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private EnumMapper enumMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordValidator passwordPolicyValidator;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUpRequestContext() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void buscarPorId_shouldReturnResponse() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setId(1L);
        entity.setEmail("ana@acme.com");

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(1L);
        response.setEmail("ana@acme.com");
        response.setEmpresa(new EmpresaResponseDTO(10L, null, "Acme", "12345678901234"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(usuarioMapper.toResponse(entity)).thenReturn(response);

        UsuarioResponseDTO result = usuarioService.buscarPorId(1L);

        assertEquals(1L, result.getId());
        assertEquals("ana@acme.com", result.getEmail());
        verify(usuarioMapper).toResponse(entity);
    }

    @Test
    void buscarPorId_shouldThrowWhenMissing() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setEmail("ana@acme.com");

        when(usuarioRepository.findByEmail("ana@acme.com")).thenReturn(Optional.of(entity));

        var result = usuarioService.loadUserByUsername("ana@acme.com");

        assertSame(entity, result);
    }

    @Test
    void loadUserByUsername_shouldThrowWhenMissing() {
        when(usuarioRepository.findByEmail("missing@acme.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("missing@acme.com"));
    }

    @Test
    void listar_shouldMapPage() {
        UsuarioSistema u1 = new UsuarioSistema();
        u1.setId(1L);
        UsuarioSistema u2 = new UsuarioSistema();
        u2.setId(2L);

        Page<UsuarioSistema> page = new PageImpl<>(List.of(u1, u2), PageRequest.of(0, 20), 2);
        when(usuarioRepository.findAll(any(Pageable.class))).thenReturn(page);

        UsuarioResponseDTO r1 = new UsuarioResponseDTO();
        r1.setId(1L);
        UsuarioResponseDTO r2 = new UsuarioResponseDTO();
        r2.setId(2L);
        when(usuarioMapper.toResponse(u1)).thenReturn(r1);
        when(usuarioMapper.toResponse(u2)).thenReturn(r2);

        Page<UsuarioResponseDTO> result = usuarioService.listar(PageRequest.of(0, 20));

        assertEquals(2, result.getContent().size());
        verify(usuarioMapper).toResponse(u1);
        verify(usuarioMapper).toResponse(u2);
    }

    @Test
    void listarPorEmpresa_shouldMapPage() {
        UsuarioSistema u1 = new UsuarioSistema();
        u1.setId(1L);

        Page<UsuarioSistema> page = new PageImpl<>(List.of(u1), PageRequest.of(0, 20), 1);
        when(usuarioRepository.findByEmpresaId(any(Long.class), any(Pageable.class))).thenReturn(page);

        UsuarioResponseDTO r1 = new UsuarioResponseDTO();
        r1.setId(1L);
        when(usuarioMapper.toResponse(u1)).thenReturn(r1);

        Page<UsuarioResponseDTO> result = usuarioService.listarPorEmpresa(10L, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(usuarioMapper).toResponse(u1);
    }

    @Test
    void me_shouldReturnUsuarioByPrincipal() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setEmail("ana@acme.com");

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setEmail("ana@acme.com");

        when(usuarioRepository.findByEmail("ana@acme.com")).thenReturn(Optional.of(entity));
        when(usuarioMapper.toResponse(entity)).thenReturn(response);

        Principal principal = () -> "ana@acme.com";
        UsuarioResponseDTO result = usuarioService.me(principal);

        assertEquals("ana@acme.com", result.getEmail());
        verify(usuarioRepository).findByEmail("ana@acme.com");
    }

    @Test
    void findByEmail_shouldReturnResponse() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setEmail("ana@acme.com");

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setEmail("ana@acme.com");

        when(usuarioRepository.findByEmail("ana@acme.com")).thenReturn(Optional.of(entity));
        when(usuarioMapper.toResponse(entity)).thenReturn(response);

        UsuarioResponseDTO result = usuarioService.findByEmail("ana@acme.com");

        assertEquals("ana@acme.com", result.getEmail());
    }

    @Test
    void findByEmail_shouldThrowWhenMissing() {
        when(usuarioRepository.findByEmail("missing@acme.com")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.findByEmail("missing@acme.com"));
    }

    @Test
    void atualizar_shouldRejectEmailWhenAlreadyUsed() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setId(1L);
        entity.setEmail("old@acme.com");

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Ana", "new@acme.com", "SenhaForte9", "USER", 10L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(usuarioRepository.existsByEmail("new@acme.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> usuarioService.atualizar(1L, dto));
        verify(empresaRepository, never()).findById(any());
    }

    @Test
    void atualizar_shouldKeepPasswordWhenBlank() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setId(1L);
        entity.setEmail("ana@acme.com");
        entity.setSenha("senha-atual");

        Empresa empresa = new Empresa(10L, "12345678901234", "Acme", null);

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Ana", "ana@acme.com", "", "USER", 10L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
        when(enumMapper.toTipoUsuario("USER")).thenReturn(TipoUsuario.USER);
        when(usuarioRepository.save(any(UsuarioSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(1L);
        response.setEmpresa(new EmpresaResponseDTO(10L, null, "Acme", "12345678901234"));
        when(usuarioMapper.toResponse(any(UsuarioSistema.class))).thenReturn(response);

        usuarioService.atualizar(1L, dto);

        ArgumentCaptor<UsuarioSistema> captor = ArgumentCaptor.forClass(UsuarioSistema.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("senha-atual", captor.getValue().getSenha());

        verify(passwordPolicyValidator, never()).validate(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void atualizar_shouldThrowWhenUserMissing() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Ana", "ana@acme.com", "SenhaForte9", "USER", 10L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldThrowWhenEmpresaMissing() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setId(1L);
        entity.setEmail("ana@acme.com");

        UsuarioRequestDTO dto = new UsuarioRequestDTO("Ana", "ana@acme.com", "SenhaForte9", "USER", 10L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldNotCheckEmailWhenUnchanged() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setId(1L);
        entity.setEmail("ana@acme.com");

        Empresa empresa = new Empresa(10L, "12345678901234", "Acme", null);
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Ana", "ana@acme.com", "", "USER", 10L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
        when(enumMapper.toTipoUsuario("USER")).thenReturn(TipoUsuario.USER);
        when(usuarioRepository.save(any(UsuarioSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(1L);
        when(usuarioMapper.toResponse(any(UsuarioSistema.class))).thenReturn(response);

        usuarioService.atualizar(1L, dto);

        verify(usuarioRepository, never()).existsByEmail(any());
    }

    @Test
    void atualizar_shouldEncodePasswordWhenProvided() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setId(1L);
        entity.setEmail("ana@acme.com");
        entity.setSenha("senha-atual");

        Empresa empresa = new Empresa(10L, "12345678901234", "Acme", null);
        UsuarioRequestDTO dto = new UsuarioRequestDTO("Ana", "ana@acme.com", "NovaSenha9", "USER", 10L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaRepository.findById(10L)).thenReturn(Optional.of(empresa));
        when(enumMapper.toTipoUsuario("USER")).thenReturn(TipoUsuario.USER);
        when(passwordEncoder.encode("NovaSenha9")).thenReturn("encoded");
        when(usuarioRepository.save(any(UsuarioSistema.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = new UsuarioResponseDTO();
        response.setId(1L);
        response.setEmpresa(new EmpresaResponseDTO(10L, null, "Acme", "12345678901234"));
        when(usuarioMapper.toResponse(any(UsuarioSistema.class))).thenReturn(response);

        usuarioService.atualizar(1L, dto);

        ArgumentCaptor<UsuarioSistema> captor = ArgumentCaptor.forClass(UsuarioSistema.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("encoded", captor.getValue().getSenha());
        verify(passwordPolicyValidator).validate("NovaSenha9");
    }

    @Test
    void excluir_shouldDeleteWhenFound() {
        UsuarioSistema entity = new UsuarioSistema();
        entity.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(entity));

        usuarioService.excluir(1L);

        verify(usuarioRepository).delete(entity);
    }

    @Test
    void excluir_shouldThrowWhenMissing() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.excluir(1L));
    }
}
