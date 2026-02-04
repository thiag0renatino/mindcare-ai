package com.fiap.mindcare.controller;

import com.fiap.mindcare.config.security.SecurityConfig;
import com.fiap.mindcare.dto.TriagemRequestDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.service.TriagemService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Triagens", description = "Controle de registros de triagem e avaliação inicial dos usuários")
@RestController
@RequestMapping("/api/triagens")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class TriagemController {

    private final TriagemService triagemService;

    public TriagemController(TriagemService triagemService) {
        this.triagemService = triagemService;
    }

    @Operation(
            summary = "Registrar triagem",
            description = "Cria uma nova triagem para um usuário. Campos com valores fixos: risco (BAIXO, MODERADO, ALTO)."
    )
    @PostMapping
    public ResponseEntity<TriagemResponseDTO> criar(@Valid @RequestBody TriagemRequestDTO dto) {
        TriagemResponseDTO response = triagemService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Buscar triagem por ID",
            description = "Retorna os detalhes completos de uma triagem específica."
    )
    @GetMapping("/{id}")
    public ResponseEntity<TriagemResponseDTO> buscarPorId(@PathVariable Long id) {
        TriagemResponseDTO response = triagemService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar triagens",
            description = "Retorna triagens cadastradas com suporte a paginação e ordenação."
    )
    @GetMapping
    public ResponseEntity<Page<TriagemResponseDTO>> listar(@ParameterObject
                                                           @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                           Pageable pageable) {
        Page<TriagemResponseDTO> page = triagemService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Listar triagens do usuário",
            description = "Filtra triagens associadas a um usuário determinado, mantendo paginação. Requer ser o próprio usuário ou ADMIN."
    )
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<TriagemResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId,
                                                                     @ParameterObject @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<TriagemResponseDTO> page = triagemService.listarPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Atualizar triagem",
            description = "Edita uma triagem cadastrada. Campos com valores fixos: risco (BAIXO, MODERADO, ALTO)."
    )
    @PutMapping("/{id}")
    public ResponseEntity<TriagemResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TriagemRequestDTO dto) {
        TriagemResponseDTO response = triagemService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Excluir triagem",
            description = "Remove definitivamente uma triagem e seus dados registrados."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        triagemService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
