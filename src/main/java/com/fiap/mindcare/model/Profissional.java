package com.fiap.mindcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
@SequenceGenerator(
        name = "profissional_seq",
        sequenceName = "SEQ_PROFISSIONAL_GS",
        allocationSize = 1)
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profissional_seq")
    private Long id;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String nome;

    @NotBlank
    @Column(length = 80, nullable = false)
    private String especialidade;

    @Column(length = 120)
    private String convenio;

    @Column(length = 160)
    private String contato;

    public Profissional() {
    }

    public Profissional(Long id, String nome, String especialidade, String convenio, String contato) {
        this.id = id;
        this.nome = nome;
        this.especialidade = especialidade;
        this.convenio = convenio;
        this.contato = contato;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Profissional that = (Profissional) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome) && Objects.equals(especialidade, that.especialidade) && Objects.equals(convenio, that.convenio) && Objects.equals(contato, that.contato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, especialidade, convenio, contato);
    }
}
