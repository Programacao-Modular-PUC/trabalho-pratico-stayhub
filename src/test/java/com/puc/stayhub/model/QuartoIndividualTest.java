package com.puc.stayhub.model;

import com.puc.stayhub.exception.RecursoNaoPermitidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuartoIndividual - regras de diaria, capacidade e berco")
class QuartoIndividualTest {

    private QuartoIndividual criar(int camas, double base, boolean ar, boolean hidro) {
        QuartoIndividual q = new QuartoIndividual(camas);
        q.setValorBase(base);
        q.setPossuiAR(ar);
        q.setPossuiHidro(hidro);
        return q;
    }

    @Test
    @DisplayName("Capacidade maxima e igual ao numero de camas de solteiro")
    void capacidade() {
        assertEquals(1, criar(1, 100.0, false, false).getCapacidadeMaxima());
        assertEquals(3, criar(3, 100.0, false, false).getCapacidadeMaxima());
    }

    @Test
    @DisplayName("Diaria com 1 cama retorna apenas o valor base")
    void diariaUmaCama() {
        QuartoIndividual q = criar(1, 100.0, false, false);
        assertEquals(100.0, q.calcularDiaria(1, false), 0.001);
    }

    @Test
    @DisplayName("Diaria com camas extras cobra adicional por cama a partir da 2a")
    void diariaCamasExtras() {
        QuartoIndividual q = criar(3, 100.0, false, false);
        // 100 + 2 * 25 = 150
        assertEquals(150.0, q.calcularDiaria(2, false), 0.001);
    }

    @Test
    @DisplayName("Diaria soma adicional de AR e Hidro")
    void diariaComAdicionais() {
        QuartoIndividual q = criar(2, 100.0, true, true);
        // 100 + 25 (1 cama extra) + 30 (AR) + 50 (Hidro) = 205
        assertEquals(205.0, q.calcularDiaria(2, false), 0.001);
    }

    @Test
    @DisplayName("Berco em quarto individual lanca RecursoNaoPermitidoException")
    void bercoProibido() {
        QuartoIndividual q = criar(1, 100.0, false, false);
        RecursoNaoPermitidoException ex = assertThrows(
            RecursoNaoPermitidoException.class,
            () -> q.calcularDiaria(1, true)
        );
        assertTrue(ex.getMessage().contains("Individual"));
    }

    @Test
    @DisplayName("Tipo retornado e INDIVIDUAL")
    void tipo() {
        assertEquals(TipoQuarto.INDIVIDUAL, criar(1, 100.0, false, false).getTipo());
    }
}