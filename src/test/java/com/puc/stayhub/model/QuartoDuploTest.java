package com.puc.stayhub.model;

import com.puc.stayhub.exception.RecursoNaoPermitidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuartoDuplo - regras de diaria, capacidade e berco")
class QuartoDuploTest {

    private QuartoDuplo criar(TipoCamaCasal tipo, boolean berco, double base) {
        QuartoDuplo q = new QuartoDuplo(tipo, berco);
        q.setValorBase(base);
        return q;
    }

    @Test
    @DisplayName("Capacidade e 2 sem berco e 3 com berco disponivel")
    void capacidade() {
        assertEquals(2, criar(TipoCamaCasal.CASAL, false, 200.0).getCapacidadeMaxima());
        assertEquals(3, criar(TipoCamaCasal.CASAL, true, 200.0).getCapacidadeMaxima());
    }

    @Test
    @DisplayName("Diaria base + adicional de conforto pela cama escolhida")
    void diariaConforto() {
        // Base 200 + adicional CASAL (40) = 240
        assertEquals(240.0, criar(TipoCamaCasal.CASAL, false, 200.0).calcularDiaria(2, false), 0.001);
        // Base 200 + adicional QUEEN (80) = 280
        assertEquals(280.0, criar(TipoCamaCasal.QUEEN, false, 200.0).calcularDiaria(2, false), 0.001);
        // Base 200 + adicional KING (120) = 320
        assertEquals(320.0, criar(TipoCamaCasal.KING, false, 200.0).calcularDiaria(2, false), 0.001);
    }

    @Test
    @DisplayName("Quando ha berco disponivel e e solicitado, aplica TAXA_BERCO")
    void diariaComBerco() {
        QuartoDuplo q = criar(TipoCamaCasal.CASAL, true, 200.0);
        // 200 + 40 (casal) + 35 (berco) = 275
        assertEquals(275.0, q.calcularDiaria(3, true), 0.001);
    }

    @Test
    @DisplayName("Solicitar berco em quarto que nao oferece lanca RecursoNaoPermitidoException")
    void bercoIndisponivel() {
        QuartoDuplo q = criar(TipoCamaCasal.CASAL, false, 200.0);
        assertThrows(RecursoNaoPermitidoException.class, () -> q.calcularDiaria(2, true));
    }

    @Test
    @DisplayName("Diaria considera adicional de AR e Hidro")
    void diariaComAdicionais() {
        QuartoDuplo q = criar(TipoCamaCasal.CASAL, false, 200.0);
        q.setPossuiAR(true);
        q.setPossuiHidro(true);
        // 200 + 40 + 30 + 50 = 320
        assertEquals(320.0, q.calcularDiaria(2, false), 0.001);
    }
}