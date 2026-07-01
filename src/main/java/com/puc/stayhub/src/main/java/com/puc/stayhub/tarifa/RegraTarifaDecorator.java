package com.puc.stayhub.tarifa;
/**
* Decorator abstrato: cada decorator envolve outra RegraTarifa e aplica
* transformacao adicional sobre o valor calculado por ela.
*/
public abstract class RegraTarifaDecorator implements RegraTarifa {
protected final RegraTarifa regraAnterior;
protected RegraTarifaDecorator(RegraTarifa regraAnterior) {
this.regraAnterior = regraAnterior;
}
@Override
public double calcular(ContextoTarifacao ctx) {
double valorAnterior = regraAnterior.calcular(ctx);
return aplicar(valorAnterior, ctx);
}
protected abstract double aplicar(double valorAnterior, ContextoTarifacao ctx);
@Override
public String descricao() {
return regraAnterior.descricao() + " + " + descricaoLocal();
}
protected abstract String descricaoLocal();
}