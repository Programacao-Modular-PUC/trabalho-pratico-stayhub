package com.puc.stayhub.tarifa;
public class DescontoClienteFrequente extends RegraTarifaDecorator {
public DescontoClienteFrequente(RegraTarifa anterior) { super(anterior); }
@Override
protected double aplicar(double valorAnterior, ContextoTarifacao ctx) {
int qtd = ctx.getQtdReservasCliente();
if (qtd >= 10) return valorAnterior * 0.85;
if (qtd >= 5) return valorAnterior * 0.90;
if (qtd >= 3) return valorAnterior * 0.95;
return valorAnterior;
}
@Override
protected String descricaoLocal() { return "Desconto cliente frequente"; }
}