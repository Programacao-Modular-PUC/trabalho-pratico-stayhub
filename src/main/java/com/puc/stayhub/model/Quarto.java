package com.puc.stayhub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "quartos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo", include = JsonTypeInfo.As.EXISTING_PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = QuartoIndividual.class, name = "INDIVIDUAL"),
    @JsonSubTypes.Type(value = QuartoDuplo.class, name = "DUPLO"),
    @JsonSubTypes.Type(value = QuartoFamilia.class, name = "FAMILIA")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public abstract class Quarto {

    public static final double ADICIONAL_AR = 30.0;
    public static final double ADICIONAL_HIDRO = 50.0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Valor base e obrigatorio")
    @Positive(message = "Valor base deve ser positivo")
    @Column(nullable = false)
    private Double valorBase;

    @Column(nullable = false)
    private boolean possuiAR;

    @Column(nullable = false)
    private boolean possuiHidro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "residencia_id", nullable = false)
    @JsonIgnoreProperties({"quartos", "hibernateLazyInitializer", "handler"})
    private Residencia residencia;

    @Transient
    public abstract TipoQuarto getTipo();

    @Transient
    public abstract int getCapacidadeMaxima();

    public abstract double calcularDiaria(int numHospedes, boolean solicitouBerco);

    protected double aplicarAdicionaisComuns(double base) {
        double valor = base;
        if (possuiAR)    valor += ADICIONAL_AR;
        if (possuiHidro) valor += ADICIONAL_HIDRO;
        return valor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAR() { return possuiAR; }
    public void setPossuiAR(boolean possuiAR) { this.possuiAR = possuiAR; }

    public boolean isPossuiHidro() { return possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public Residencia getResidencia() { return residencia; }
    public void setResidencia(Residencia residencia) { this.residencia = residencia; }
}