package com.fiap.mindcare.controller;

import com.fiap.mindcare.config.security.SecurityConfig;
import com.fiap.mindcare.dto.AtualizarNomeRequestDTO;
import com.fiap.mindcare.dto.AtualizarSenhaRequestDTO;
import com.fiap.mindcare.dto.UsuarioRequestDTO;
import com.fiap.mindcare.dto.UsuarioResponseDTO;
import com.fiap.mindcare.service.AuthService;
import com.fiap.mindcare.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Usuários", description = "Gerencia usuários da plataforma corporativa e seus perfis de acesso")
@RestController
@RequestMapping("/api/usuarios")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthService authService;

    public UsuarioController(UsuarioService usuarioService, AuthService authService) {
        this.usuarioService = usuarioService;
        this.authService = authService;
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

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(Principal principal) {
        return ResponseEntity.ok(usuarioService.me(principal));
    }

    @Operation(
            summary = "Atualizar senha",
            description = "Permite que o usuário autenticado atualize sua própria senha."
    )
    @PatchMapping("/me/senha")
    public ResponseEntity<Void> atualizarSenha(@RequestBody @Valid AtualizarSenhaRequestDTO dto) {
        authService.changePassword(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Atualizar nome",
            description = "Permite que o usuário autenticado atualize seu próprio nome."
    )
    @PatchMapping("/me/nome")
    public ResponseEntity<Void> atualizarNome(@RequestBody @Valid AtualizarNomeRequestDTO dto) {
        authService.changeName(dto);
        return ResponseEntity.noContent().build();
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
