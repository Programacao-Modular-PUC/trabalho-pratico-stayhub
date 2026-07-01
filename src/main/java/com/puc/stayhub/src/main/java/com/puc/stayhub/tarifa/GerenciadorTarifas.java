package com.puc.stayhub.tarifa;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
/**
* Singleton que centraliza as regras de tarifacao do sistema.
*
* Justificativa do Singleton: a lista de feriados, promocoes e regras sazonais
* ativas e uma configuracao global do sistema. Ter mais de uma instancia
* poderia levar a valores divergentes para reservas identicas.
*/
public class GerenciadorTarifas {
private static volatile GerenciadorTarifas instancia;
private final Set<LocalDate> feriados = new HashSet<>();
private final List<Function<RegraTarifa, RegraTarifa>> decoradoresAtivos = new ArrayList<>();
private GerenciadorTarifas() {
aplicarConfiguracaoPadrao();
}
public static GerenciadorTarifas getInstancia() {
if (instancia == null) {
synchronized (GerenciadorTarifas.class) {
if (instancia == null) instancia = new GerenciadorTarifas();
}
}
return instancia;
}
public void adicionarFeriado(LocalDate data) { feriados.add(data); }
public void removerFeriado(LocalDate data) { feriados.remove(data); }
public Set<LocalDate> getFeriados() { return Collections.unmodifiableSet(feriados); }
public void adicionarPromocao(String nome, double percentual,
LocalDate inicio, LocalDate fim) {
decoradoresAtivos.add(anterior ->
new PromocaoTemporaria(anterior, nome, percentual, inicio, fim));
}
public RegraTarifa montarRegra() {
RegraTarifa regra = new TarifaBase();
for (Function<RegraTarifa, RegraTarifa> f : decoradoresAtivos) {
regra = f.apply(regra);
}
return regra;
}
public double calcular(ContextoTarifacao ctx) {
return montarRegra().calcular(ctx);
}
public void resetar() {
feriados.clear();
decoradoresAtivos.clear();
aplicarConfiguracaoPadrao();
}
private void aplicarConfiguracaoPadrao() {
decoradoresAtivos.add(AltaTemporada::new);
decoradoresAtivos.add(BaixaTemporada::new);
decoradoresAtivos.add(DescontoClienteFrequente::new);
decoradoresAtivos.add(anterior -> new Feriado(anterior, feriados));
}
}