package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.dto.UsuarioResponseDTO;
import com.fiap.mindcare.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Gerencia usuários da plataforma corporativa e seus perfis de acesso")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna os dados detalhados de um usuário específico, incluindo empresa e perfil de acesso."
    )
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        UsuarioResponseDTO response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar usuários",
            description = "Retorna usuários cadastrados com suporte a paginação e ordenação."
    )
    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> listar(@ParameterObject
                                                           @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                           Pageable pageable) {
        Page<UsuarioResponseDTO> page = usuarioService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Listar usuários por empresa",
            description = "Filtra os usuários vinculados a uma empresa específica usando paginação."
    )
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarPorEmpresa(@PathVariable Long empresaId,
                                                                     @ParameterObject
                                                                     @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<UsuarioResponseDTO> page = usuarioService.listarPorEmpresa(empresaId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza os dados básicos e o perfil do usuário. Campos com valores fixos: tipo (ADMIN, USER)."
    )
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Excluir usuário",
            description = "Remove permanentemente um usuário e perde o acesso ao sistema."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
