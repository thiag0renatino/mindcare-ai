package com.fiap.mindcare.dto;

import com.fiap.mindcare.enuns.RiscoTriagem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TriagemRequestDTO {

    @NotNull
    private Long usuarioId;

    @NotNull
    private LocalDateTime dataHora;

    @NotBlank
    @Size(min = 5)
    private String relato;

    @NotBlank
    @Size(max = 10)
    private String risco;

    private String sugestao;

    public TriagemRequestDTO() {
    }

    public TriagemRequestDTO(Long usuarioId, LocalDateTime dataHora, String relato, String risco, String sugestao) {
        this.usuarioId = usuarioId;
        this.dataHora = dataHora;
        this.relato = relato;
        this.risco = risco;
        this.sugestao = sugestao;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getRelato() {
        return relato;
    }

    public void setRelato(String relato) {
        this.relato = relato;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public String getSugestao() {
        return sugestao;
    }

    public void setSugestao(String sugestao) {
        this.sugestao = sugestao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
