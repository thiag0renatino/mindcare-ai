package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.dto.UsuarioResponseDTO;
import com.fiap.mindcare.service.UsuarioService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listar(@ParameterObject
                                                           @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                           Pageable pageable) {
        Page<UsuarioResponseDTO> page = usuarioService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarPorEmpresa(@PathVariable Long empresaId,
                                                                     @ParameterObject
                                                                     @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<UsuarioResponseDTO> page = usuarioService.listarPorEmpresa(empresaId, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
