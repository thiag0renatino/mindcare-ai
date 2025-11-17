package com.fiap.mindcare.repository;

import com.fiap.mindcare.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByCnpj(String cnpj);

    Optional<Empresa> findByNomeContainingIgnoreCase(String nome);

    boolean existsByCnpj(String cnpj);
}
