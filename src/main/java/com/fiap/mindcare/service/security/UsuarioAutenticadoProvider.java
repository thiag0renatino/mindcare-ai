package com.fiap.mindcare.service.security;

import com.fiap.mindcare.model.UsuarioSistema;
import com.fiap.mindcare.repository.UsuarioSistemaRepository;
import com.fiap.mindcare.service.exception.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticadoProvider {

    private final UsuarioSistemaRepository repository;

    public UsuarioAutenticadoProvider(UsuarioSistemaRepository repository) {
        this.repository = repository;
    }

    public UsuarioSistema getUsuarioAutenticado() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        return repository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Usuário inválido"));
    }
}
