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
 * Justificativa do Singleton: a lista de feriados, promocoes e regras
 * sazonais ativas e uma configuracao global do sistema. Ter mais de uma
 * instancia poderia levar a valores divergentes para reservas identicas.
 *
 * Comportamento padrao: apenas o decorator Feriado esta ativo (no-op enquanto
 * o Set de feriados estiver vazio). Alta/Baixa temporada e Desconto Cliente
 * Frequente sao opt-in explicito, para que o preco cadastrado no quarto seja
 * respeitado por padrao (comportamento estilo Airbnb: o dono decide quando
 * ativar regras de sazonalidade).
 */
public class GerenciadorTarifas {

    private static volatile GerenciadorTarifas instancia;
    private final Set<LocalDate> feriados = new HashSet<>();
    private final List<Function<RegraTarifa, RegraTarifa>> promocoes = new ArrayList<>();
    private boolean altaTemporadaAtiva = false;
    private boolean baixaTemporadaAtiva = false;
    private boolean descontoFrequenteAtivo = false;

    private GerenciadorTarifas() {}

    public static GerenciadorTarifas getInstancia() {
        if (instancia == null) {
            synchronized (GerenciadorTarifas.class) {
                if (instancia == null) instancia = new GerenciadorTarifas();
            }
        }
        return instancia;
    }

    // ---- Feriados ----
    public void adicionarFeriado(LocalDate data) { feriados.add(data); }
    public void removerFeriado(LocalDate data) { feriados.remove(data); }
    public Set<LocalDate> getFeriados() { return Collections.unmodifiableSet(feriados); }

    // ---- Promocoes ----
    public void adicionarPromocao(String nome, double percentual,
                                  LocalDate inicio, LocalDate fim) {
        promocoes.add(anterior ->
            new PromocaoTemporaria(anterior, nome, percentual, inicio, fim));
    }

    // ---- Regras sazonais (opt-in) ----
    public void setAltaTemporadaAtiva(boolean ativa) { this.altaTemporadaAtiva = ativa; }
    public void setBaixaTemporadaAtiva(boolean ativa) { this.baixaTemporadaAtiva = ativa; }
    public void setDescontoFrequenteAtivo(boolean ativa) { this.descontoFrequenteAtivo = ativa; }
    public boolean isAltaTemporadaAtiva() { return altaTemporadaAtiva; }
    public boolean isBaixaTemporadaAtiva() { return baixaTemporadaAtiva; }
    public boolean isDescontoFrequenteAtivo() { return descontoFrequenteAtivo; }

    // ---- Montagem e calculo ----
    public RegraTarifa montarRegra() {
        RegraTarifa regra = new TarifaBase();
        // Feriado sempre presente (no-op enquanto Set vazio)
        regra = new Feriado(regra, feriados);
        if (altaTemporadaAtiva)       regra = new AltaTemporada(regra);
        if (baixaTemporadaAtiva)      regra = new BaixaTemporada(regra);
        if (descontoFrequenteAtivo)   regra = new DescontoClienteFrequente(regra);
        for (Function<RegraTarifa, RegraTarifa> f : promocoes) {
            regra = f.apply(regra);
        }
        return regra;
    }

    public double calcular(ContextoTarifacao ctx) {
        return montarRegra().calcular(ctx);
    }

    public void resetar() {
        feriados.clear();
        promocoes.clear();
        altaTemporadaAtiva = false;
        baixaTemporadaAtiva = false;
        descontoFrequenteAtivo = false;
    }
}