package com.puc.stayhub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "residencias")
public class Residencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome da residencia e obrigatorio")
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Endereco e obrigatorio")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String endereco;

    @Size(max = 80)
    @Column(length = 80)
    private String cidade;

    @Size(max = 2)
    @Column(length = 2)
    private String uf;

    @JsonIgnore
    @OneToMany(mappedBy = "residencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Quarto> quartos = new ArrayList<>();

    public Residencia() {}

    public Residencia(String nome, String endereco, String cidade, String uf) {
        this.nome = nome;
        this.endereco = endereco;
        this.cidade = cidade;
        this.uf = uf;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public List<Quarto> getQuartos() { return quartos; }
    public void setQuartos(List<Quarto> quartos) { this.quartos = quartos; }
}