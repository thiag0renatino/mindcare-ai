package com.fiap.mindcare.service;

import com.fiap.mindcare.controller.ProfissionalController;
import com.fiap.mindcare.dto.ProfissionalRequestDTO;
import com.fiap.mindcare.dto.ProfissionalResponseDTO;
import com.fiap.mindcare.mapper.ProfissionalMapper;
import com.fiap.mindcare.model.Profissional;
import com.fiap.mindcare.repository.ProfissionalRepository;
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
        ProfissionalResponseDTO response = profissionalMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    public ProfissionalResponseDTO buscarPorId(Long id) {
        Profissional entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
        ProfissionalResponseDTO dto = profissionalMapper.toResponse(entity);
        addHateoasLinks(dto);
        return dto;
    }

    public Page<ProfissionalResponseDTO> listar(Pageable pageable) {
        return profissionalRepository.findAll(pageable)
                .map(entity -> {
                    ProfissionalResponseDTO dto = profissionalMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    public Page<ProfissionalResponseDTO> buscarPorEspecialidade(String especialidade, Pageable pageable) {
        return profissionalRepository
                .findByEspecialidadeContainingIgnoreCase(especialidade, pageable)
                .map(entity -> {
                    ProfissionalResponseDTO dto = profissionalMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
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

        ProfissionalResponseDTO response = profissionalMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    @Transactional
    public void excluir(Long id) {
        Profissional entity = profissionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));
        profissionalRepository.delete(entity);
    }

    private static void addHateoasLinks(ProfissionalResponseDTO dto) {
        var pageableExample = PageRequest.of(0, 20, Sort.by("id").descending());
        dto.add(linkTo(methodOn(ProfissionalController.class).listar(pageableExample)).withRel("listar").withType("GET"));

        if (dto.getEspecialidade() != null && !dto.getEspecialidade().isBlank()) {
            dto.add(linkTo(methodOn(ProfissionalController.class).buscarPorEspecialidade(dto.getEspecialidade(), pageableExample)).withRel("buscarPorEspecialidade").withType("GET"));
        }

        dto.add(linkTo(methodOn(ProfissionalController.class).criar(new ProfissionalRequestDTO())).withRel("criar").withType("POST"));

        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(ProfissionalController.class).buscarPorId(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(ProfissionalController.class).atualizar(dto.getId(), new ProfissionalRequestDTO())).withRel("atualizar").withType("PUT"));
            dto.add(linkTo(methodOn(ProfissionalController.class).excluir(dto.getId())).withRel("excluir").withType("DELETE"));
        }
    }
}
