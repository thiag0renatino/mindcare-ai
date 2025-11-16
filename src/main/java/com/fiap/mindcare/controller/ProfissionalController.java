package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.ProfissionalRequestDTO;
import com.fiap.mindcare.dto.ProfissionalResponseDTO;
import com.fiap.mindcare.service.ProfissionalService;
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
@RequestMapping("/api/profissionais")
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    public ProfissionalController(ProfissionalService profissionalService) {
        this.profissionalService = profissionalService;
    }

    @PostMapping
    public ResponseEntity<ProfissionalResponseDTO> criar(@Valid @RequestBody ProfissionalRequestDTO dto) {
        ProfissionalResponseDTO response = profissionalService.criar(dto);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> buscarPorId(@PathVariable Long id) {
        ProfissionalResponseDTO response = profissionalService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ProfissionalResponseDTO>> listar(@ParameterObject
                                                                @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                Pageable pageable) {
        Page<ProfissionalResponseDTO> page = profissionalService.listar(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/especialidade")
    public ResponseEntity<Page<ProfissionalResponseDTO>> buscarPorEspecialidade(@RequestParam String especialidade,
                                                                                @ParameterObject
                                                                                @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
                                                                                Pageable pageable) {
        Page<ProfissionalResponseDTO> page = profissionalService.buscarPorEspecialidade(especialidade, pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProfissionalRequestDTO dto) {
        ProfissionalResponseDTO response = profissionalService.atualizar(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        profissionalService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
