package com.fiap.mindcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MindCheckAiRequestDTO {

    @NotBlank
    @Size(min = 10)
    private String relato;

    private String sintomas;
    private String humor;
    private String rotina;

    public String getRelato() {
        return relato;
    }

    public void setRelato(String relato) {
        this.relato = relato;
    }

    public String getSintomas() {
        return sintomas;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public String getHumor() {
        return humor;
    }

    public void setHumor(String humor) {
        this.humor = humor;
    }

    public String getRotina() {
        return rotina;
    }

    public void setRotina(String rotina) {
        this.rotina = rotina;
    }
}
