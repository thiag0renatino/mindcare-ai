package com.fiap.mindcare.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TriagemResponseDTO extends RepresentationModel<TriagemResponseDTO> {

    private Long id;
    private LocalDateTime dataHora;
    private String relato;
    private String risco;
    private String sugestao;

    private UsuarioResponseDTO usuario;

    public TriagemResponseDTO() {
    }

    public TriagemResponseDTO(Long id, LocalDateTime dataHora, String risco, String relato, UsuarioResponseDTO usuario, String sugestao) {
        this.id = id;
        this.dataHora = dataHora;
        this.risco = risco;
        this.relato = relato;
        this.usuario = usuario;
        this.sugestao = sugestao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
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

    public UsuarioResponseDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResponseDTO usuario) {
        this.usuario = usuario;
    }
}
