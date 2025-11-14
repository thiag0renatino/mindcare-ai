package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.ProfissionalRequestDTO;
import com.fiap.mindcare.dto.ProfissionalResponseDTO;
import com.fiap.mindcare.mapper.ProfissionalMapper;
import com.fiap.mindcare.model.Profissional;
import com.fiap.mindcare.repository.ProfissionalRepository;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final ProfissionalMapper profissionalMapper;

    public ProfissionalService(ProfissionalRepository profissionalRepository, ProfissionalMapper profissionalMapper) {
        this.profissionalRepository = profissionalRepository;
        this.profissionalMapper = profissionalMapper;
    }

    @Transactional
    public ProfissionalResponseDTO criar(ProfissionalRequestDTO dto) {
        Profissional entity = profissionalMapper.toEntity(dto);
        entity = profissionalRepository.save(entity);
        return profissionalMapper.toResponse(entity);
    }

    public ProfissionalResponseDTO buscarPorId(Long id) {
        Profissional entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
        return profissionalMapper.toResponse(entity);
    }

    public Page<ProfissionalResponseDTO> listar(Pageable pageable) {
        return profissionalRepository.findAll(pageable)
                .map(profissionalMapper::toResponse);
    }

    public Page<ProfissionalResponseDTO> buscarPorEspecialidade(String especialidade, Pageable pageable) {
        return profissionalRepository
                .findByEspecialidadeContainingIgnoreCase(especialidade, pageable)
                .map(profissionalMapper::toResponse);
    }

    @Transactional
    public ProfissionalResponseDTO atualizar(Long id, ProfissionalRequestDTO dto) {
        Profissional entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        entity.setNome(dto.getNome());
        entity.setEspecialidade(dto.getEspecialidade());
        entity.setConvenio(dto.getConvenio());
        entity.setContato(dto.getContato());

        entity = profissionalRepository.save(entity);

        return profissionalMapper.toResponse(entity);
    }

    @Transactional
    public void excluir(Long id) {
        Profissional entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
        profissionalRepository.delete(entity);
    }
}
