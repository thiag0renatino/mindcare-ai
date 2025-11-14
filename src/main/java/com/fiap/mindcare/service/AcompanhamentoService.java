package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.AcompanhamentoRequestDTO;
import com.fiap.mindcare.dto.AcompanhamentoResponseDTO;
import com.fiap.mindcare.mapper.AcompanhamentoMapper;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.model.Acompanhamento;
import com.fiap.mindcare.model.Encaminhamento;
import com.fiap.mindcare.repository.AcompanhamentoRepository;
import com.fiap.mindcare.repository.EncaminhamentoRepository;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcompanhamentoService {

    private final AcompanhamentoRepository acompanhamentoRepository;
    private final EncaminhamentoRepository encaminhamentoRepository;
    private final AcompanhamentoMapper acompanhamentoMapper;
    private final EnumMapper enumMapper;

    public AcompanhamentoService(AcompanhamentoRepository acompanhamentoRepository, EncaminhamentoRepository encaminhamentoRepository, AcompanhamentoMapper acompanhamentoMapper, EnumMapper enumMapper) {
        this.acompanhamentoRepository = acompanhamentoRepository;
        this.encaminhamentoRepository = encaminhamentoRepository;
        this.acompanhamentoMapper = acompanhamentoMapper;
        this.enumMapper = enumMapper;
    }

    @Transactional
    public AcompanhamentoResponseDTO criar(AcompanhamentoRequestDTO dto) {
        Acompanhamento entity = acompanhamentoMapper.toEntity(dto);

        Encaminhamento encaminhamento = encaminhamentoRepository.findById(dto.getEncaminhamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));
        entity.setEncaminhamento(encaminhamento);

        entity.setTipoEvento(enumMapper.toTipoEventoAcompanhamento(dto.getTipoEvento()));
        entity.setDescricao(dto.getDescricao());
        entity.setAnexoUrl(dto.getAnexoUrl());

        entity = acompanhamentoRepository.save(entity);

        return acompanhamentoMapper.toResponse(entity);
    }

    public AcompanhamentoResponseDTO buscarPorId(Long id) {
        Acompanhamento entity = acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado"));

        return acompanhamentoMapper.toResponse(entity);
    }

    public Page<AcompanhamentoResponseDTO> listar(Pageable pageable) {
        return acompanhamentoRepository.findAll(pageable)
                .map(acompanhamentoMapper::toResponse);
    }

    public Page<AcompanhamentoResponseDTO> listarPorEncaminhamento(Long encaminhamentoId, Pageable pageable) {
        return acompanhamentoRepository
                .findByEncaminhamentoIdOrderByDataEventoDesc(encaminhamentoId, pageable)
                .map(acompanhamentoMapper::toResponse);
    }

    @Transactional
    public AcompanhamentoResponseDTO atualizar(Long id, AcompanhamentoRequestDTO dto) {
        Acompanhamento entity = acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado"));

        Encaminhamento encaminhamento = encaminhamentoRepository.findById(dto.getEncaminhamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));
        entity.setEncaminhamento(encaminhamento);

        entity.setDataEvento(dto.getDataEvento());
        entity.setTipoEvento(enumMapper.toTipoEventoAcompanhamento(dto.getTipoEvento()));
        entity.setDescricao(dto.getDescricao());
        entity.setAnexoUrl(dto.getAnexoUrl());

        entity = acompanhamentoRepository.save(entity);

        return acompanhamentoMapper.toResponse(entity);
    }

    @Transactional
    public void excluir(Long id) {
        Acompanhamento entity = acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado"));

        acompanhamentoRepository.delete(entity);
    }
}
