package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.EncaminhamentoRecomendadoDTO;
import com.fiap.mindcare.dto.EncaminhamentoRequestDTO;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
import com.fiap.mindcare.enuns.PrioridadeEncaminhamento;
import com.fiap.mindcare.enuns.StatusEncaminhamento;
import com.fiap.mindcare.enuns.TipoEncaminhamento;
import com.fiap.mindcare.mapper.EncaminhamentoMapper;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.Encaminhamento;
import com.fiap.mindcare.model.Profissional;
import com.fiap.mindcare.model.Triagem;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.EncaminhamentoRepository;
import com.fiap.mindcare.repository.ProfissionalRepository;
import com.fiap.mindcare.repository.TriagemRepository;
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
class EncaminhamentoServiceTest {

    @Mock
    private EncaminhamentoRepository encaminhamentoRepository;

    @Mock
    private TriagemRepository triagemRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private ProfissionalRepository profissionalRepository;

    @Mock
    private EncaminhamentoMapper encaminhamentoMapper;

    @Mock
    private EnumMapper enumMapper;

    @InjectMocks
    private EncaminhamentoService encaminhamentoService;

    @BeforeEach
    void setUpRequestContext() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDownRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void criar_shouldThrowWhenTriagemMissing() {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null);
        when(encaminhamentoMapper.toEntity(dto)).thenReturn(new Encaminhamento());
        when(triagemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.criar(dto));
    }

    @Test
    void criar_shouldThrowWhenProfissionalMissing() {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, 20L, "obs", "Psicologia", "MEDIA", "PENDENTE", null);
        when(encaminhamentoMapper.toEntity(dto)).thenReturn(new Encaminhamento());
        when(triagemRepository.findById(10L)).thenReturn(Optional.of(new Triagem()));
        when(profissionalRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.criar(dto));
    }

    @Test
    void criar_shouldPersistAndReturnResponse() {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, 20L, "obs", "Psicologia", "MEDIA", "PENDENTE", null);

        Triagem triagem = new Triagem();
        triagem.setId(10L);
        Profissional profissional = new Profissional();
        profissional.setId(20L);

        Encaminhamento entity = new Encaminhamento();
        Encaminhamento saved = new Encaminhamento();
        saved.setId(1L);

        EncaminhamentoResponseDTO response = new EncaminhamentoResponseDTO();
        response.setId(1L);

        when(encaminhamentoMapper.toEntity(dto)).thenReturn(entity);
        when(triagemRepository.findById(10L)).thenReturn(Optional.of(triagem));
        when(profissionalRepository.findById(20L)).thenReturn(Optional.of(profissional));
        when(enumMapper.toTipoEncaminhamento("ESPECIALIDADE")).thenReturn(TipoEncaminhamento.ESPECIALIDADE);
        when(enumMapper.toPrioridadeEncaminhamento("MEDIA")).thenReturn(PrioridadeEncaminhamento.MEDIA);
        when(enumMapper.toStatusEncaminhamento("PENDENTE")).thenReturn(StatusEncaminhamento.PENDENTE);
        when(encaminhamentoRepository.save(any(Encaminhamento.class))).thenReturn(saved);
        when(encaminhamentoMapper.toResponse(saved)).thenReturn(response);

        EncaminhamentoResponseDTO result = encaminhamentoService.criar(dto);

        assertEquals(1L, result.getId());
        ArgumentCaptor<Encaminhamento> captor = ArgumentCaptor.forClass(Encaminhamento.class);
        verify(encaminhamentoRepository).save(captor.capture());
        Encaminhamento savedEntity = captor.getValue();
        assertEquals(triagem, savedEntity.getTriagem());
        assertEquals(profissional, savedEntity.getProfissional());
        assertEquals(TipoEncaminhamento.ESPECIALIDADE, savedEntity.getTipo());
        assertEquals(PrioridadeEncaminhamento.MEDIA, savedEntity.getPrioridade());
        assertEquals(StatusEncaminhamento.PENDENTE, savedEntity.getStatus());
    }

    @Test
    void criar_shouldNotLookupProfissionalWhenNull() {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null);
        when(encaminhamentoMapper.toEntity(dto)).thenReturn(new Encaminhamento());
        when(triagemRepository.findById(10L)).thenReturn(Optional.of(new Triagem()));
        when(enumMapper.toTipoEncaminhamento("ESPECIALIDADE")).thenReturn(TipoEncaminhamento.ESPECIALIDADE);
        when(encaminhamentoRepository.save(any(Encaminhamento.class))).thenReturn(new Encaminhamento());
        when(encaminhamentoMapper.toResponse(any(Encaminhamento.class))).thenReturn(new EncaminhamentoResponseDTO());

        encaminhamentoService.criar(dto);

        verify(profissionalRepository, never()).findById(any());
    }

    @Test
    void buscarPorId_shouldThrowWhenMissing() {
        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.buscarPorId(1L));
    }

    @Test
    void buscarPorId_shouldReturnResponse() {
        Encaminhamento entity = new Encaminhamento();
        entity.setId(1L);
        EncaminhamentoResponseDTO response = new EncaminhamentoResponseDTO();
        response.setId(1L);

        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(encaminhamentoMapper.toResponse(entity)).thenReturn(response);

        EncaminhamentoResponseDTO result = encaminhamentoService.buscarPorId(1L);

        assertEquals(1L, result.getId());
        verify(encaminhamentoMapper).toResponse(entity);
    }

    @Test
    void listar_shouldMapPage() {
        Encaminhamento e1 = new Encaminhamento();
        e1.setId(1L);
        Encaminhamento e2 = new Encaminhamento();
        e2.setId(2L);
        Page<Encaminhamento> page = new PageImpl<>(List.of(e1, e2), PageRequest.of(0, 20), 2);

        EncaminhamentoResponseDTO r1 = new EncaminhamentoResponseDTO();
        r1.setId(1L);
        EncaminhamentoResponseDTO r2 = new EncaminhamentoResponseDTO();
        r2.setId(2L);

        when(encaminhamentoRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(encaminhamentoMapper.toResponse(e1)).thenReturn(r1);
        when(encaminhamentoMapper.toResponse(e2)).thenReturn(r2);

        Page<EncaminhamentoResponseDTO> result = encaminhamentoService.listar(PageRequest.of(0, 20));

        assertEquals(2, result.getContent().size());
        verify(encaminhamentoMapper).toResponse(e1);
        verify(encaminhamentoMapper).toResponse(e2);
    }

    @Test
    void listarPorTriagem_shouldMapPage() {
        Encaminhamento e1 = new Encaminhamento();
        e1.setId(1L);
        Page<Encaminhamento> page = new PageImpl<>(List.of(e1), PageRequest.of(0, 20), 1);

        EncaminhamentoResponseDTO r1 = new EncaminhamentoResponseDTO();
        r1.setId(1L);

        when(encaminhamentoRepository.findByTriagemId(any(Long.class), any(Pageable.class))).thenReturn(page);
        when(encaminhamentoMapper.toResponse(e1)).thenReturn(r1);

        Page<EncaminhamentoResponseDTO> result = encaminhamentoService.listarPorTriagem(10L, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(encaminhamentoMapper).toResponse(e1);
    }

    @Test
    void atualizar_shouldThrowWhenMissing() {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null);
        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldThrowWhenTriagemMissing() {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null);
        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.of(new Encaminhamento()));
        when(triagemRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldThrowWhenProfissionalMissing() {
        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, 20L, "obs", "Psicologia", "MEDIA", "PENDENTE", null);
        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.of(new Encaminhamento()));
        when(triagemRepository.findById(10L)).thenReturn(Optional.of(new Triagem()));
        when(profissionalRepository.findById(20L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.atualizar(1L, dto));
    }

    @Test
    void atualizar_shouldSetProfissionalNullWhenMissingInDto() {
        Encaminhamento entity = new Encaminhamento();
        entity.setId(1L);
        entity.setProfissional(new Profissional());

        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, null, "obs", "Psicologia", "MEDIA", "PENDENTE", null);

        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(triagemRepository.findById(10L)).thenReturn(Optional.of(new Triagem()));
        when(enumMapper.toTipoEncaminhamento("ESPECIALIDADE")).thenReturn(TipoEncaminhamento.ESPECIALIDADE);
        when(enumMapper.toPrioridadeEncaminhamento("MEDIA")).thenReturn(PrioridadeEncaminhamento.MEDIA);
        when(enumMapper.toStatusEncaminhamento("PENDENTE")).thenReturn(StatusEncaminhamento.PENDENTE);
        when(encaminhamentoRepository.save(any(Encaminhamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(encaminhamentoMapper.toResponse(any(Encaminhamento.class))).thenReturn(new EncaminhamentoResponseDTO());

        encaminhamentoService.atualizar(1L, dto);

        ArgumentCaptor<Encaminhamento> captor = ArgumentCaptor.forClass(Encaminhamento.class);
        verify(encaminhamentoRepository).save(captor.capture());
        assertEquals(null, captor.getValue().getProfissional());
    }

    @Test
    void atualizar_shouldPersistChanges() {
        Encaminhamento entity = new Encaminhamento();
        entity.setId(1L);

        EncaminhamentoRequestDTO dto = new EncaminhamentoRequestDTO("ESPECIALIDADE", 10L, 20L, "obs", "Psicologia", "MEDIA", "PENDENTE", "Exame X");

        Triagem triagem = new Triagem();
        Profissional profissional = new Profissional();

        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(triagemRepository.findById(10L)).thenReturn(Optional.of(triagem));
        when(profissionalRepository.findById(20L)).thenReturn(Optional.of(profissional));
        when(enumMapper.toTipoEncaminhamento("ESPECIALIDADE")).thenReturn(TipoEncaminhamento.ESPECIALIDADE);
        when(enumMapper.toPrioridadeEncaminhamento("MEDIA")).thenReturn(PrioridadeEncaminhamento.MEDIA);
        when(enumMapper.toStatusEncaminhamento("PENDENTE")).thenReturn(StatusEncaminhamento.PENDENTE);
        when(encaminhamentoRepository.save(any(Encaminhamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(encaminhamentoMapper.toResponse(any(Encaminhamento.class))).thenReturn(new EncaminhamentoResponseDTO());

        encaminhamentoService.atualizar(1L, dto);

        ArgumentCaptor<Encaminhamento> captor = ArgumentCaptor.forClass(Encaminhamento.class);
        verify(encaminhamentoRepository).save(captor.capture());
        Encaminhamento saved = captor.getValue();
        assertEquals(triagem, saved.getTriagem());
        assertEquals(profissional, saved.getProfissional());
        assertEquals("Exame X", saved.getExame());
        assertEquals("Psicologia", saved.getEspecialidade());
        assertEquals("obs", saved.getObservacao());
    }

    @Test
    void listarRecomendados_shouldThrowWhenEmpresaMissing() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.listarRecomendados(1L, "Psi", PageRequest.of(0, 20)));
    }

    @Test
    void listarRecomendados_shouldMapPage() {
        Empresa empresa = new Empresa(1L, "12345678901234", "Acme", "Plano A");
        Profissional profissional = new Profissional();
        profissional.setId(10L);
        profissional.setNome("Dra. Ana");
        profissional.setEspecialidade("Psicologia");
        profissional.setContato("9999");
        profissional.setConvenio("Plano A");

        Page<Profissional> page = new PageImpl<>(List.of(profissional), PageRequest.of(0, 20), 1);

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(profissionalRepository.findByEspecialidadeContainingIgnoreCaseAndConvenioContainingIgnoreCase(any(String.class), any(String.class), any(Pageable.class)))
                .thenReturn(page);

        Page<EncaminhamentoRecomendadoDTO> result = encaminhamentoService.listarRecomendados(1L, "Psi", PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        EncaminhamentoRecomendadoDTO dto = result.getContent().get(0);
        assertEquals(10L, dto.getProfissionalId());
        assertEquals("Dra. Ana", dto.getNome());
    }

    @Test
    void excluir_shouldThrowWhenMissing() {
        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> encaminhamentoService.excluir(1L));
    }

    @Test
    void excluir_shouldDeleteWhenFound() {
        Encaminhamento entity = new Encaminhamento();
        entity.setId(1L);
        when(encaminhamentoRepository.findById(1L)).thenReturn(Optional.of(entity));

        encaminhamentoService.excluir(1L);

        verify(encaminhamentoRepository).delete(entity);
    }
}
