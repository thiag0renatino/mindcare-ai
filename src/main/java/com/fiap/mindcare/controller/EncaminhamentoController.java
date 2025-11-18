package com.fiap.mindcare.controller;

import com.fiap.mindcare.config.security.SecurityConfig;
import com.fiap.mindcare.dto.EncaminhamentoRecomendadoDTO;
import com.fiap.mindcare.dto.EncaminhamentoRequestDTO;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
import com.fiap.mindcare.service.EncaminhamentoService;
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

@Tag(name = "Encaminhamentos", description = "Administra encaminhamentos clínicos originados das triagens")
@RestController
@RequestMapping("/api/encaminhamentos")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class EncaminhamentoController {

    private final EncaminhamentoService encaminhamentoService;

    public EncaminhamentoController(EncaminhamentoService encaminhamentoService) {
        this.encaminhamentoService = encaminhamentoService;
    }

    @Operation(
            summary = "Criar um novo encaminhamento",
            description = "Registra um encaminhamento para uma triagem. Campos com valores fixos: tipo (EXAME, ESPECIALIDADE, PROFISSIONAL, HABITO), prioridade (BAIXA, MEDIA, ALTA), status (PENDENTE, AGENDADO, CONCLUIDO, CANCELADO)."
    )
    @PostMapping
    public ResponseEntity<EncaminhamentoResponseDTO> criar(@Valid @RequestBody EncaminhamentoRequestDTO dto) {
        EncaminhamentoResponseDTO response = encaminhamentoService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(
            summary = "Buscar encaminhamento por ID",
            description = "Retorna os dados completos de um encaminhamento específico."
    )
    @GetMapping("/{id}")
    public ResponseEntity<EncaminhamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        EncaminhamentoResponseDTO response = encaminhamentoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Listar encaminhamentos",
            description = "Retorna os encaminhamentos cadastrados com suporte a paginação e ordenação."
    )
    @GetMapping
    public ResponseEntity<Page<EncaminhamentoResponseDTO>> listar(@ParameterObject
                                                                  @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        Page<EncaminhamentoResponseDTO> page = encaminhamentoService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Recomendar profissionais/exames conveniados",
            description = "Retorna uma lista paginada de profissionais filtrados por especialidade e convênio médico da empresa informada."
    )
    @GetMapping("/recomendados")
    public ResponseEntity<Page<EncaminhamentoRecomendadoDTO>> listarRecomendados(@RequestParam Long empresaId,
                                                                                 @RequestParam(required = false, defaultValue = "") String especialidade,
                                                                                 @ParameterObject
                                                                                 @PageableDefault(page = 0, size = 20, sort = "nome", direction = Sort.Direction.ASC)
                                                                                 Pageable pageable) {
        Page<EncaminhamentoRecomendadoDTO> page = encaminhamentoService.listarRecomendados(empresaId, especialidade, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Listar encaminhamentos por triagem",
            description = "Filtra os encaminhamentos vinculados a uma triagem utilizando parâmetros de paginação."
    )
    @GetMapping("/triagem/{triagemId}")
    public ResponseEntity<Page<EncaminhamentoResponseDTO>> listarPorTriagem(@PathVariable Long triagemId,
                                                                            @ParameterObject
                                                                            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                            Pageable pageable) {
        Page<EncaminhamentoResponseDTO> page = encaminhamentoService.listarPorTriagem(triagemId, pageable);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Atualizar encaminhamento",
            description = "Permite alterar um encaminhamento já existente. Campos com valores fixos: tipo (EXAME, ESPECIALIDADE, PROFISSIONAL, HABITO), prioridade (BAIXA, MEDIA, ALTA), status (PENDENTE, AGENDADO, CONCLUIDO, CANCELADO)."
    )
    @PutMapping("/{id}")
    public ResponseEntity<EncaminhamentoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EncaminhamentoRequestDTO dto) {
        EncaminhamentoResponseDTO response = encaminhamentoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Excluir encaminhamento",
            description = "Remove permanentemente um encaminhamento que não será mais acompanhado."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        encaminhamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
