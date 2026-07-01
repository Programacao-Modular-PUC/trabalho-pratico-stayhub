package com.puc.stayhub.tarifa;
import com.puc.stayhub.model.Cliente;
import java.time.LocalDate;
public class ContextoTarifacao {
private final double valorBase;
private final LocalDate dataInicio;
private final LocalDate dataFim;
private final Cliente cliente;
private final int qtdReservasCliente;
public ContextoTarifacao(double valorBase, LocalDate dataInicio, LocalDate dataFim,
Cliente cliente, int qtdReservasCliente) {
this.valorBase = valorBase;
this.dataInicio = dataInicio;
this.dataFim = dataFim;
this.cliente = cliente;
this.qtdReservasCliente = qtdReservasCliente;
}
public double getValorBase() { return valorBase; }
public LocalDate getDataInicio() { return dataInicio; }
public LocalDate getDataFim() { return dataFim; }
public Cliente getCliente() { return cliente; }
public int getQtdReservasCliente() { return qtdReservasCliente; }
}