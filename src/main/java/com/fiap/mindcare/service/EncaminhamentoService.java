package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.EncaminhamentoRequestDTO;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
import com.fiap.mindcare.mapper.EncaminhamentoMapper;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.model.Encaminhamento;
import com.fiap.mindcare.model.Profissional;
import com.fiap.mindcare.model.Triagem;
import com.fiap.mindcare.repository.EncaminhamentoRepository;
import com.fiap.mindcare.repository.ProfissionalRepository;
import com.fiap.mindcare.repository.TriagemRepository;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EncaminhamentoService {

    private final EncaminhamentoRepository encaminhamentoRepository;
    private final TriagemRepository triagemRepository;
    private final ProfissionalRepository profissionalRepository;
    private final EncaminhamentoMapper encaminhamentoMapper;
    private final EnumMapper enumMapper;

    public EncaminhamentoService(EncaminhamentoRepository encaminhamentoRepository, TriagemRepository triagemRepository, ProfissionalRepository profissionalRepository, EncaminhamentoMapper encaminhamentoMapper, EnumMapper enumMapper) {
        this.encaminhamentoRepository = encaminhamentoRepository;
        this.triagemRepository = triagemRepository;
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

        return encaminhamentoMapper.toResponse(entity);
    }

    public EncaminhamentoResponseDTO buscarPorId(Long id) {
        Encaminhamento entity = encaminhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));
        return encaminhamentoMapper.toResponse(entity);
    }

    public Page<EncaminhamentoResponseDTO> listar(Pageable pageable) {
        return encaminhamentoRepository.findAll(pageable)
                .map(encaminhamentoMapper::toResponse);
    }

    public Page<EncaminhamentoResponseDTO> listarPorTriagem(Long triagemId, Pageable pageable) {
        return encaminhamentoRepository.findByTriagemId(triagemId, pageable)
                .map(encaminhamentoMapper::toResponse);
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

        return encaminhamentoMapper.toResponse(entity);
    }

    @Transactional
    public void excluir(Long id) {
        Encaminhamento entity = encaminhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));
        encaminhamentoRepository.delete(entity);
    }
}
