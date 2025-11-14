package com.fiap.mindcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class EncaminhamentoRequestDTO {

    @NotBlank
    @Size(max = 20)
    // EXAME, ESPECIALIDADE, PROFISSIONAL, HABITO
    private String tipo;

    @Size(max = 120)
    private String exame;

    @Size(max = 80)
    private String especialidade;

    @Size(max = 10)
    // BAIXA, MEDIA, ALTA
    private String prioridade;

    @Size(max = 12)
    // PENDENTE, AGENDADO, CONCLUIDO, CANCELADO
    private String status;

    @Size(max = 400)
    private String observacao;

    @NotNull
    private Long triagemId;

    private Long profissionalId;

    public EncaminhamentoRequestDTO() {
    }

    public EncaminhamentoRequestDTO(String tipo, Long triagemId, Long profissionalId, String observacao, String especialidade, String prioridade, String status, String exame) {
        this.tipo = tipo;
        this.triagemId = triagemId;
        this.profissionalId = profissionalId;
        this.observacao = observacao;
        this.especialidade = especialidade;
        this.prioridade = prioridade;
        this.status = status;
        this.exame = exame;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
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

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getTriagemId() {
        return triagemId;
    }

    public void setTriagemId(Long triagemId) {
        this.triagemId = triagemId;
    }

    public Long getProfissionalId() {
        return profissionalId;
    }

    public void setProfissionalId(Long profissionalId) {
        this.profissionalId = profissionalId;
    }
}
