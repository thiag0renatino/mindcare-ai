package com.fiap.mindcare.controller;

import com.fiap.mindcare.config.security.SecurityConfig;
import com.fiap.mindcare.dto.AcompanhamentoRequestDTO;
import com.fiap.mindcare.dto.AcompanhamentoResponseDTO;
import com.fiap.mindcare.service.AcompanhamentoService;
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

@Tag(name = "Acompanhamentos", description = "Gerencia eventos e interações derivados de um encaminhamento clínico")
@RestController
@RequestMapping("/api/acompanhamentos")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class AcompanhamentoController {

    private final AcompanhamentoService acompanhamentoService;

    public AcompanhamentoController(AcompanhamentoService acompanhamentoService) {
        this.acompanhamentoService = acompanhamentoService;
    }

    @Operation(
            summary = "Registrar evento de acompanhamento",
            description = "Cria um novo evento vinculado a um encaminhamento existente. Campos com valores fixos: tipoEvento (AGENDAMENTO, RESULTADO, ALTA, CANCELAMENTO, OBSERVACAO)."
    )
    @PostMapping
    public ResponseEntity<AcompanhamentoResponseDTO> criar(@RequestBody @Valid AcompanhamentoRequestDTO dto) {
        AcompanhamentoResponseDTO response = acompanhamentoService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Buscar acompanhamento por ID",
            description = "Retorna os detalhes completos de um evento único de acompanhamento."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AcompanhamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        AcompanhamentoResponseDTO response = acompanhamentoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar acompanhamentos",
            description = "Retorna eventos de acompanhamento com paginação e ordenação configuráveis."
    )
    @GetMapping
    public ResponseEntity<Page<AcompanhamentoResponseDTO>> listar(@ParameterObject
                                                                  @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        Page<AcompanhamentoResponseDTO> page = acompanhamentoService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Listar acompanhamentos por encaminhamento",
            description = "Retorna todos os eventos de acompanhamento associados a um encaminhamento específico utilizando paginação."
    )
    @GetMapping("/encaminhamento/{encaminhamentoId}")
    public ResponseEntity<Page<AcompanhamentoResponseDTO>> listarPorEncaminhamento(@PathVariable Long encaminhamentoId,
                                                                                   @ParameterObject
                                                                                   @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                                   Pageable pageable) {
        Page<AcompanhamentoResponseDTO> page = acompanhamentoService.listarPorEncaminhamento(encaminhamentoId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Atualizar evento de acompanhamento",
            description = "Permite revisar um evento previamente cadastrado. Campos com valores fixos: tipoEvento (AGENDAMENTO, RESULTADO, ALTA, CANCELAMENTO, OBSERVACAO)."
    )
    @PutMapping("/{id}")
    public ResponseEntity<AcompanhamentoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AcompanhamentoRequestDTO dto) {
        AcompanhamentoResponseDTO response = acompanhamentoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Excluir evento de acompanhamento",
            description = "Remove definitivamente um evento de acompanhamento do histórico."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        acompanhamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
