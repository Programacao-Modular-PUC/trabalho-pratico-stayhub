package com.puc.stayhub.tarifa;
import java.time.Month;
public class BaixaTemporada extends RegraTarifaDecorator {
private static final double MULTIPLICADOR = 0.85;
public BaixaTemporada(RegraTarifa anterior) { super(anterior); }
@Override
protected double aplicar(double valorAnterior, ContextoTarifacao ctx) {
Month mes = ctx.getDataInicio().getMonth();
if (mes == Month.MARCH || mes == Month.APRIL || mes == Month.MAY || mes == Month.AUGUST) {
return valorAnterior * MULTIPLICADOR;
}
return valorAnterior;
}
@Override
protected String descricaoLocal() { return "Baixa temporada (-15%)"; }
}