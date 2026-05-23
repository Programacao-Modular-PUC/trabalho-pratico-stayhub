package com.puc.stayhub.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Classe abstrata que representa um quarto. A estrategia de heranca utilizada
 * e SINGLE_TABLE: todos os subtipos sao persistidos em uma unica tabela
 * "quartos", diferenciados pela coluna discriminadora "tipo".
 *
 * <p>Atributos comuns a todos os quartos:
 * <ul>
 *     <li>id</li>
 *     <li>valorBase</li>
 *     <li>possuiAR</li>
 *     <li>possuiHidro</li>
 * </ul>
 */
@Entity
@Table(name = "quartos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "tipo", include = JsonTypeInfo.As.EXISTING_PROPERTY)
public abstract class Quarto {

    /** Adicional sobre a diaria caso o quarto possua ar-condicionado. */
    public static final double ADICIONAL_AR = 30.0;

    /** Adicional sobre a diaria caso o quarto possua hidromassagem. */
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
    private Residencia residencia;

    /**
     * Retorna o tipo concreto do quarto (usado em DTOs e respostas REST).
     */
    @Transient
    public abstract TipoQuarto getTipo();

    /**
     * Capacidade maxima de hospedes que o quarto comporta.
     */
    @Transient
    public abstract int getCapacidadeMaxima();

    /**
     * Calcula o valor da diaria para a configuracao do quarto.
     *
     * @param numHospedes      quantidade de hospedes para a estadia
     * @param solicitouBerco   indica se o cliente solicitou berco (relevante apenas em Quarto Duplo)
     * @return valor da diaria em reais
     */
    public abstract double calcularDiaria(int numHospedes, boolean solicitouBerco);

    /**
     * Soma os adicionais comuns (AR e hidromassagem) sobre o valor informado.
     */
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
