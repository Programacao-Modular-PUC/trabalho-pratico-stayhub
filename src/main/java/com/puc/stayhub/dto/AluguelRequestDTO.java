package com.puc.stayhub.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class AluguelRequestDTO {

    @NotNull(message = "Id do quarto e obrigatorio")
    private Long quartoId;

    @NotNull(message = "Id do cliente e obrigatorio")
    private Long clienteId;

    @NotNull(message = "Data de inicio e obrigatoria")
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim e obrigatoria")
    private LocalDate dataFim;

    @Positive(message = "Numero de hospedes deve ser positivo")
    private int numHospedes;

    private boolean solicitouBerco;

    public Long getQuartoId() { return quartoId; }
    public void setQuartoId(Long quartoId) { this.quartoId = quartoId; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public int getNumHospedes() { return numHospedes; }
    public void setNumHospedes(int numHospedes) { this.numHospedes = numHospedes; }

    public boolean isSolicitouBerco() { return solicitouBerco; }
    public void setSolicitouBerco(boolean solicitouBerco) { this.solicitouBerco = solicitouBerco; }
}