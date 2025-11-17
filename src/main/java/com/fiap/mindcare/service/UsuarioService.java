package com.fiap.mindcare.service;

import com.fiap.mindcare.controller.UsuarioController;
import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.dto.UsuarioResponseDTO;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.mapper.UsuarioMapper;
import com.fiap.mindcare.model.Empresa;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.EmpresaRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.service.exception.BusinessException;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioSistemaRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioMapper usuarioMapper;
    private final EnumMapper enumMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioSistemaRepository usuarioRepository, EmpresaRepository empresaRepository, UsuarioMapper usuarioMapper, EnumMapper enumMapper, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioMapper = usuarioMapper;
        this.enumMapper = enumMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        UsuarioSistema entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        UsuarioResponseDTO dto = usuarioMapper.toResponse(entity);
        addHateoasLinks(dto);
        return dto;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email não encontrado"));
    }

    public Page<UsuarioResponseDTO> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(entity -> {
                    UsuarioResponseDTO dto = usuarioMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    public Page<UsuarioResponseDTO> listarPorEmpresa(Long empresaId, Pageable pageable) {
        return usuarioRepository.findByEmpresaId(empresaId, pageable)
                .map(entity -> {
                    UsuarioResponseDTO dto = usuarioMapper.toResponse(entity);
                    addHateoasLinks(dto);
                    return dto;
                });
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        UsuarioSistema entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!entity.getEmail().equals(dto.getEmail())
                && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Já existe outro usuário com este e-mail");
        }

        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setTipo(enumMapper.toTipoUsuario(dto.getTipo()));
        entity.setEmpresa(empresa);
        if (StringUtils.isNotBlank(dto.getSenha())) {
            entity.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        entity = usuarioRepository.save(entity);

        UsuarioResponseDTO response = usuarioMapper.toResponse(entity);
        addHateoasLinks(response);
        return response;
    }

    @Transactional
    public void excluir(Long id) {
        UsuarioSistema entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuarioRepository.delete(entity);
    }

    private static void addHateoasLinks(UsuarioResponseDTO dto) {
        var pageableExample = PageRequest.of(0, 20, Sort.by("id").descending());
        dto.add(linkTo(methodOn(UsuarioController.class).listar(pageableExample)).withRel("listar").withType("GET"));

        if (dto.getEmpresa() != null && dto.getEmpresa().getId() != null) {
            Long empresaId = dto.getEmpresa().getId();
            dto.add(linkTo(methodOn(UsuarioController.class).listarPorEmpresa(empresaId, pageableExample)).withRel("listarPorEmpresa").withType("GET"));
        }

        if (dto.getId() != null) {
            dto.add(linkTo(methodOn(UsuarioController.class).buscarPorId(dto.getId())).withSelfRel().withType("GET"));
            dto.add(linkTo(methodOn(UsuarioController.class).atualizar(dto.getId(), new UsuarioRequestDTO())).withRel("atualizar").withType("PUT"));
            dto.add(linkTo(methodOn(UsuarioController.class).excluir(dto.getId())).withRel("excluir").withType("DELETE"));
        }
    }
}
