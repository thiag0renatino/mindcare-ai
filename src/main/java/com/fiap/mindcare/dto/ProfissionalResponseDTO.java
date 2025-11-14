package com.fiap.mindcare.dto;

public class ProfissionalResponseDTO {

    private Long id;
    private String nome;
    private String especialidade;
    private String convenio;
    private String contato;

    public ProfissionalResponseDTO() {
    }

    public ProfissionalResponseDTO(Long id, String convenio, String especialidade, String contato, String nome) {
        this.id = id;
        this.convenio = convenio;
        this.especialidade = especialidade;
        this.contato = contato;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
