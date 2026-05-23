package com.puc.stayhub.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;

/**
 * Quarto Individual.
 * <ul>
 *     <li>Pode ter 1 ou mais camas de solteiro.</li>
 *     <li>Nao permite berco.</li>
 *     <li>Diaria: valor base + adicional por cama (a partir da 2a cama).</li>
 *     <li>Capacidade proporcional ao numero de camas.</li>
 * </ul>
 */
@Entity
@DiscriminatorValue("INDIVIDUAL")
public class QuartoIndividual extends Quarto {

    /** Valor cobrado por cada cama adicional (a partir da 2a). */
    public static final double ADICIONAL_POR_CAMA = 25.0;

    @Min(value = 1, message = "Quarto individual deve ter ao menos 1 cama de solteiro")
    private int camasSolteiro;

    public QuartoIndividual() {}

    public QuartoIndividual(int camasSolteiro) {
        this.camasSolteiro = camasSolteiro;
    }

    @Override
    public TipoQuarto getTipo() {
        return TipoQuarto.INDIVIDUAL;
    }

    @Override
    public int getCapacidadeMaxima() {
        return camasSolteiro;
    }

    @Override
    public double calcularDiaria(int numHospedes, boolean solicitouBerco) {
        if (solicitouBerco) {
            throw new IllegalArgumentException("Quarto Individual nao permite berco");
        }
        double base = getValorBase();
        if (camasSolteiro > 1) {
            base += (camasSolteiro - 1) * ADICIONAL_POR_CAMA;
        }
        return aplicarAdicionaisComuns(base);
    }

    public int getCamasSolteiro() { return camasSolteiro; }
    public void setCamasSolteiro(int camasSolteiro) { this.camasSolteiro = camasSolteiro; }
}
