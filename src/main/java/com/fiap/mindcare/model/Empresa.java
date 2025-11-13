package com.fiap.mindcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
@SequenceGenerator(
        name = "empresa_seq",
        sequenceName = "SEQ_EMPRESA_GS",
        allocationSize = 1)
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empresa_seq")
    private Long id;

    @NotBlank
    @Column(length = 14, unique = true, nullable = false)
    private String cnpj;

    @NotBlank
    @Column(length = 120, nullable = false)
    private String nome;

    @Column(length = 120)
    private String planoSaude;

    public Empresa() {
    }

    public Empresa(Long id, String cnpj, String nome, String planoSaude) {
        this.id = id;
        this.cnpj = cnpj;
        this.nome = nome;
        this.planoSaude = planoSaude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPlanoSaude() {
        return planoSaude;
    }

    public void setPlanoSaude(String planoSaude) {
        this.planoSaude = planoSaude;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Empresa empresa = (Empresa) o;
        return Objects.equals(id, empresa.id) && Objects.equals(cnpj, empresa.cnpj) && Objects.equals(nome, empresa.nome) && Objects.equals(planoSaude, empresa.planoSaude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cnpj, nome, planoSaude);
    }
}
