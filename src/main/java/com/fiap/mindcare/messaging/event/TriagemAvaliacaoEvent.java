package com.fiap.mindcare.messaging.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TriagemAvaliacaoEvent implements Serializable {

    private Long triagemId;
    private Long usuarioId;
    private String risco;
    private boolean encaminhamentoCriado;
    private String especialidadeSugerida;
    private LocalDateTime dataHora;

    public TriagemAvaliacaoEvent() {
    }

    public TriagemAvaliacaoEvent(Long triagemId,
                                 Long usuarioId,
                                 String risco,
                                 boolean encaminhamentoCriado,
                                 String especialidadeSugerida,
                                 LocalDateTime dataHora) {
        this.triagemId = triagemId;
        this.usuarioId = usuarioId;
        this.risco = risco;
        this.encaminhamentoCriado = encaminhamentoCriado;
        this.especialidadeSugerida = especialidadeSugerida;
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

    public String getEspecialidadeSugerida() {
        return especialidadeSugerida;
    }

    public void setEspecialidadeSugerida(String especialidadeSugerida) {
        this.especialidadeSugerida = especialidadeSugerida;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
