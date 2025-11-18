package com.fiap.mindcare.service;

import com.fiap.mindcare.controller.EncaminhamentoController;
import com.fiap.mindcare.dto.EncaminhamentoRecomendadoDTO;
import com.fiap.mindcare.dto.EncaminhamentoRequestDTO;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class EncaminhamentoService {

    private final EncaminhamentoRepository encaminhamentoRepository;
    private final TriagemRepository triagemRepository;
    private final EmpresaRepository empresaRepository;
    private final ProfissionalRepository profissionalRepository;
    private final EncaminhamentoMapper encaminhamentoMapper;
    private final EnumMapper enumMapper;

    public EncaminhamentoService(EncaminhamentoRepository encaminhamentoRepository, TriagemRepository triagemRepository, EmpresaRepository empresaRepository, ProfissionalRepository profissionalRepository, EncaminhamentoMapper encaminhamentoMapper, EnumMapper enumMapper) {
        this.encaminhamentoRepository = encaminhamentoRepository;
        this.triagemRepository = triagemRepository;
        this.empresaRepository = empresaRepository;
        this.profissionalRepository = profissionalRepository;
        this.encaminhamentoMapper = encaminhamentoMapper;
        this.enumMapper = enumMapper;
    }

    @Transactional
    public EncaminhamentoResponseDTO criar(EncaminhamentoRequestDTO dto) {
        Encaminhamento entity = encaminhamentoMapper.toEntity(dto);

        Triagem triagem = triagemRepository.findById(dto.getTriagemId())
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));
        entity.setTriagem(triagem);

        if (dto.getProfissionalId() != null) {
            Profissional profissional = profissionalRepository.findById(dto.getProfissionalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
            entity.setProfissional(profissional);
        }

        entity.setTipo(enumMapper.toTipoEncaminhamento(dto.getTipo()));

        if (dto.getPrioridade() != null) {
            entity.setPrioridade(enumMapper.toPrioridadeEncaminhamento(dto.getPrioridade()));
        }

        if (dto.getStatus() != null) {
            entity.setStatus(enumMapper.toStatusEncaminhamento(dto.getStatus()));
        }

        entity.setExame(dto.getExame() != null ? dto.getExame() : entity.getExame());
        entity.setEspecialidade(dto.getEspecialidade() != null ? dto.getEspecialidade() : entity.getEspecialidade());
        entity.setObservacao(dto.getObservacao() != null ? dto.getObservacao() : entity.getObservacao());

        entity = encaminhamentoRepository.save(entity);

        EncaminhamentoResponseDTO response = encaminhamentoMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    public EncaminhamentoResponseDTO buscarPorId(Long id) {
        Encaminhamento entity = encaminhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));
        EncaminhamentoResponseDTO dto = encaminhamentoMapper.toResponse(entity);
        addHateoasLinks(dto);
        return dto;
    }

    public Page<EncaminhamentoResponseDTO> listar(Pageable pageable) {
        return encaminhamentoRepository.findAll(pageable)
                .map(entity -> {
                    EncaminhamentoResponseDTO dto = encaminhamentoMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    public Page<EncaminhamentoResponseDTO> listarPorTriagem(Long triagemId, Pageable pageable) {
        return encaminhamentoRepository.findByTriagemId(triagemId, pageable)
                .map(entity -> {
                    EncaminhamentoResponseDTO dto = encaminhamentoMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    @Transactional
    public EncaminhamentoResponseDTO atualizar(Long id, EncaminhamentoRequestDTO dto) {
        Encaminhamento entity = encaminhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));

        Triagem triagem = triagemRepository.findById(dto.getTriagemId())
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));
        entity.setTriagem(triagem);

        if (dto.getProfissionalId() != null) {
            Profissional profissional = profissionalRepository.findById(dto.getProfissionalId())
                    .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
            entity.setProfissional(profissional);
        } else {
            entity.setProfissional(null);
        }

        entity.setTipo(enumMapper.toTipoEncaminhamento(dto.getTipo()));

        if (dto.getPrioridade() != null) {
            entity.setPrioridade(enumMapper.toPrioridadeEncaminhamento(dto.getPrioridade()));
        }

        if (dto.getStatus() != null) {
            entity.setStatus(enumMapper.toStatusEncaminhamento(dto.getStatus()));
        }

        entity.setExame(dto.getExame());
        entity.setEspecialidade(dto.getEspecialidade());
        entity.setObservacao(dto.getObservacao());

        entity = encaminhamentoRepository.save(entity);

        EncaminhamentoResponseDTO response = encaminhamentoMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    @Transactional
    public void excluir(Long id) {
        Encaminhamento entity = encaminhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));
        encaminhamentoRepository.delete(entity);
    }

    public Page<EncaminhamentoRecomendadoDTO> listarRecomendados(Long empresaId, String especialidade, Pageable pageable) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        String especialidadeFiltro = especialidade != null ? especialidade : "";
        String convenio = empresa.getPlanoSaude() != null ? empresa.getPlanoSaude() : "";

        return profissionalRepository
                .findByEspecialidadeContainingIgnoreCaseAndConvenioContainingIgnoreCase(especialidadeFiltro, convenio, pageable)
                .map(this::toRecomendadoDto);
    }

    private EncaminhamentoRecomendadoDTO toRecomendadoDto(Profissional profissional) {
        return new EncaminhamentoRecomendadoDTO(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEspecialidade(),
                profissional.getContato(),
                profissional.getConvenio()
        );
    }

    private static void addHateoasLinks(EncaminhamentoResponseDTO dto) {
        var pageableExample = PageRequest.of(0, 20, Sort.by("id").descending());
        dto.add(linkTo(methodOn(EncaminhamentoController.class).listar(pageableExample)).withRel("listar").withType("GET"));

        if (dto.getTriagem() != null && dto.getTriagem().getId() != null) {
            Long triagemId = dto.getTriagem().getId();
            dto.add(linkTo(methodOn(EncaminhamentoController.class).listarPorTriagem(triagemId, pageableExample)).withRel("listarPorTriagem").withType("GET"));
        }

        dto.add(linkTo(methodOn(EncaminhamentoController.class).criar(new EncaminhamentoRequestDTO())).withRel("criar").withType("POST"));

        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(EncaminhamentoController.class).buscarPorId(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(EncaminhamentoController.class).atualizar(dto.getId(), new EncaminhamentoRequestDTO())).withRel("atualizar").withType("PUT"));
            dto.add(linkTo(methodOn(EncaminhamentoController.class).excluir(dto.getId())).withRel("excluir").withType("DELETE"));
        }
    }
}
