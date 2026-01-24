package com.fiap.mindcare.controller;

import com.fiap.mindcare.service.MindcareProcedureService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procedures/insert")
@ConditionalOnProperty(prefix = "app.procedures", name = "enabled", havingValue = "true")
public class MindcareProcedureController {

    private final MindcareProcedureService service;

    public MindcareProcedureController(MindcareProcedureService service) {
        this.service = service;
    }

    @GetMapping("/export/json")
    public ResponseEntity<String> exportJson() {
        String json = service.exportarJsonCompleto();
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(json);
    }

    @Operation(summary = "Inserir EMPRESA via procedure pkg_empresa.inserir")
    @PostMapping("/empresas")
    public ResponseEntity<Void> inserirEmpresa(@RequestBody MindcareProcedureService.EmpresaProcRequest req) {
        service.inserirEmpresa(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Inserir USUARIO_SISTEMA via procedure pkg_usuario_sistema.inserir")
    @PostMapping("/usuarios")
    public ResponseEntity<Void> inserirUsuarioSistema(@RequestBody MindcareProcedureService.UsuarioSistemaProcRequest req) {
        service.inserirUsuarioSistema(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Inserir PROFISSIONAL via procedure pkg_profissional.inserir")
    @PostMapping("/profissionais")
    public ResponseEntity<Void> inserirProfissional(@RequestBody MindcareProcedureService.ProfissionalProcRequest req) {
        service.inserirProfissional(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Inserir TRIAGEM via procedure pkg_triagem.inserir")
    @PostMapping("/triagens")
    public ResponseEntity<Void> inserirTriagem(@RequestBody MindcareProcedureService.TriagemProcRequest req) {
        service.inserirTriagem(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Inserir ENCAMINHAMENTO via procedure pkg_encaminhamento.inserir")
    @PostMapping("/encaminhamentos")
    public ResponseEntity<Void> inserirEncaminhamento(@RequestBody MindcareProcedureService.EncaminhamentoProcRequest req) {
        service.inserirEncaminhamento(req);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Inserir ACOMPANHAMENTO via procedure pkg_acompanhamento.inserir")
    @PostMapping("/acompanhamentos")
    public ResponseEntity<Void> inserirAcompanhamento(@RequestBody MindcareProcedureService.AcompanhamentoProcRequest req) {
        service.inserirAcompanhamento(req);
        return ResponseEntity.ok().build();
    }
}
