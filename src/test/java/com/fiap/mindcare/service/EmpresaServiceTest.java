package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.EmpresaRequestDTO;
import com.fiap.mindcare.dto.EmpresaResponseDTO;
import com.fiap.mindcare.mapper.EmpresaMapper;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.service.exception.BusinessException;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper empresaMapper;

    @InjectMocks
    private EmpresaService empresaService;

    @BeforeEach
    void setUpRequestContext() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void criar_shouldRejectDuplicatedCnpj() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO("12345678901234", "Acme", "Plano A");

        when(empresaRepository.existsByCnpj("12345678901234")).thenReturn(true);

        assertThrows(BusinessException.class, () -> empresaService.criar(dto));
        verify(empresaMapper, never()).toEntity(any());
    }

    @Test
    void criar_shouldPersistAndReturnResponse() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO("12345678901234", "Acme", "Plano A");
        Empresa entity = new Empresa(null, dto.getCnpj(), dto.getNome(), dto.getPlanoSaude());
        Empresa saved = new Empresa(1L, dto.getCnpj(), dto.getNome(), dto.getPlanoSaude());

        EmpresaResponseDTO response = new EmpresaResponseDTO(1L, dto.getPlanoSaude(), dto.getNome(), dto.getCnpj());

        when(empresaRepository.existsByCnpj(dto.getCnpj())).thenReturn(false);
        when(empresaMapper.toEntity(dto)).thenReturn(entity);
        when(empresaRepository.save(entity)).thenReturn(saved);
        when(empresaMapper.toResponse(saved)).thenReturn(response);

        EmpresaResponseDTO result = empresaService.criar(dto);

        assertEquals(1L, result.getId());
        verify(empresaMapper).toEntity(dto);
        verify(empresaRepository).save(entity);
        verify(empresaMapper).toResponse(saved);
    }

    @Test
    void buscarPorId_shouldThrowWhenMissing() {
        when(empresaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> empresaService.buscarPorId(99L));
    }

    @Test
    void buscarPorId_shouldReturnResponse() {
        Empresa entity = new Empresa(1L, "12345678901234", "Acme", "Plano A");
        EmpresaResponseDTO response = new EmpresaResponseDTO(1L, "Plano A", "Acme", "12345678901234");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaMapper.toResponse(entity)).thenReturn(response);

        EmpresaResponseDTO result = empresaService.buscarPorId(1L);

        assertEquals(1L, result.getId());
        verify(empresaMapper).toResponse(entity);
    }

    @Test
    void listar_shouldMapPage() {
        Empresa e1 = new Empresa(1L, "12345678901234", "Acme", "Plano A");
        Empresa e2 = new Empresa(2L, "23456789012345", "Beta", "Plano B");
        Page<Empresa> page = new PageImpl<>(List.of(e1, e2), PageRequest.of(0, 20), 2);

        EmpresaResponseDTO r1 = new EmpresaResponseDTO(1L, "Plano A", "Acme", "12345678901234");
        EmpresaResponseDTO r2 = new EmpresaResponseDTO(2L, "Plano B", "Beta", "23456789012345");

        when(empresaRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(empresaMapper.toResponse(e1)).thenReturn(r1);
        when(empresaMapper.toResponse(e2)).thenReturn(r2);

        Page<EmpresaResponseDTO> result = empresaService.listar(PageRequest.of(0, 20));

        assertEquals(2, result.getContent().size());
        verify(empresaMapper).toResponse(e1);
        verify(empresaMapper).toResponse(e2);
    }

    @Test
    void atualizar_shouldThrowWhenMissing() {
        EmpresaRequestDTO dto = new EmpresaRequestDTO("12345678901234", "Acme", "Plano A");
        when(empresaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> empresaService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldRejectDuplicatedCnpj() {
        Empresa entity = new Empresa(1L, "11111111111111", "Acme", "Plano A");
        EmpresaRequestDTO dto = new EmpresaRequestDTO("22222222222222", "Acme", "Plano A");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaRepository.existsByCnpj("22222222222222")).thenReturn(true);

        assertThrows(BusinessException.class, () -> empresaService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldNotCheckCnpjWhenUnchanged() {
        Empresa entity = new Empresa(1L, "12345678901234", "Acme", "Plano A");
        EmpresaRequestDTO dto = new EmpresaRequestDTO("12345678901234", "Acme", "Plano A");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(empresaMapper.toResponse(any(Empresa.class)))
                .thenReturn(new EmpresaResponseDTO(1L, "Plano A", "Acme", "12345678901234"));

        empresaService.atualizar(1L, dto);

        verify(empresaRepository, never()).existsByCnpj(any());
    }

    @Test
    void atualizar_shouldPersistChanges() {
        Empresa entity = new Empresa(1L, "12345678901234", "Acme", "Plano A");
        EmpresaRequestDTO dto = new EmpresaRequestDTO("12345678901234", "Acme Nova", "Plano B");

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(empresaMapper.toResponse(any(Empresa.class)))
                .thenReturn(new EmpresaResponseDTO(1L, "Plano B", "Acme Nova", "12345678901234"));

        EmpresaResponseDTO result = empresaService.atualizar(1L, dto);

        assertEquals("Acme Nova", result.getNome());
        ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
        verify(empresaRepository).save(captor.capture());
        assertEquals("Plano B", captor.getValue().getPlanoSaude());
    }

    @Test
    void excluir_shouldThrowWhenMissing() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> empresaService.excluir(1L));
    }

    @Test
    void excluir_shouldDeleteWhenFound() {
        Empresa entity = new Empresa(1L, "12345678901234", "Acme", "Plano A");
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(entity));

        empresaService.excluir(1L);

        verify(empresaRepository).delete(entity);
    }
}
