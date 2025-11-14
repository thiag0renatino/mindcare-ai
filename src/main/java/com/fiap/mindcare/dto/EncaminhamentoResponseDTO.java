package com.fiap.mindcare.dto;

import org.springframework.hateoas.RepresentationModel;

public class EncaminhamentoResponseDTO extends RepresentationModel<EncaminhamentoResponseDTO> {


    private Long id;
    private String tipo;
    private String exame;
    private String especialidade;
    private String prioridade;
    private String status;
    private String observacao;

    private TriagemResponseDTO triagem;
    private ProfissionalResponseDTO profissional;

    public EncaminhamentoResponseDTO() {
    }

    public EncaminhamentoResponseDTO(Long id, String exame, String tipo, String especialidade, String prioridade, String status, String observacao, TriagemResponseDTO triagem, ProfissionalResponseDTO profissional) {
        this.id = id;
        this.exame = exame;
        this.tipo = tipo;
        this.especialidade = especialidade;
        this.prioridade = prioridade;
        this.status = status;
        this.observacao = observacao;
        this.triagem = triagem;
        this.profissional = profissional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getExame() {
        return exame;
    }

    public void setExame(String exame) {
        this.exame = exame;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TriagemResponseDTO getTriagem() {
        return triagem;
    }

    public void setTriagem(TriagemResponseDTO triagem) {
        this.triagem = triagem;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public ProfissionalResponseDTO getProfissional() {
        return profissional;
    }

    public void setProfissional(ProfissionalResponseDTO profissional) {
        this.profissional = profissional;
    }
}
