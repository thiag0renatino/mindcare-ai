package com.fiap.mindcare.controller;

import com.fiap.mindcare.dto.AuthRequestDTO;
import com.fiap.mindcare.dto.AuthSignInDTO;
import com.fiap.mindcare.service.AuthService;
import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Endpoints de autenticação com JWT")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Autenticar usuário e obter token JWT")
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AuthSignInDTO credential) {
        if (credentialsIsInvalid(credential)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requisição inválida");
        }

        return service.signIn(credential);
    }

    @Operation(summary = "Atualizar token JWT a partir de um refresh token")
    @PutMapping("/refresh/{email}")
    public ResponseEntity<?> refreshToken(@PathVariable("email") String email,
                                          @RequestHeader("Authorization") String refreshToken) {
        if (parametersAreInvalid(email, refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Requisição inválida");
        }
        return service.refreshToken(email, refreshToken);
    }

    @Operation(summary = "Registrar um novo usuário da plataforma. Empresas de exemplo: (MindCare Solutions, TechNova Corp, HealthSync Data, InnovaCare Tech)")
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody @Valid AuthRequestDTO credential) {
        return service.register(credential);
    }

    private boolean credentialsIsInvalid(AuthSignInDTO credential) {
        return credential == null
                || StringUtils.isBlank(credential.getEmail())
                || StringUtils.isBlank(credential.getSenha());
    }

    private boolean parametersAreInvalid(String email, String refreshToken) {
        return StringUtils.isBlank(email) || StringUtils.isBlank(refreshToken);
    }
}
