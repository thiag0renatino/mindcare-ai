package com.fiap.mindcare.model;

import com.fiap.mindcare.enuns.RiscoTriagem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@SequenceGenerator(
        name = "triagem_seq",
        sequenceName = "SEQ_TRIAGEM_GS",
        allocationSize = 1)
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Lob
    private String relato;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(length = 10, nullable = false)
    private RiscoTriagem risco;

    @Lob
    private String sugestao;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioSistema usuario;

    public Triagem() {
    }

    public Triagem(Long id, LocalDateTime dataHora, String relato, RiscoTriagem risco, String sugestao, UsuarioSistema usuario) {
        this.id = id;
        this.dataHora = dataHora;
        this.relato = relato;
        this.risco = risco;
        this.sugestao = sugestao;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getRelato() {
        return relato;
    }

    public void setRelato(String relato) {
        this.relato = relato;
    }

    public RiscoTriagem getRisco() {
        return risco;
    }

    public void setRisco(RiscoTriagem risco) {
        this.risco = risco;
    }

    public String getSugestao() {
        return sugestao;
    }

    public void setSugestao(String sugestao) {
        this.sugestao = sugestao;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Triagem triagem = (Triagem) o;
        return Objects.equals(id, triagem.id) && Objects.equals(dataHora, triagem.dataHora) && Objects.equals(relato, triagem.relato) && risco == triagem.risco && Objects.equals(sugestao, triagem.sugestao) && Objects.equals(usuario, triagem.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataHora, relato, risco, sugestao, usuario);
    }
}
