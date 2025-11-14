package com.fiap.mindcare.repository;

import com.fiap.mindcare.model.Profissional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Page<Profissional> findByEspecialidadeContainingIgnoreCase(String especialidade, Pageable pageable);

    Page<Profissional> findByConvenioContainingIgnoreCase(String convenio, Pageable pageable);

    Page<Profissional> findByEspecialidadeContainingIgnoreCaseAndConvenioContainingIgnoreCase(String especialidade,
                                                                                              String convenio,
                                                                                              Pageable pageable);
}
