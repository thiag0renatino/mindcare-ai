package com.fiap.mindcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class AcompanhamentoRequestDTO {

    @NotNull
    private Long encaminhamentoId;

    @NotNull
    private LocalDateTime dataEvento;

    @NotBlank
    @Size(max = 20)
    // AGENDAMENTO, RESULTADO, ALTA, CANCELAMENTO, OBSERVACAO
    private String tipoEvento;

    private String descricao;

    @Size(max = 400)
    private String anexoUrl;

    public AcompanhamentoRequestDTO() {
    }

    public AcompanhamentoRequestDTO(Long encaminhamentoId, String anexoUrl, String descricao, String tipoEvento, LocalDateTime dataEvento) {
        this.encaminhamentoId = encaminhamentoId;
        this.anexoUrl = anexoUrl;
        this.descricao = descricao;
        this.tipoEvento = tipoEvento;
        this.dataEvento = dataEvento;
    }

    public Long getEncaminhamentoId() {
        return encaminhamentoId;
    }

    public void setEncaminhamentoId(Long encaminhamentoId) {
        this.encaminhamentoId = encaminhamentoId;
    }

    public LocalDateTime getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDateTime dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
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
}
