package com.fiap.mindcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmpresaRequestDTO {

    @NotBlank
    @Size(min = 14, max = 14)
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter exatamente 14 dígitos numéricos")
    private String cnpj;

    @NotBlank
    @Size(max = 120)
    private String nome;

    @Size(max = 120)
    private String planoSaude;

    public EmpresaRequestDTO() {
    }

    public EmpresaRequestDTO(String cnpj, String nome, String planoSaude) {
        this.cnpj = cnpj;
        this.nome = nome;
        this.planoSaude = planoSaude;
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
}
