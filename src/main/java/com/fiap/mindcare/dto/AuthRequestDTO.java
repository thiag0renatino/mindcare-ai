package com.fiap.mindcare.dto;

public class AuthRequestDTO {

    private String nome;
    private String email;
    private String senha;
    private String empresa;

    public AuthRequestDTO() {
    }

    public AuthRequestDTO(String nome, String empresa, String email, String senha) {
        this.nome = nome;
        this.empresa = empresa;
        this.email = email;
        this.senha = senha;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
