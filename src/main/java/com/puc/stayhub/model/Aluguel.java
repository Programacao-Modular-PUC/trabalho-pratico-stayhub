package com.puc.stayhub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "alugueis")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quarto_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "residencia"})
    private Quarto quarto;

    @NotNull
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @NotNull
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Positive
    @Column(name = "num_hospedes", nullable = false)
    private int numHospedes;

    @Column(name = "solicitou_berco", nullable = false)
    private boolean solicitouBerco;

    @Column(name = "valor_diaria", nullable = false)
    private double valorDiaria;

    @Column(name = "valor_total", nullable = false)
    private double valorTotal;

    public Aluguel() {}

    public long calcularQuantidadeDiarias() {
        if (dataInicio == null || dataFim == null) return 0;
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFim);
        return Math.max(dias, 1);
    }

    public void recalcularValores() {
        this.valorDiaria = quarto.calcularDiaria(numHospedes, solicitouBerco);
        this.valorTotal = valorDiaria * calcularQuantidadeDiarias();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Quarto getQuarto() { return quarto; }
    public void setQuarto(Quarto quarto) { this.quarto = quarto; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public int getNumHospedes() { return numHospedes; }
    public void setNumHospedes(int numHospedes) { this.numHospedes = numHospedes; }

    public boolean isSolicitouBerco() { return solicitouBerco; }
    public void setSolicitouBerco(boolean solicitouBerco) { this.solicitouBerco = solicitouBerco; }

    public double getValorDiaria() { return valorDiaria; }
    public void setValorDiaria(double valorDiaria) { this.valorDiaria = valorDiaria; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }
}