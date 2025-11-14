package com.fiap.mindcare.repository;

import com.fiap.mindcare.enuns.RiscoTriagem;
import com.fiap.mindcare.model.Triagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriagemRepository extends JpaRepository<Triagem, Long> {

    Page<Triagem> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId, Pageable pageable);

    Page<Triagem> findByRisco(RiscoTriagem risco, Pageable pageable);

    // Triagens de todos usuários de uma empresa
    Page<Triagem> findByUsuarioEmpresaId(Long empresaId, Pageable pageable);
}
