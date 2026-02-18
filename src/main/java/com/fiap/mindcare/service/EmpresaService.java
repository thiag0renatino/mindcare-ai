package com.fiap.mindcare.service;

import com.fiap.mindcare.controller.EmpresaController;
import com.fiap.mindcare.dto.EmpresaRequestDTO;
import com.fiap.mindcare.dto.EmpresaResponseDTO;
import com.fiap.mindcare.mapper.EmpresaMapper;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.service.exception.BusinessException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    public EmpresaService(EmpresaRepository empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    @Transactional
    @CacheEvict(value = "empresas", allEntries = true)
    public EmpresaResponseDTO criar(EmpresaRequestDTO dto) {
        if (empresaRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("Já existe uma empresa cadastrada com este CNPJ");
        }

        Empresa entity = empresaMapper.toEntity(dto);
        entity = empresaRepository.save(entity);

        EmpresaResponseDTO response = empresaMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    @Cacheable(value = "empresas", key = "#id")
    public EmpresaResponseDTO buscarPorId(Long id) {
        Empresa entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        EmpresaResponseDTO response = empresaMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    public Page<EmpresaResponseDTO> listar(Pageable pageable) {
        return empresaRepository.findAll(pageable)
                .map(entity -> {
                    EmpresaResponseDTO dto = empresaMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    @Transactional
    @CacheEvict(value = "empresas", allEntries = true)
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

        EmpresaResponseDTO response = empresaMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    @Transactional
    @CacheEvict(value = "empresas", allEntries = true)
    public void excluir(Long id) {
        Empresa entity = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        empresaRepository.delete(entity);
    }

    private static void addHateoasLinks(EmpresaResponseDTO dto) {
        var pageableExample = PageRequest.of(0, 20, Sort.by("id").descending());
        dto.add(linkTo(methodOn(EmpresaController.class).listar(pageableExample)).withRel("listar").withType("GET"));
        dto.add(linkTo(methodOn(EmpresaController.class).criar(new EmpresaRequestDTO())).withRel("criar").withType("POST"));

        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(EmpresaController.class).buscarPorId(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(EmpresaController.class).atualizar(dto.getId(), new EmpresaRequestDTO())).withRel("atualizar").withType("PUT"));
            dto.add(linkTo(methodOn(EmpresaController.class).excluir(dto.getId())).withRel("excluir").withType("DELETE"));
        }
    }
}
