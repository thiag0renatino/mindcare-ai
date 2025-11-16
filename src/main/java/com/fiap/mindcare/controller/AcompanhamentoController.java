package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.AcompanhamentoRequestDTO;
import com.fiap.mindcare.dto.AcompanhamentoResponseDTO;
import com.fiap.mindcare.service.AcompanhamentoService;
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

@RestController
@RequestMapping("/api/acompanhamentos")
public class AcompanhamentoController {

    private final AcompanhamentoService acompanhamentoService;

    public AcompanhamentoController(AcompanhamentoService acompanhamentoService) {
        this.acompanhamentoService = acompanhamentoService;
    }

    @PostMapping
    public ResponseEntity<AcompanhamentoResponseDTO> criar(@Valid @RequestBody AcompanhamentoRequestDTO dto) {
        AcompanhamentoResponseDTO response = acompanhamentoService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcompanhamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        AcompanhamentoResponseDTO response = acompanhamentoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<AcompanhamentoResponseDTO>> listar(@ParameterObject
                                                                  @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        Page<AcompanhamentoResponseDTO> page = acompanhamentoService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/encaminhamento/{encaminhamentoId}")
    public ResponseEntity<Page<AcompanhamentoResponseDTO>> listarPorEncaminhamento(@PathVariable Long encaminhamentoId,
                                                                                   @ParameterObject
                                                                                   @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                                   Pageable pageable) {
        Page<AcompanhamentoResponseDTO> page = acompanhamentoService.listarPorEncaminhamento(encaminhamentoId, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcompanhamentoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AcompanhamentoRequestDTO dto) {
        AcompanhamentoResponseDTO response = acompanhamentoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        acompanhamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
