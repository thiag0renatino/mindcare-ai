package com.fiap.mindcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AtualizarNomeRequestDTO {

    @NotBlank(message = "O nome não pode ser vazio")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nomeNovo;

    public AtualizarNomeRequestDTO() {
    }

    public AtualizarNomeRequestDTO(String nomeNovo) {
        this.nomeNovo = nomeNovo;
    }

    public String getNomeNovo() {
        return nomeNovo;
    }

    public void setNomeNovo(String nomeNovo) {
        this.nomeNovo = nomeNovo;
    }
}
