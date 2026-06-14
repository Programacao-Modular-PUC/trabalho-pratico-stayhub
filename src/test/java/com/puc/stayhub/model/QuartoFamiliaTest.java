package com.puc.stayhub.model;

import com.puc.stayhub.exception.CapacidadeExcedidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuartoFamilia - regras de diaria, capacidade e descontos")
class QuartoFamiliaTest {

    private QuartoFamilia criar(int s, int c, int qk, int ambientes, double base) {
        QuartoFamilia q = new QuartoFamilia(s, c, qk, ambientes);
        q.setValorBase(base);
        return q;
    }

    @Test
    @DisplayName("Capacidade soma camas (solteiro=1, casal/queenKing=2)")
    void capacidade() {
        // 2 solteiro + 1 casal + 1 queen/king = 2 + 2 + 2 = 6
        assertEquals(6, criar(2, 1, 1, 2, 300.0).getCapacidadeMaxima());
    }

    @Test
    @DisplayName("Numero de hospedes acima da capacidade lanca CapacidadeExcedidaException")
    void excederCapacidade() {
        QuartoFamilia q = criar(2, 0, 0, 1, 300.0);
        CapacidadeExcedidaException ex = assertThrows(
            CapacidadeExcedidaException.class,
            () -> q.calcularDiaria(5, false)
        );
        assertTrue(ex.getMessage().contains("excede"));
    }

    @Test
    @DisplayName("Numero de hospedes zero ou negativo lanca IllegalArgumentException")
    void hospedesInvalidos() {
        QuartoFamilia q = criar(2, 0, 0, 1, 300.0);
        assertThrows(IllegalArgumentException.class, () -> q.calcularDiaria(0, false));
    }

    @Test
    @DisplayName("Calculo de diaria para grupo de 2 hospedes (sem desconto)")
    void diariaSemDesconto() {
        QuartoFamilia q = criar(2, 1, 0, 1, 300.0);
        // 300 * (1 + 0.08*2) = 300 * 1.16 = 348
        // desconto 0% (1 a 2 hospedes)
        assertEquals(348.0, q.calcularDiaria(2, false), 0.001);
    }

    @Test
    @DisplayName("Calculo de diaria para grupo de 3 (desconto 5%)")
    void diariaDescontoPequeno() {
        QuartoFamilia q = criar(3, 0, 0, 1, 300.0);
        // 300 * 1.24 * (1 - 0.05) = 372 * 0.95 = 353.4
        assertEquals(353.4, q.calcularDiaria(3, false), 0.001);
    }

    @Test
    @DisplayName("Calculo de diaria para grupo grande (desconto 15%)")
    void diariaDescontoGrande() {
        QuartoFamilia q = criar(4, 1, 0, 2, 300.0);
        // 6 hospedes: 300 * (1 + 0.08*6) = 300 * 1.48 = 444
        // desconto 15%: 444 * 0.85 = 377.4
        assertEquals(377.4, q.calcularDiaria(6, false), 0.001);
    }

    @Test
    @DisplayName("Tabela de descontos progressivos por tamanho do grupo")
    void tabelaDescontos() {
        assertEquals(0.0,  QuartoFamilia.percentualDescontoGrupo(1), 0.0001);
        assertEquals(0.0,  QuartoFamilia.percentualDescontoGrupo(2), 0.0001);
        assertEquals(0.05, QuartoFamilia.percentualDescontoGrupo(3), 0.0001);
        assertEquals(0.10, QuartoFamilia.percentualDescontoGrupo(4), 0.0001);
        assertEquals(0.10, QuartoFamilia.percentualDescontoGrupo(5), 0.0001);
        assertEquals(0.15, QuartoFamilia.percentualDescontoGrupo(6), 0.0001);
        assertEquals(0.15, QuartoFamilia.percentualDescontoGrupo(10), 0.0001);
    }
}