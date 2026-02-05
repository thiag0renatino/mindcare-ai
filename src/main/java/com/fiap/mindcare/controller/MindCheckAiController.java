package com.fiap.mindcare.controller;

import com.fiap.mindcare.config.security.SecurityConfig;
import com.fiap.mindcare.dto.MindCheckAiRequestDTO;
import com.fiap.mindcare.dto.MindCheckAiResponseDTO;
import com.fiap.mindcare.service.MindCheckAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MindCheck AI", description = "Análise de triagem com IA generativa")
@RestController
@RequestMapping("/api/mindcheck-ai")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class MindCheckAiController {

    private final MindCheckAiService mindCheckAiService;

    public MindCheckAiController(MindCheckAiService mindCheckAiService) {
        this.mindCheckAiService = mindCheckAiService;
    }

    @Operation(
            summary = "Analisar relato de colaborador",
            description = "Utiliza Spring AI com Azure OpenAI para definir risco, guardar a triagem e gerar encaminhamentos automáticos quando necessário."
    )
    @PostMapping("/analises")
    public ResponseEntity<MindCheckAiResponseDTO> analisar(@RequestBody @Valid MindCheckAiRequestDTO request) {
        MindCheckAiResponseDTO response = mindCheckAiService.analisar(request);
        return ResponseEntity.ok(response);
    }
}
