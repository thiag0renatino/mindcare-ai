package com.fiap.mindcare.model;

import com.fiap.mindcare.enuns.PrioridadeEncaminhamento;
import com.fiap.mindcare.enuns.StatusEncaminhamento;
import com.fiap.mindcare.enuns.TipoEncaminhamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@SequenceGenerator(
        name = "encaminhamento_seq",
        sequenceName = "SEQ_ENCAMINHAMENTO_GS",
        allocationSize = 1)
public class Encaminhamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "encaminhamento_seq")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TipoEncaminhamento tipo;

    @Column(length = 120)
    private String exame = "N/A";

    @Column(length = 80)
    private String especialidade = "N/A";

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PrioridadeEncaminhamento prioridade = PrioridadeEncaminhamento.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private StatusEncaminhamento status = StatusEncaminhamento.PENDENTE;

    @Column(length = 400, nullable = false)
    private String observacao = "N/A";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "triagem_id", nullable = false)
    private Triagem triagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profissional_id")
    private Profissional profissional;

    public Encaminhamento() {
    }

    public Encaminhamento(Long id, StatusEncaminhamento status, Triagem triagem, Profissional profissional, String observacao, PrioridadeEncaminhamento prioridade, String especialidade, String exame, TipoEncaminhamento tipo) {
        this.id = id;
        this.status = status;
        this.triagem = triagem;
        this.profissional = profissional;
        this.observacao = observacao;
        this.prioridade = prioridade;
        this.especialidade = especialidade;
        this.exame = exame;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoEncaminhamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoEncaminhamento tipo) {
        this.tipo = tipo;
    }

    public String getExame() {
        return exame;
    }

    public void setExame(String exame) {
        this.exame = exame;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public PrioridadeEncaminhamento getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(PrioridadeEncaminhamento prioridade) {
        this.prioridade = prioridade;
    }

    public StatusEncaminhamento getStatus() {
        return status;
    }

    public void setStatus(StatusEncaminhamento status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Triagem getTriagem() {
        return triagem;
    }

    public void setTriagem(Triagem triagem) {
        this.triagem = triagem;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Encaminhamento that = (Encaminhamento) o;
        return Objects.equals(id, that.id) && tipo == that.tipo && Objects.equals(exame, that.exame) && Objects.equals(especialidade, that.especialidade) && prioridade == that.prioridade && status == that.status && Objects.equals(observacao, that.observacao) && Objects.equals(triagem, that.triagem) && Objects.equals(profissional, that.profissional);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tipo, exame, especialidade, prioridade, status, observacao, triagem, profissional);
    }
}
