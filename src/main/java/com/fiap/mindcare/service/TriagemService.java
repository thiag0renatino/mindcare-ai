package com.fiap.mindcare.service;

import com.fiap.mindcare.dto.TriagemRequestDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.mapper.EnumMapper;
import com.fiap.mindcare.mapper.TriagemMapper;
import com.fiap.mindcare.model.Triagem;
import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.TriagemRepository;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TriagemService {

    private final TriagemRepository triagemRepository;
    private final UsuarioSistemaRepository usuarioRepository;
    private final TriagemMapper triagemMapper;
    private final EnumMapper enumMapper;

    public TriagemService(TriagemRepository triagemRepository, UsuarioSistemaRepository usuarioRepository, TriagemMapper triagemMapper, EnumMapper enumMapper) {
        this.triagemRepository = triagemRepository;
        this.usuarioRepository = usuarioRepository;
        this.triagemMapper = triagemMapper;
        this.enumMapper = enumMapper;
    }

    @Transactional
    public TriagemResponseDTO criar(TriagemRequestDTO dto) {
        Triagem entity = triagemMapper.toEntity(dto);

        UsuarioSistema usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        entity.setUsuario(usuario);
        entity.setRisco(enumMapper.toRiscoTriagem(dto.getRisco()));

        entity = triagemRepository.save(entity);

        return triagemMapper.toResponse(entity);
    }

    public TriagemResponseDTO buscarPorId(Long id) {
        Triagem entity = triagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));

        return triagemMapper.toResponse(entity);
    }

    public Page<TriagemResponseDTO> listar(Pageable pageable) {
        return triagemRepository.findAll(pageable)
                .map(triagemMapper::toResponse);
    }

    public Page<TriagemResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return triagemRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId, pageable)
                .map(triagemMapper::toResponse);
    }

    @Transactional
    public TriagemResponseDTO atualizar(Long id, TriagemRequestDTO dto) {
        Triagem entity = triagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));

        UsuarioSistema usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        entity.setUsuario(usuario);
        entity.setDataHora(dto.getDataHora());
        entity.setRelato(dto.getRelato());
        entity.setRisco(enumMapper.toRiscoTriagem(dto.getRisco()));
        entity.setSugestao(dto.getSugestao());

        entity = triagemRepository.save(entity);

        return triagemMapper.toResponse(entity);
    }

    @Transactional
    public void excluir(Long id) {
        Triagem entity = triagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Triagem não encontrada"));

        triagemRepository.delete(entity);
    }
}
