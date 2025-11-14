package com.fiap.mindcare.repository;

import com.fiap.mindcare.enuns.PrioridadeEncaminhamento;
import com.fiap.mindcare.enuns.StatusEncaminhamento;
import com.fiap.mindcare.enuns.TipoEncaminhamento;
import com.fiap.mindcare.model.Encaminhamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncaminhamentoRepository extends JpaRepository<Encaminhamento, Long> {

    Page<Encaminhamento> findByTriagemId(Long triagemId, Pageable pageable);

    Page<Encaminhamento> findByProfissionalId(Long profissionalId, Pageable pageable);

    Page<Encaminhamento> findByStatus(StatusEncaminhamento status, Pageable pageable);

    Page<Encaminhamento> findByPrioridade(PrioridadeEncaminhamento prioridade, Pageable pageable);

    Page<Encaminhamento> findByTipo(TipoEncaminhamento tipo, Pageable pageable);
}
