package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.AcompanhamentoRequestDTO;
import com.fiap.mindcare.dto.AcompanhamentoResponseDTO;
import com.fiap.mindcare.enuns.TipoEventoAcompanhamento;
import com.fiap.mindcare.mapper.AcompanhamentoMapper;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.model.Acompanhamento;
import com.fiap.mindcare.model.Encaminhamento;
import com.fiap.mindcare.repository.AcompanhamentoRepository;
import com.fiap.mindcare.repository.EncaminhamentoRepository;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
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
class AcompanhamentoServiceTest {

    @Mock
    private AcompanhamentoRepository acompanhamentoRepository;

    @Mock
    private EncaminhamentoRepository encaminhamentoRepository;

    @Mock
    private AcompanhamentoMapper acompanhamentoMapper;

    @Mock
    private EnumMapper enumMapper;

    @InjectMocks
    private AcompanhamentoService acompanhamentoService;

    @BeforeEach
    void setUpRequestContext() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void criar_shouldThrowWhenEncaminhamentoMissing() {
        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(10L, "url", "desc", "AGENDAMENTO", LocalDateTime.now());
        when(encaminhamentoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> acompanhamentoService.criar(dto));
    }

    @Test
    void criar_shouldPersistAndReturnResponse() {
        Encaminhamento encaminhamento = new Encaminhamento();
        encaminhamento.setId(10L);

        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(10L, "url", "desc", "AGENDAMENTO", LocalDateTime.now());
        Acompanhamento entity = new Acompanhamento();
        Acompanhamento saved = new Acompanhamento();
        saved.setId(1L);

        AcompanhamentoResponseDTO response = new AcompanhamentoResponseDTO();
        response.setId(1L);

        when(acompanhamentoMapper.toEntity(dto)).thenReturn(entity);
        when(encaminhamentoRepository.findById(10L)).thenReturn(Optional.of(encaminhamento));
        when(enumMapper.toTipoEventoAcompanhamento("AGENDAMENTO")).thenReturn(TipoEventoAcompanhamento.AGENDAMENTO);
        when(acompanhamentoRepository.save(any(Acompanhamento.class))).thenReturn(saved);
        when(acompanhamentoMapper.toResponse(saved)).thenReturn(response);

        AcompanhamentoResponseDTO result = acompanhamentoService.criar(dto);

        assertEquals(1L, result.getId());
        ArgumentCaptor<Acompanhamento> captor = ArgumentCaptor.forClass(Acompanhamento.class);
        verify(acompanhamentoRepository).save(captor.capture());
        assertEquals(encaminhamento, captor.getValue().getEncaminhamento());
        assertEquals(TipoEventoAcompanhamento.AGENDAMENTO, captor.getValue().getTipoEvento());
    }

    @Test
    void buscarPorId_shouldThrowWhenMissing() {
        when(acompanhamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> acompanhamentoService.buscarPorId(1L));
    }

