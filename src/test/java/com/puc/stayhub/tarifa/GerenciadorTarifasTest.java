package com.puc.stayhub.tarifa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GerenciadorTarifas - Singleton + Strategy + Decorator")
class GerenciadorTarifasTest {

    private GerenciadorTarifas gerenciador;

    @BeforeEach
    void setUp() {
        gerenciador = GerenciadorTarifas.getInstancia();
        gerenciador.resetar();
    }

    @Test
    @DisplayName("getInstancia sempre retorna a mesma instancia (Singleton)")
    void singleton_retornaSempreMesmaInstancia() {
        GerenciadorTarifas a = GerenciadorTarifas.getInstancia();
        GerenciadorTarifas b = GerenciadorTarifas.getInstancia();
        assertSame(a, b);
    }

    @Test
    @DisplayName("Feriado adicionado aumenta o valor em 20% sobre o valor base")
    void feriado_aplicaAdicionalDe20Porcento() {
        LocalDate natal = LocalDate.of(2026, 12, 25);
        gerenciador.adicionarFeriado(natal);

        ContextoTarifacao ctx = new ContextoTarifacao(
            200.0, natal, natal.plusDays(1), null, 0);
        double valor = gerenciador.calcular(ctx);

        // sem alta temporada ativa: 200 * 1.20 (feriado) = 240
        assertEquals(240.0, valor, 0.01);
    }

    @Test
    @DisplayName("Alta temporada opt-in: 200 -> 270 em mes de alta")
    void altaTemporada_aplicaSomenteQuandoAtivada() {
        LocalDate natal = LocalDate.of(2026, 12, 25);
        ContextoTarifacao ctx = new ContextoTarifacao(
            200.0, natal, natal.plusDays(1), null, 0);

        // sem ativar: mantem valor base
        assertEquals(200.0, gerenciador.calcular(ctx), 0.01);

        // ativando: aplica +35% (dezembro esta em alta)
        gerenciador.setAltaTemporadaAtiva(true);
        assertEquals(270.0, gerenciador.calcular(ctx), 0.01);
    }

    @Test
    @DisplayName("Fora de alta/baixa temporada e sem promocoes, retorna o valor base")
    void mesNeutro_retornaValorBase() {
        LocalDate junho = LocalDate.of(2027, 6, 10);
        ContextoTarifacao ctx = new ContextoTarifacao(
            150.0, junho, junho.plusDays(2), null, 0);
        double valor = gerenciador.calcular(ctx);

        assertEquals(150.0, valor, 0.01);
    }

    @Test
    @DisplayName("Cliente frequente recebe desconto quando decorator esta ativo")
    void clienteFrequente_recebeDesconto() {
        gerenciador.setDescontoFrequenteAtivo(true);

        LocalDate junho = LocalDate.of(2027, 6, 10);
        ContextoTarifacao ctxOcasional = new ContextoTarifacao(
            100.0, junho, junho.plusDays(1), null, 0);
        ContextoTarifacao ctxFrequente = new ContextoTarifacao(
            100.0, junho, junho.plusDays(1), null, 20);

        double ocasional = gerenciador.calcular(ctxOcasional);
        double frequente = gerenciador.calcular(ctxFrequente);

        assertTrue(frequente < ocasional,
            "cliente frequente deveria pagar menos que ocasional");
    }

    @Test
    @DisplayName("Promocao vigente aplica desconto percentual sobre a diaria")
    void promocao_vigenteAplicaDesconto() {
        LocalDate hoje = LocalDate.now();
        gerenciador.adicionarPromocao("Teste", 0.30, hoje.minusDays(1), hoje.plusDays(10));

        LocalDate junho = LocalDate.of(2027, 6, 10);
        ContextoTarifacao ctx = new ContextoTarifacao(
            100.0, junho, junho.plusDays(1), null, 0);
        double valor = gerenciador.calcular(ctx);

        // mes neutro (100.0) x promocao 30% = 70.0
        assertEquals(70.0, valor, 0.01);
    }

    @Test
    @DisplayName("resetar limpa feriados e restaura regras padrao")
    void resetar_limpaEstado() {
        gerenciador.adicionarFeriado(LocalDate.of(2026, 12, 25));
        assertEquals(1, gerenciador.getFeriados().size());

        gerenciador.resetar();
        assertTrue(gerenciador.getFeriados().isEmpty());
    }

    @Test
    @DisplayName("montarRegra encadeia decorators e descricao reflete a cadeia")
    void montarRegra_descricaoDecoradaContemBase() {
        RegraTarifa regra = gerenciador.montarRegra();
        assertNotNull(regra);
        assertTrue(regra.descricao().contains("Tarifa base"),
            "descricao deveria comecar pela tarifa base: " + regra.descricao());
    }
}