package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.EmpresaRequestDTO;
import com.fiap.mindcare.dto.EmpresaResponseDTO;
import com.fiap.mindcare.mapper.EmpresaMapper;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.service.exception.BusinessException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    public EmpresaService(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    @Transactional
    public EmpresaResponseDTO criar(EmpresaRequestDTO dto) {
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("Já existe uma empresa cadastrada com este CNPJ");
        }

        Empresa entity = empresaMapper.toEntity(dto);
        entity = empresaRepository.save(entity);

        return empresaMapper.toResponse(entity);
    }

    public EmpresaResponseDTO buscarPorId(Long id) {
        Empresa entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        return empresaMapper.toResponse(entity);
    }

    public Page<EmpresaResponseDTO> listar(Pageable pageable) {
        return empresaRepository.findAll(pageable)
                .map(empresaMapper::toResponse);
    }

    @Transactional
    public EmpresaResponseDTO atualizar(Long id, EmpresaRequestDTO dto) {
        Empresa entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (!entity.getCnpj().equals(dto.getCnpj())
                && empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("Já existe outra empresa com este CNPJ");
        }

        entity.setCnpj(dto.getCnpj());
        entity.setNome(dto.getNome());
        entity.setPlanoSaude(dto.getPlanoSaude());

        entity = empresaRepository.save(entity);

        return empresaMapper.toResponse(entity);
    }

    @Transactional
    public void excluir(Long id) {
        Empresa entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        empresaRepository.delete(entity);
    }
}
