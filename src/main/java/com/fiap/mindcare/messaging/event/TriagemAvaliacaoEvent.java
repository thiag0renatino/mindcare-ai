package com.fiap.mindcare.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

public class TriagemAvaliacaoEvent {

    private Long triagemId;
    private Long usuarioId;
    private String risco;
    private boolean encaminhamentoCriado;
    private List<String> especialidadesSugeridas;
    private LocalDateTime dataHora;

    public TriagemAvaliacaoEvent() {
    }

    public TriagemAvaliacaoEvent(Long triagemId,
                                 Long usuarioId,
                                 String risco,
                                 boolean encaminhamentoCriado,
                                 List<String> especialidadesSugeridas,
                                 LocalDateTime dataHora) {
        this.triagemId = triagemId;
        this.usuarioId = usuarioId;
        this.risco = risco;
        this.encaminhamentoCriado = encaminhamentoCriado;
        this.especialidadesSugeridas = especialidadesSugeridas;
        this.dataHora = dataHora;
    }

    public Long getTriagemId() {
        return triagemId;
    }

    public void setTriagemId(Long triagemId) {
        this.triagemId = triagemId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }

    public boolean isEncaminhamentoCriado() {
        return encaminhamentoCriado;
    }

    public void setEncaminhamentoCriado(boolean encaminhamentoCriado) {
        this.encaminhamentoCriado = encaminhamentoCriado;
    }

    public List<String> getEspecialidadesSugeridas() {
        return especialidadesSugeridas;
    }

    public void setEspecialidadesSugeridas(List<String> especialidadesSugeridas) {
        this.especialidadesSugeridas = especialidadesSugeridas;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