    @Test
    void buscarPorId_shouldReturnResponse() {
        Acompanhamento entity = new Acompanhamento();
        entity.setId(1L);
        AcompanhamentoResponseDTO response = new AcompanhamentoResponseDTO();
        response.setId(1L);

        when(acompanhamentoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(acompanhamentoMapper.toResponse(entity)).thenReturn(response);

        AcompanhamentoResponseDTO result = acompanhamentoService.buscarPorId(1L);

        assertEquals(1L, result.getId());
        verify(acompanhamentoMapper).toResponse(entity);
    }

    @Test
    void listar_shouldMapPage() {
        Acompanhamento a1 = new Acompanhamento();
        a1.setId(1L);
        Acompanhamento a2 = new Acompanhamento();
        a2.setId(2L);
        Page<Acompanhamento> page = new PageImpl<>(List.of(a1, a2), PageRequest.of(0, 20), 2);

        AcompanhamentoResponseDTO r1 = new AcompanhamentoResponseDTO();
        r1.setId(1L);
        AcompanhamentoResponseDTO r2 = new AcompanhamentoResponseDTO();
        r2.setId(2L);

        when(acompanhamentoRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(acompanhamentoMapper.toResponse(a1)).thenReturn(r1);
        when(acompanhamentoMapper.toResponse(a2)).thenReturn(r2);

        Page<AcompanhamentoResponseDTO> result = acompanhamentoService.listar(PageRequest.of(0, 20));

        assertEquals(2, result.getContent().size());
        verify(acompanhamentoMapper).toResponse(a1);
        verify(acompanhamentoMapper).toResponse(a2);
    }

    @Test
    void listarPorEncaminhamento_shouldMapPage() {
        Acompanhamento a1 = new Acompanhamento();
        a1.setId(1L);
        Page<Acompanhamento> page = new PageImpl<>(List.of(a1), PageRequest.of(0, 20), 1);

        AcompanhamentoResponseDTO r1 = new AcompanhamentoResponseDTO();
        r1.setId(1L);

        when(acompanhamentoRepository.findByEncaminhamentoIdOrderByDataEventoDesc(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(acompanhamentoMapper.toResponse(a1)).thenReturn(r1);

        Page<AcompanhamentoResponseDTO> result = acompanhamentoService.listarPorEncaminhamento(10L, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(acompanhamentoMapper).toResponse(a1);
    }

    @Test
    void atualizar_shouldThrowWhenMissing() {
        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(10L, "url", "desc", "AGENDAMENTO", LocalDateTime.now());
        when(acompanhamentoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> acompanhamentoService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldThrowWhenEncaminhamentoMissing() {
        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(10L, "url", "desc", "AGENDAMENTO", LocalDateTime.now());
        when(acompanhamentoRepository.findById(1L)).thenReturn(Optional.of(new Acompanhamento()));
        when(encaminhamentoRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> acompanhamentoService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldPersistChanges() {
        Encaminhamento encaminhamento = new Encaminhamento();
        encaminhamento.setId(10L);

        Acompanhamento entity = new Acompanhamento();
        entity.setId(1L);

        LocalDateTime dataEvento = LocalDateTime.now();
        AcompanhamentoRequestDTO dto = new AcompanhamentoRequestDTO(10L, "url", "desc", "RESULTADO", dataEvento);

        when(acompanhamentoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(encaminhamentoRepository.findById(10L)).thenReturn(Optional.of(encaminhamento));
        when(enumMapper.toTipoEventoAcompanhamento("RESULTADO")).thenReturn(TipoEventoAcompanhamento.RESULTADO);
        when(acompanhamentoRepository.save(any(Acompanhamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(acompanhamentoMapper.toResponse(any(Acompanhamento.class))).thenReturn(new AcompanhamentoResponseDTO());

        acompanhamentoService.atualizar(1L, dto);

        ArgumentCaptor<Acompanhamento> captor = ArgumentCaptor.forClass(Acompanhamento.class);
        verify(acompanhamentoRepository).save(captor.capture());
        Acompanhamento saved = captor.getValue();
        assertEquals(encaminhamento, saved.getEncaminhamento());
        assertEquals(TipoEventoAcompanhamento.RESULTADO, saved.getTipoEvento());
        assertEquals("desc", saved.getDescricao());
        assertEquals("url", saved.getAnexoUrl());
        assertEquals(dataEvento, saved.getDataEvento());
    }

    @Test
    void excluir_shouldThrowWhenMissing() {
        when(acompanhamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> acompanhamentoService.excluir(1L));
    }

    @Test
    void excluir_shouldDeleteWhenFound() {
        Acompanhamento entity = new Acompanhamento();
        entity.setId(1L);
        when(acompanhamentoRepository.findById(1L)).thenReturn(Optional.of(entity));

        acompanhamentoService.excluir(1L);

        verify(acompanhamentoRepository).delete(entity);
    }
}
