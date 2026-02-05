package com.fiap.mindcare.model;

import com.fiap.mindcare.enuns.TipoUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@SequenceGenerator(
        name = "usuario_seq",
        sequenceName = "SEQ_USUARIO_GS",
        allocationSize = 1)
public class UsuarioSistema implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 100, nullable = false)
    private String nome;

    @NotBlank
    @Email(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Column(length = 150, unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(length = 255, nullable = false)
    private String senha;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20, nullable = false)
    private TipoUsuario tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    public UsuarioSistema() {
    }

    public UsuarioSistema(Long id, String senha, Empresa empresa, TipoUsuario tipo, String email, String nome) {
        this.id = id;
        this.senha = senha;
        this.empresa = empresa;
        this.tipo = tipo;
        this.email = email;
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

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioSistema that = (UsuarioSistema) o;
        return Objects.equals(id, that.id) && Objects.equals(nome, that.nome) && Objects.equals(email, that.email) && Objects.equals(senha, that.senha) && tipo == that.tipo && Objects.equals(empresa, that.empresa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, email, senha, tipo, empresa);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.tipo == TipoUsuario.ADMIN) return List.of(new SimpleGrantedAuthority("ADMIN"),
                new SimpleGrantedAuthority("USER"));
        else return List.of(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
