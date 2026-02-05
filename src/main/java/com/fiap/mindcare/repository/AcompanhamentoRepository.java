package com.fiap.mindcare.repository;

import com.fiap.mindcare.enuns.TipoEventoAcompanhamento;
import com.fiap.mindcare.model.Acompanhamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcompanhamentoRepository extends JpaRepository<Acompanhamento, Long> {

    Page<Acompanhamento> findByEncaminhamentoIdOrderByDataEventoDesc(Long encaminhamentoId, Pageable pageable);

    Page<Acompanhamento> findByTipoEvento(TipoEventoAcompanhamento tipoEvento, Pageable pageable);

    Page<Acompanhamento> findByEncaminhamentoTriagemUsuarioId(Long usuarioId, Pageable pageable);
}

