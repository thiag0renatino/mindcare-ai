package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.TriagemRequestDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.enuns.RiscoTriagem;
import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.mapper.TriagemMapper;
import com.fiap.mindcare.model.Triagem;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.TriagemRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.service.exception.AccessDeniedException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import com.fiap.mindcare.service.security.UsuarioAutenticadoProvider;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TriagemServiceTest {

    @Mock
    private TriagemRepository triagemRepository;

    @Mock
    private UsuarioSistemaRepository usuarioRepository;

    @Mock
    private TriagemMapper triagemMapper;

    @Mock
    private EnumMapper enumMapper;

    @Mock
    private UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    @InjectMocks
    private TriagemService triagemService;

    @BeforeEach
    void setUpRequestContext() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    private UsuarioSistema criarUsuario(Long id, TipoUsuario tipo) {
        UsuarioSistema usuario = new UsuarioSistema();
        usuario.setId(id);
        usuario.setTipo(tipo);
        return usuario;
    }

    @Test
    void criar_shouldPersistAndReturnResponse() {
        UsuarioSistema usuario = criarUsuario(10L, TipoUsuario.USER);

        TriagemRequestDTO dto = new TriagemRequestDTO(LocalDateTime.now(), "relato", "ALTO", "sugestao");
        Triagem entity = new Triagem();
        Triagem saved = new Triagem();
        saved.setId(1L);

        TriagemResponseDTO response = new TriagemResponseDTO();
        response.setId(1L);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(triagemMapper.toEntity(dto)).thenReturn(entity);
        when(enumMapper.toRiscoTriagem("ALTO")).thenReturn(RiscoTriagem.ALTO);
        when(triagemRepository.save(any(Triagem.class))).thenReturn(saved);
        when(triagemMapper.toResponse(saved)).thenReturn(response);

        TriagemResponseDTO result = triagemService.criar(dto);

        assertEquals(1L, result.getId());
        ArgumentCaptor<Triagem> captor = ArgumentCaptor.forClass(Triagem.class);
        verify(triagemRepository).save(captor.capture());
        assertEquals(usuario, captor.getValue().getUsuario());
        assertEquals(RiscoTriagem.ALTO, captor.getValue().getRisco());
    }

    @Test
    void buscarPorId_shouldThrowWhenMissing() {
        when(triagemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> triagemService.buscarPorId(1L));
    }

    @Test
    void buscarPorId_shouldReturnResponse() {
        UsuarioSistema usuario = criarUsuario(10L, TipoUsuario.USER);

        Triagem entity = new Triagem();
        entity.setId(1L);
        entity.setUsuario(usuario);

        TriagemResponseDTO response = new TriagemResponseDTO();
        response.setId(1L);

        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);
        when(triagemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(triagemMapper.toResponse(entity)).thenReturn(response);

        TriagemResponseDTO result = triagemService.buscarPorId(1L);

        assertEquals(1L, result.getId());
        verify(triagemMapper).toResponse(entity);
    }

    @Test
    void listar_shouldMapPage() {
        Triagem t1 = new Triagem();
        t1.setId(1L);
        Triagem t2 = new Triagem();
        t2.setId(2L);
        Page<Triagem> page = new PageImpl<>(List.of(t1, t2), PageRequest.of(0, 20), 2);

        TriagemResponseDTO r1 = new TriagemResponseDTO();
        r1.setId(1L);
        TriagemResponseDTO r2 = new TriagemResponseDTO();
        r2.setId(2L);

        when(triagemRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(triagemMapper.toResponse(t1)).thenReturn(r1);
        when(triagemMapper.toResponse(t2)).thenReturn(r2);

        Page<TriagemResponseDTO> result = triagemService.listar(PageRequest.of(0, 20));

        assertEquals(2, result.getContent().size());
        verify(triagemMapper).toResponse(t1);
        verify(triagemMapper).toResponse(t2);
    }

    @Test
    void listarPorUsuario_shouldAllowOwner() {
        UsuarioSistema usuario = criarUsuario(10L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);

        Triagem t1 = new Triagem();
        t1.setId(1L);
        Page<Triagem> page = new PageImpl<>(List.of(t1), PageRequest.of(0, 20), 1);

        TriagemResponseDTO r1 = new TriagemResponseDTO();
        r1.setId(1L);

        when(triagemRepository.findByUsuarioIdOrderByDataHoraDesc(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(triagemMapper.toResponse(t1)).thenReturn(r1);

        Page<TriagemResponseDTO> result = triagemService.listarPorUsuario(10L, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(triagemMapper).toResponse(t1);
    }

    @Test
    void listarPorUsuario_shouldAllowAdmin() {
        UsuarioSistema admin = criarUsuario(99L, TipoUsuario.ADMIN);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(admin);

        Triagem t1 = new Triagem();
        t1.setId(1L);
        Page<Triagem> page = new PageImpl<>(List.of(t1), PageRequest.of(0, 20), 1);

        TriagemResponseDTO r1 = new TriagemResponseDTO();
        r1.setId(1L);

        when(triagemRepository.findByUsuarioIdOrderByDataHoraDesc(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(triagemMapper.toResponse(t1)).thenReturn(r1);

        Page<TriagemResponseDTO> result = triagemService.listarPorUsuario(10L, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
    }

    @Test
    void listarPorUsuario_shouldThrowWhenNotOwnerNorAdmin() {
        UsuarioSistema usuario = criarUsuario(20L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);

        assertThrows(AccessDeniedException.class, () -> triagemService.listarPorUsuario(10L, PageRequest.of(0, 20)));
    }

    @Test
    void atualizar_shouldThrowWhenTriagemMissing() {
        UsuarioSistema usuario = criarUsuario(10L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);

        TriagemRequestDTO dto = new TriagemRequestDTO(LocalDateTime.now(), "relato", "ALTO", "sugestao");
        when(triagemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> triagemService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldThrowWhenNotOwnerNorAdmin() {
        UsuarioSistema autenticado = criarUsuario(20L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(autenticado);

        UsuarioSistema owner = criarUsuario(10L, TipoUsuario.USER);
        Triagem entity = new Triagem();
        entity.setId(1L);
        entity.setUsuario(owner);

        when(triagemRepository.findById(1L)).thenReturn(Optional.of(entity));

        TriagemRequestDTO dto = new TriagemRequestDTO(LocalDateTime.now(), "relato", "ALTO", "sugestao");
        assertThrows(AccessDeniedException.class, () -> triagemService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldPersistChangesForOwner() {
        UsuarioSistema usuario = criarUsuario(10L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);

        Triagem entity = new Triagem();
        entity.setId(1L);
        entity.setUsuario(usuario);

        LocalDateTime dataHora = LocalDateTime.now();
        TriagemRequestDTO dto = new TriagemRequestDTO(dataHora, "relato novo", "MODERADO", "sugestao nova");

        when(triagemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enumMapper.toRiscoTriagem("MODERADO")).thenReturn(RiscoTriagem.MODERADO);
        when(triagemRepository.save(any(Triagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(triagemMapper.toResponse(any(Triagem.class))).thenReturn(new TriagemResponseDTO());

        triagemService.atualizar(1L, dto);

        ArgumentCaptor<Triagem> captor = ArgumentCaptor.forClass(Triagem.class);
        verify(triagemRepository).save(captor.capture());
        Triagem saved = captor.getValue();
        assertEquals(usuario, saved.getUsuario());
        assertEquals(RiscoTriagem.MODERADO, saved.getRisco());
        assertEquals("relato novo", saved.getRelato());
        assertEquals("sugestao nova", saved.getSugestao());
        assertEquals(dataHora, saved.getDataHora());
    }

    @Test
    void atualizar_shouldAllowAdmin() {
        UsuarioSistema admin = criarUsuario(99L, TipoUsuario.ADMIN);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(admin);

        UsuarioSistema owner = criarUsuario(10L, TipoUsuario.USER);
        Triagem entity = new Triagem();
        entity.setId(1L);
        entity.setUsuario(owner);

        TriagemRequestDTO dto = new TriagemRequestDTO(LocalDateTime.now(), "relato", "ALTO", "sugestao");

        when(triagemRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(enumMapper.toRiscoTriagem("ALTO")).thenReturn(RiscoTriagem.ALTO);
        when(triagemRepository.save(any(Triagem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(triagemMapper.toResponse(any(Triagem.class))).thenReturn(new TriagemResponseDTO());

        triagemService.atualizar(1L, dto);

        verify(triagemRepository).save(any(Triagem.class));
    }

    @Test
    void excluir_shouldThrowWhenMissing() {
        UsuarioSistema usuario = criarUsuario(10L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);

        when(triagemRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> triagemService.excluir(1L));
    }

    @Test
    void excluir_shouldDeleteWhenOwner() {
        UsuarioSistema usuario = criarUsuario(10L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(usuario);

        Triagem entity = new Triagem();
        entity.setId(1L);
        entity.setUsuario(usuario);
        when(triagemRepository.findById(1L)).thenReturn(Optional.of(entity));

        triagemService.excluir(1L);

        verify(triagemRepository).delete(entity);
    }

    @Test
    void excluir_shouldThrowWhenNotOwnerNorAdmin() {
        UsuarioSistema autenticado = criarUsuario(20L, TipoUsuario.USER);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(autenticado);

        UsuarioSistema owner = criarUsuario(10L, TipoUsuario.USER);
        Triagem entity = new Triagem();
        entity.setId(1L);
        entity.setUsuario(owner);
        when(triagemRepository.findById(1L)).thenReturn(Optional.of(entity));

        assertThrows(AccessDeniedException.class, () -> triagemService.excluir(1L));
    }

    @Test
    void excluir_shouldAllowAdmin() {
        UsuarioSistema admin = criarUsuario(99L, TipoUsuario.ADMIN);
        when(usuarioAutenticadoProvider.getUsuarioAutenticado()).thenReturn(admin);

        UsuarioSistema owner = criarUsuario(10L, TipoUsuario.USER);
        Triagem entity = new Triagem();
        entity.setId(1L);
        entity.setUsuario(owner);
        when(triagemRepository.findById(1L)).thenReturn(Optional.of(entity));

        triagemService.excluir(1L);

        verify(triagemRepository).delete(entity);
    }
}
