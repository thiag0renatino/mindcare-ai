package com.fiap.mindcare.controller;

import com.fiap.mindcare.config.security.SecurityConfig;
import com.fiap.mindcare.dto.ProfissionalRequestDTO;
import com.fiap.mindcare.dto.ProfissionalResponseDTO;
import com.fiap.mindcare.service.ProfissionalService;
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

@Tag(name = "Profissionais", description = "Catálogo de profissionais de saúde disponíveis para encaminhamentos")
@RestController
@RequestMapping("/api/profissionais")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @Operation(
            summary = "Cadastrar profissional",
            description = "Registra um novo profissional de saúde com especialidade, convênio e dados de contato."
    )
    @PostMapping
    public ResponseEntity<ProfissionalResponseDTO> criar(@Valid @RequestBody ProfissionalRequestDTO dto) {
        ProfissionalResponseDTO response = profissionalService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Buscar profissional por ID",
            description = "Retorna os detalhes completos de um profissional específico."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> buscarPorId(@PathVariable Long id) {
        ProfissionalResponseDTO response = profissionalService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar profissionais",
            description = "Retorna profissionais cadastrados com suporte a paginação e ordenação."
    )
    @GetMapping
    public ResponseEntity<Page<ProfissionalResponseDTO>> listar(@ParameterObject
                                                                @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                Pageable pageable) {
        Page<ProfissionalResponseDTO> page = profissionalService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Buscar profissionais por especialidade",
            description = "Filtra o catálogo usando o nome exato da especialidade informada, mantendo paginação."
    )
    @GetMapping("/especialidade")
    public ResponseEntity<Page<ProfissionalResponseDTO>> buscarPorEspecialidade(@RequestParam String especialidade,
                                                                                @ParameterObject
                                                                                @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                                Pageable pageable) {
        Page<ProfissionalResponseDTO> page = profissionalService.buscarPorEspecialidade(especialidade, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Atualizar profissional",
            description = "Alterar dados de um profissional cadastrado, incluindo especialidade, convênio e contato."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProfissionalRequestDTO dto) {
        ProfissionalResponseDTO response = profissionalService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Excluir profissional",
            description = "Remove permanentemente um profissional do catálogo."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        profissionalService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
