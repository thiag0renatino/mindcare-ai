package com.fiap.mindcare.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MindCheckAiResponseDTO {

    private String risco;
    private List<String> sugestoes;
    private List<String> encaminhamentos;
    private String justificativa;
    private TriagemResponseDTO triagem;
    private EncaminhamentoResponseDTO encaminhamento;

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public List<String> getSugestoes() {
        return sugestoes;
    }

    public void setSugestoes(List<String> sugestoes) {
        this.sugestoes = sugestoes;
    }

    public List<String> getEncaminhamentos() {
        return encaminhamentos;
    }

    public void setEncaminhamentos(List<String> encaminhamentos) {
        this.encaminhamentos = encaminhamentos;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public TriagemResponseDTO getTriagem() {
        return triagem;
    }

    public void setTriagem(TriagemResponseDTO triagem) {
        this.triagem = triagem;
    }

    public EncaminhamentoResponseDTO getEncaminhamento() {
        return encaminhamento;
    }

    public void setEncaminhamento(EncaminhamentoResponseDTO encaminhamento) {
        this.encaminhamento = encaminhamento;
    }
}
