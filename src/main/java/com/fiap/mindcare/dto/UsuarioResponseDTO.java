package com.fiap.mindcare.dto;

public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private String tipo;

    private EmpresaResponseDTO empresa;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(Long id, String tipo, String email, EmpresaResponseDTO empresa, String nome) {
        this.id = id;
        this.tipo = tipo;
        this.email = email;
        this.empresa = empresa;
        this.nome = nome;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public EmpresaResponseDTO getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaResponseDTO empresa) {
        this.empresa = empresa;
    }
}
