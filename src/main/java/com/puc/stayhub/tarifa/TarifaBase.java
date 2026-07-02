package com.puc.stayhub.tarifa;
public class TarifaBase implements RegraTarifa {
@Override
public double calcular(ContextoTarifacao ctx) {
return ctx.getValorBase();
}
@Override
public String descricao() { return "Tarifa base"; }
}