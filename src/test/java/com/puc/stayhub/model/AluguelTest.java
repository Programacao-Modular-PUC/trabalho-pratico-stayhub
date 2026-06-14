package com.puc.stayhub.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Aluguel - calculo de diarias, valor total e ciclo de vida")
class AluguelTest {

    private QuartoIndividual quartoBasico() {
        QuartoIndividual q = new QuartoIndividual(1);
        q.setValorBase(150.0);
        return q;
    }

    @Test
    @DisplayName("Quantidade de diarias e calculada pela diferenca em dias")
    void quantidadeDiarias() {
        Aluguel a = new Aluguel();
        a.setDataInicio(LocalDate.of(2026, 6, 10));
        a.setDataFim(LocalDate.of(2026, 6, 15));
        assertEquals(5, a.calcularQuantidadeDiarias());
    }

    @Test
    @DisplayName("Aluguel de mesmo dia conta como 1 diaria minima")
    void diariaMinima() {
        Aluguel a = new Aluguel();
        a.setDataInicio(LocalDate.of(2026, 6, 10));
        a.setDataFim(LocalDate.of(2026, 6, 10));
        assertEquals(1, a.calcularQuantidadeDiarias());
    }

    @Test
    @DisplayName("Datas nulas resultam em zero diarias")
    void diariasComDatasNulas() {
        Aluguel a = new Aluguel();
        assertEquals(0, a.calcularQuantidadeDiarias());
    }

    @Test
    @DisplayName("recalcularValores produz diaria e total corretos")
    void recalcularValores() {
        Aluguel a = new Aluguel();
        a.setQuarto(quartoBasico());
        a.setNumHospedes(1);
        a.setSolicitouBerco(false);
        a.setDataInicio(LocalDate.of(2026, 6, 10));
        a.setDataFim(LocalDate.of(2026, 6, 13));
        a.recalcularValores();
        assertEquals(150.0, a.getValorDiaria(), 0.001);
        assertEquals(450.0, a.getValorTotal(), 0.001);
    }

    @Test
    @DisplayName("Aluguel comeca como ATIVO e pode ser cancelado")
    void cicloDeVida() {
        Aluguel a = new Aluguel();
        assertEquals(StatusAluguel.ATIVO, a.getStatus());
        assertTrue(a.isAtivo());
        a.cancelar();
        assertEquals(StatusAluguel.CANCELADO, a.getStatus());
        assertFalse(a.isAtivo());
    }
}