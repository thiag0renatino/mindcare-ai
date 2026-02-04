package com.fiap.mindcare.service;

import com.fiap.mindcare.controller.TriagemController;
import com.fiap.mindcare.dto.TriagemRequestDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.mapper.TriagemMapper;
import com.fiap.mindcare.model.Triagem;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.TriagemRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.service.exception.AccessDeniedException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import com.fiap.mindcare.service.security.UsuarioAutenticadoProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final UsuarioSistemaRepository usuarioRepository;
    private final TriagemMapper triagemMapper;
    private final EnumMapper enumMapper;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    public TriagemService(TriagemRepository triagemRepository, UsuarioSistemaRepository usuarioRepository, TriagemMapper triagemMapper, EnumMapper enumMapper, UsuarioAutenticadoProvider usuarioAutenticadoProvider) {
        this.triagemRepository = triagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.triagemMapper = triagemMapper;
        this.enumMapper = enumMapper;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
    }

    @Transactional
    public TriagemResponseDTO criar(TriagemRequestDTO dto) {
        UsuarioSistema usuario = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Triagem entity = triagemMapper.toEntity(dto);
        entity.setUsuario(usuario);
        entity.setRisco(enumMapper.toRiscoTriagem(dto.getRisco()));

        entity = triagemRepository.save(entity);

        TriagemResponseDTO response = triagemMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    public TriagemResponseDTO buscarPorId(Long id) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Triagem entity = triagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));

        if (!autenticado.getId().equals(entity.getUsuario().getId()) && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");

        }

        TriagemResponseDTO dto = triagemMapper.toResponse(entity);
        addHateoasLinks(dto);
        return dto;
    }

    public Page<TriagemResponseDTO> listar(Pageable pageable) {
        return triagemRepository.findAll(pageable)
                .map(entity -> {
                    TriagemResponseDTO dto = triagemMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    public Page<TriagemResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        if (!autenticado.getId().equals(usuarioId) && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        return triagemRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId, pageable)
                .map(entity -> {
                    TriagemResponseDTO dto = triagemMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    @Transactional
    public TriagemResponseDTO atualizar(Long id, TriagemRequestDTO dto) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Triagem entity = triagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));

        if (!entity.getUsuario().getId().equals(autenticado.getId()) && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        entity.setDataHora(dto.getDataHora());
        entity.setRelato(dto.getRelato());
        entity.setRisco(enumMapper.toRiscoTriagem(dto.getRisco()));
        entity.setSugestao(dto.getSugestao());

        entity = triagemRepository.save(entity);

        TriagemResponseDTO response = triagemMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    @Transactional
    public void excluir(Long id) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Triagem entity = triagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));

        if (!entity.getUsuario().getId().equals(autenticado.getId()) && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        triagemRepository.delete(entity);
    }

    private static void addHateoasLinks(TriagemResponseDTO dto) {
        var pageableExample = PageRequest.of(0, 20, Sort.by("id").descending());
        dto.add(linkTo(methodOn(TriagemController.class).listar(pageableExample)).withRel("listar").withType("GET"));

        if (dto.getUsuario() != null && dto.getUsuario().getId() != null) {
            Long usuarioId = dto.getUsuario().getId();
            dto.add(linkTo(methodOn(TriagemController.class).listarPorUsuario(usuarioId, pageableExample)).withRel("listarPorUsuario").withType("GET"));
        }
        dto.add(linkTo(methodOn(TriagemController.class).criar(new TriagemRequestDTO())).withRel("criar").withType("POST"));

        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(TriagemController.class).buscarPorId(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(TriagemController.class).atualizar(dto.getId(), new TriagemRequestDTO())).withRel("atualizar").withType("PUT"));
            dto.add(linkTo(methodOn(TriagemController.class).excluir(dto.getId())).withRel("excluir").withType("DELETE"));
        }
    }
}
