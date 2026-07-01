package com.puc.stayhub.tarifa;
import java.time.LocalDate;
public class PromocaoTemporaria extends RegraTarifaDecorator {
private final String nomePromocao;
private final double percentualDesconto;
private final LocalDate validadeInicio;
private final LocalDate validadeFim;
public PromocaoTemporaria(RegraTarifa anterior, String nomePromocao,
double percentualDesconto,
LocalDate validadeInicio, LocalDate validadeFim) {
super(anterior);
this.nomePromocao = nomePromocao;
this.percentualDesconto = percentualDesconto;
this.validadeInicio = validadeInicio;
this.validadeFim = validadeFim;
}
@Override
protected double aplicar(double valorAnterior, ContextoTarifacao ctx) {
LocalDate hoje = LocalDate.now();
if (!hoje.isBefore(validadeInicio) && !hoje.isAfter(validadeFim)) {
return valorAnterior * (1 - percentualDesconto);
}
return valorAnterior;
}
@Override
protected String descricaoLocal() {
return String.format("Promocao '%s' (-%.0f%%)", nomePromocao, percentualDesconto * 100);
}
}