package com.fiap.mindcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfissionalRequestDTO {

    @NotBlank
    @Size(max = 100)
    private String nome;

    @NotBlank
    @Size(max = 80)
    private String especialidade;

    @Size(max = 120)
    private String convenio;

    @Size(max = 160)
    private String contato;

    public ProfissionalRequestDTO() {
    }

    public ProfissionalRequestDTO(String nome, String convenio, String especialidade, String contato) {
        this.nome = nome;
        this.convenio = convenio;
        this.especialidade = especialidade;
        this.contato = contato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }
}
