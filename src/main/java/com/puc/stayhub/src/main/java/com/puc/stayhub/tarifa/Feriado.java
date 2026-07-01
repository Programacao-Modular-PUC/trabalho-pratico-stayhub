package com.puc.stayhub.tarifa;
import java.time.LocalDate;
import java.util.Set;
public class Feriado extends RegraTarifaDecorator {
private static final double MULTIPLICADOR = 1.20;
private final Set<LocalDate> feriados;
public Feriado(RegraTarifa anterior, Set<LocalDate> feriados) {
super(anterior);
this.feriados = feriados;
}
@Override
protected double aplicar(double valorAnterior, ContextoTarifacao ctx) {
LocalDate d = ctx.getDataInicio();
while (!d.isAfter(ctx.getDataFim())) {
if (feriados.contains(d)) return valorAnterior * MULTIPLICADOR;
d = d.plusDays(1);
}
return valorAnterior;
}
@Override
protected String descricaoLocal() { return "Feriado (+20%)"; }
}