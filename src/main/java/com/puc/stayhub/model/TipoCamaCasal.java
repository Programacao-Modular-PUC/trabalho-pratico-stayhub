package com.puc.stayhub.model;

/**
 * Tipos de cama de casal disponiveis no Quarto Duplo.
 * Cada tipo possui um adicional de conforto sobre o valor base da diaria.
 */
public enum TipoCamaCasal {

    CASAL(40.0),
    QUEEN(80.0),
    KING(120.0);

    private final double adicionalConforto;

    TipoCamaCasal(double adicionalConforto) {
        this.adicionalConforto = adicionalConforto;
    }

    public double getAdicionalConforto() {
        return adicionalConforto;
    }
}
