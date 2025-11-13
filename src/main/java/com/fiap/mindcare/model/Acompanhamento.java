package com.fiap.mindcare.model;

import com.fiap.mindcare.enuns.TipoEventoAcompanhamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@SequenceGenerator(
        name = "acompanhamento_seq",
        sequenceName = "SEQ_ACOMPANHAMENTO_GS",
        allocationSize = 1)
public class Acompanhamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acompanhamento_seq")
    private Long id;

    @NotNull
    @Column(name = "data_evento", nullable = false)
    private LocalDateTime dataEvento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", length = 20, nullable = false)
    private TipoEventoAcompanhamento tipoEvento;

    @Lob
    private String descricao;

    @Column(name = "anexo_url", length = 400)
    private String anexoUrl = "N/A";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encaminhamento_id", nullable = false)
    private Encaminhamento encaminhamento;

    public Acompanhamento() {
    }

    public Acompanhamento(Long id, TipoEventoAcompanhamento tipoEvento, LocalDateTime dataEvento, String descricao, String anexoUrl, Encaminhamento encaminhamento) {
        this.id = id;
        this.tipoEvento = tipoEvento;
        this.dataEvento = dataEvento;
        this.descricao = descricao;
        this.anexoUrl = anexoUrl;
        this.encaminhamento = encaminhamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDateTime dataEvento) {
        this.dataEvento = dataEvento;
    }

    public TipoEventoAcompanhamento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoAcompanhamento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAnexoUrl() {
        return anexoUrl;
    }

    public void setAnexoUrl(String anexoUrl) {
        this.anexoUrl = anexoUrl;
    }

    public Encaminhamento getEncaminhamento() {
        return encaminhamento;
    }

    public void setEncaminhamento(Encaminhamento encaminhamento) {
        this.encaminhamento = encaminhamento;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Acompanhamento that = (Acompanhamento) o;
        return Objects.equals(id, that.id) && Objects.equals(dataEvento, that.dataEvento) && tipoEvento == that.tipoEvento && Objects.equals(descricao, that.descricao) && Objects.equals(anexoUrl, that.anexoUrl) && Objects.equals(encaminhamento, that.encaminhamento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataEvento, tipoEvento, descricao, anexoUrl, encaminhamento);
    }
}
