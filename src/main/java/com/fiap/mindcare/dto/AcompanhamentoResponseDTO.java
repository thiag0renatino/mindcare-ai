package com.fiap.mindcare.dto;

import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

public class AcompanhamentoResponseDTO extends RepresentationModel<AcompanhamentoResponseDTO> {

    private Long id;
    private LocalDateTime dataEvento;
    private String tipoEvento;
    private String descricao;
    private String anexoUrl;

    private EncaminhamentoResponseDTO encaminhamento;

    public AcompanhamentoResponseDTO() {
    }

    public AcompanhamentoResponseDTO(Long id, String descricao, EncaminhamentoResponseDTO encaminhamento, String anexoUrl, String tipoEvento, LocalDateTime dataEvento) {
        this.id = id;
        this.descricao = descricao;
        this.encaminhamento = encaminhamento;
        this.anexoUrl = anexoUrl;
        this.tipoEvento = tipoEvento;
        this.dataEvento = dataEvento;
    }

    public LocalDateTime getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDateTime dataEvento) {
        this.dataEvento = dataEvento;
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

    public EncaminhamentoResponseDTO getEncaminhamento() {
        return encaminhamento;
    }

    public void setEncaminhamento(EncaminhamentoResponseDTO encaminhamento) {
        this.encaminhamento = encaminhamento;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
