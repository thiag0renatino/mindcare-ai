package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.EncaminhamentoRequestDTO;
import com.fiap.mindcare.dto.EncaminhamentoResponseDTO;
import com.fiap.mindcare.service.EncaminhamentoService;
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
@RequestMapping("/api/encaminhamentos")
public class EncaminhamentoController {

    private final EncaminhamentoService encaminhamentoService;

    public EncaminhamentoController(EncaminhamentoService encaminhamentoService) {
        this.encaminhamentoService = encaminhamentoService;
    }

    @PostMapping
    public ResponseEntity<EncaminhamentoResponseDTO> criar(@Valid @RequestBody EncaminhamentoRequestDTO dto) {
        EncaminhamentoResponseDTO response = encaminhamentoService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncaminhamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        EncaminhamentoResponseDTO response = encaminhamentoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<EncaminhamentoResponseDTO>> listar(@ParameterObject
                                                                  @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                  Pageable pageable) {
        Page<EncaminhamentoResponseDTO> page = encaminhamentoService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/triagem/{triagemId}")
    public ResponseEntity<Page<EncaminhamentoResponseDTO>> listarPorTriagem(@PathVariable Long triagemId,
                                                                            @ParameterObject
                                                                            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                            Pageable pageable) {
        Page<EncaminhamentoResponseDTO> page = encaminhamentoService.listarPorTriagem(triagemId, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EncaminhamentoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EncaminhamentoRequestDTO dto) {
        EncaminhamentoResponseDTO response = encaminhamentoService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        encaminhamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
