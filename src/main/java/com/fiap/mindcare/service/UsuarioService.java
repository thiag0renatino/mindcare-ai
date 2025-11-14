package com.fiap.mindcare.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioSistemaRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioMapper usuarioMapper;
    private final EnumMapper enumMapper;

    public UsuarioService(UsuarioSistemaRepository usuarioRepository, EmpresaRepository empresaRepository, UsuarioMapper usuarioMapper, EnumMapper enumMapper) {
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioMapper = usuarioMapper;
        this.enumMapper = enumMapper;
    }


    public UsuarioResponseDTO buscarPorId(Long id) {
        UsuarioSistema entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return usuarioMapper.toResponse(entity);
    }

    public Page<UsuarioResponseDTO> listar(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponse);
    }

    public Page<UsuarioResponseDTO> listarPorEmpresa(Long empresaId, Pageable pageable) {
        return usuarioRepository.findByEmpresaId(empresaId, pageable)
                .map(usuarioMapper::toResponse);
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

        entity.setSenha(dto.getSenha());

        entity = usuarioRepository.save(entity);

        return usuarioMapper.toResponse(entity);
    }

    @Transactional
    public void excluir(Long id) {
        UsuarioSistema entity = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        usuarioRepository.delete(entity);
    }
}
