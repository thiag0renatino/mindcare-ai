package com.fiap.mindcare.dto;

public class EncaminhamentoRecomendadoDTO {

    private Long profissionalId;
    private String nome;
    private String especialidade;
    private String contato;
    private String convenio;

    public EncaminhamentoRecomendadoDTO() {
    }

    public EncaminhamentoRecomendadoDTO(Long profissionalId, String nome, String especialidade, String contato, String convenio) {
        this.profissionalId = profissionalId;
        this.nome = nome;
        this.especialidade = especialidade;
        this.contato = contato;
        this.convenio = convenio;
    }

    public Long getProfissionalId() {
        return profissionalId;
    }

    public void setProfissionalId(Long profissionalId) {
        this.profissionalId = profissionalId;
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

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getConvenio() {
        return convenio;
    }

    public void setConvenio(String convenio) {
        this.convenio = convenio;
    }
}
