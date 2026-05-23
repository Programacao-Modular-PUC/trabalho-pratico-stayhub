package com.puc.stayhub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do cliente e obrigatorio")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "CPF e obrigatorio")
    @Size(min = 11, max = 14)
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Email(message = "E-mail invalido")
    @Column(length = 120)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    public Cliente() {}

    public Cliente(String nome, String cpf, String email, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}
