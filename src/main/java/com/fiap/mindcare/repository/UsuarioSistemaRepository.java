package com.fiap.mindcare.repository;

import com.fiap.mindcare.enuns.TipoUsuario;
import com.fiap.mindcare.model.UsuarioSistema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioSistemaRepository extends JpaRepository<UsuarioSistema, Long> {

    Optional<UsuarioSistema> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UsuarioSistema> findByEmpresaId(Long empresaId, Pageable pageable);

    Page<UsuarioSistema> findByTipo(TipoUsuario tipo, Pageable pageable);
}
