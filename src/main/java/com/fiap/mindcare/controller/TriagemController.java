package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.TriagemRequestDTO;
import com.fiap.mindcare.dto.TriagemResponseDTO;
import com.fiap.mindcare.service.TriagemService;
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
@RequestMapping("/api/triagens")
public class TriagemController {

    private final TriagemService triagemService;

    public TriagemController(TriagemService triagemService) {
        this.triagemService = triagemService;
    }

    @PostMapping
    public ResponseEntity<TriagemResponseDTO> criar(@Valid @RequestBody TriagemRequestDTO dto) {
        TriagemResponseDTO response = triagemService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TriagemResponseDTO> buscarPorId(@PathVariable Long id) {
        TriagemResponseDTO response = triagemService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TriagemResponseDTO>> listar(@ParameterObject
                                                           @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                           Pageable pageable) {
        Page<TriagemResponseDTO> page = triagemService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<TriagemResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId,
                                                                     @ParameterObject @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<TriagemResponseDTO> page = triagemService.listarPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TriagemResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TriagemRequestDTO dto) {
        TriagemResponseDTO response = triagemService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        triagemService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
