package com.puc.stayhub.tarifa;
import java.time.Month;
public class AltaTemporada extends RegraTarifaDecorator {
private static final double MULTIPLICADOR = 1.35;
public AltaTemporada(RegraTarifa anterior) { super(anterior); }
@Override
protected double aplicar(double valorAnterior, ContextoTarifacao ctx) {
Month mes = ctx.getDataInicio().getMonth();
if (mes == Month.DECEMBER || mes == Month.JANUARY || mes == Month.JULY) {
return valorAnterior * MULTIPLICADOR;
}
return valorAnterior;
}
@Override
protected String descricaoLocal() { return "Alta temporada (+35%)"; }
}