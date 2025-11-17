package com.fiap.mindcare.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.RepresentationModel;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmpresaResponseDTO extends RepresentationModel<EmpresaResponseDTO> {

    private Long id;
    private String cnpj;
    private String nome;
    private String planoSaude;

    public EmpresaResponseDTO() {
    }

    public EmpresaResponseDTO(Long id, String planoSaude, String nome, String cnpj) {
        this.id = id;
        this.planoSaude = planoSaude;
        this.nome = nome;
        this.cnpj = cnpj;
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
}
