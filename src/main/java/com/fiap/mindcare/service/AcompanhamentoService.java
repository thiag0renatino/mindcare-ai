package com.fiap.mindcare.service;

import com.fiap.mindcare.controller.AcompanhamentoController;
import com.fiap.mindcare.dto.AcompanhamentoRequestDTO;
import com.fiap.mindcare.dto.AcompanhamentoResponseDTO;
import com.fiap.mindcare.mapper.AcompanhamentoMapper;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.model.Acompanhamento;
import com.fiap.mindcare.model.Encaminhamento;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.AcompanhamentoRepository;
import com.fiap.mindcare.repository.EncaminhamentoRepository;
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
public class AcompanhamentoService {

    private final AcompanhamentoRepository acompanhamentoRepository;
    private final EncaminhamentoRepository encaminhamentoRepository;
    private final AcompanhamentoMapper acompanhamentoMapper;
    private final EnumMapper enumMapper;
    private final UsuarioAutenticadoProvider usuarioAutenticadoProvider;

    public AcompanhamentoService(AcompanhamentoRepository acompanhamentoRepository, EncaminhamentoRepository encaminhamentoRepository, AcompanhamentoMapper acompanhamentoMapper, EnumMapper enumMapper, UsuarioAutenticadoProvider usuarioAutenticadoProvider) {
        this.acompanhamentoRepository = acompanhamentoRepository;
        this.encaminhamentoRepository = encaminhamentoRepository;
        this.acompanhamentoMapper = acompanhamentoMapper;
        this.enumMapper = enumMapper;
        this.usuarioAutenticadoProvider = usuarioAutenticadoProvider;
    }

    @Transactional
    public AcompanhamentoResponseDTO criar(AcompanhamentoRequestDTO dto) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Encaminhamento encaminhamento = encaminhamentoRepository.findById(dto.getEncaminhamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));

        if (!autenticado.getId().equals(encaminhamento.getTriagem().getUsuario().getId())
                && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        Acompanhamento entity = acompanhamentoMapper.toEntity(dto);
        entity.setEncaminhamento(encaminhamento);
        entity.setTipoEvento(enumMapper.toTipoEventoAcompanhamento(dto.getTipoEvento()));
        entity.setDescricao(dto.getDescricao());
        entity.setAnexoUrl(dto.getAnexoUrl());

        entity = acompanhamentoRepository.save(entity);

        AcompanhamentoResponseDTO response = acompanhamentoMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    public AcompanhamentoResponseDTO buscarPorId(Long id) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Acompanhamento entity = acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado"));

        if (!autenticado.getId().equals(entity.getEncaminhamento().getTriagem().getUsuario().getId())
                && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        AcompanhamentoResponseDTO dto = acompanhamentoMapper.toResponse(entity);
        addHateoasLinks(dto);
        return dto;
    }

    public Page<AcompanhamentoResponseDTO> listar(Pageable pageable) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Page<Acompanhamento> page;
        if (autenticado.getTipo() == TipoUsuario.ADMIN) {
            page = acompanhamentoRepository.findAll(pageable);
        } else {
            page = acompanhamentoRepository.findByEncaminhamentoTriagemUsuarioId(autenticado.getId(), pageable);
        }

        return page.map(entity -> {
            AcompanhamentoResponseDTO dto = acompanhamentoMapper.toResponse(entity);
            addHateoasLinks(dto);
            return dto;
        });
    }

    public Page<AcompanhamentoResponseDTO> listarPorEncaminhamento(Long encaminhamentoId, Pageable pageable) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Encaminhamento encaminhamento = encaminhamentoRepository.findById(encaminhamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));

        if (!autenticado.getId().equals(encaminhamento.getTriagem().getUsuario().getId())
                && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        return acompanhamentoRepository
                .findByEncaminhamentoIdOrderByDataEventoDesc(encaminhamentoId, pageable)
                .map(entity -> {
                    AcompanhamentoResponseDTO dto = acompanhamentoMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    @Transactional
    public AcompanhamentoResponseDTO atualizar(Long id, AcompanhamentoRequestDTO dto) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Acompanhamento entity = acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado"));

        if (!autenticado.getId().equals(entity.getEncaminhamento().getTriagem().getUsuario().getId())
                && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        Encaminhamento encaminhamento = encaminhamentoRepository.findById(dto.getEncaminhamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Encaminhamento não encontrado"));

        if (!autenticado.getId().equals(encaminhamento.getTriagem().getUsuario().getId())
                && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        entity.setEncaminhamento(encaminhamento);
        entity.setDataEvento(dto.getDataEvento());
        entity.setTipoEvento(enumMapper.toTipoEventoAcompanhamento(dto.getTipoEvento()));
        entity.setDescricao(dto.getDescricao());
        entity.setAnexoUrl(dto.getAnexoUrl());

        entity = acompanhamentoRepository.save(entity);

        AcompanhamentoResponseDTO response = acompanhamentoMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    @Transactional
    public void excluir(Long id) {
        UsuarioSistema autenticado = usuarioAutenticadoProvider.getUsuarioAutenticado();

        Acompanhamento entity = acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Acompanhamento não encontrado"));

        if (!autenticado.getId().equals(entity.getEncaminhamento().getTriagem().getUsuario().getId())
                && autenticado.getTipo() != TipoUsuario.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        acompanhamentoRepository.delete(entity);
    }

    private static void addHateoasLinks(AcompanhamentoResponseDTO dto) {
        var pageableExample = PageRequest.of(0, 20, Sort.by("id").descending());
        dto.add(linkTo(methodOn(AcompanhamentoController.class).listar(pageableExample)).withRel("listar").withType("GET"));

        if (dto.getEncaminhamento() != null && dto.getEncaminhamento().getId() != null) {
            Long encaminhamentoId = dto.getEncaminhamento().getId();
            dto.add(linkTo(methodOn(AcompanhamentoController.class).listarPorEncaminhamento(encaminhamentoId, pageableExample)).withRel("listarPorEncaminhamento").withType("GET"));
        }
        dto.add(linkTo(methodOn(AcompanhamentoController.class).criar(new AcompanhamentoRequestDTO())).withRel("criar").withType("POST"));

        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(AcompanhamentoController.class).buscarPorId(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(AcompanhamentoController.class).atualizar(dto.getId(), new AcompanhamentoRequestDTO())).withRel("atualizar").withType("PUT"));
            dto.add(linkTo(methodOn(AcompanhamentoController.class).excluir(dto.getId())).withRel("excluir").withType("DELETE"));
        }
    }
}
