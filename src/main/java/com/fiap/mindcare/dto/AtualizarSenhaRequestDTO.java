package com.fiap.mindcare.dto;

import jakarta.validation.constraints.Size;

public class AtualizarSenhaRequestDTO {

    private String senhaAtual;

    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senhaNova;

    public AtualizarSenhaRequestDTO() {
    }

    public AtualizarSenhaRequestDTO(String senhaAtual, String senhaNova) {
        this.senhaAtual = senhaAtual;
        this.senhaNova = senhaNova;
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getSenhaNova() {
        return senhaNova;
    }

    public void setSenhaNova(String senhaNova) {
        this.senhaNova = senhaNova;
    }
}
