package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.EmpresaRequestDTO;
import com.fiap.mindcare.dto.EmpresaResponseDTO;
import com.fiap.mindcare.service.EmpresaService;
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
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> criar(@Valid @RequestBody EmpresaRequestDTO dto) {
        EmpresaResponseDTO response = empresaService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(@PathVariable Long id) {
        EmpresaResponseDTO response = empresaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<EmpresaResponseDTO>> listar(@ParameterObject
                                                           @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                           Pageable pageable) {
        Page<EmpresaResponseDTO> page = empresaService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody EmpresaRequestDTO dto) {
        EmpresaResponseDTO response = empresaService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        empresaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
