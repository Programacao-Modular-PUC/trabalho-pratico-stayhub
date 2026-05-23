package com.puc.stayhub.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;

/**
 * Quarto Familia.
 * <ul>
 *     <li>Capacidade maior, com mistura de camas (solteiro, casal, queen/king).</li>
 *     <li>Capacidade calculada a partir da configuracao das camas.</li>
 *     <li>Possui ambientes distintos (estudo, home office, etc.).</li>
 *     <li>Calculo de diaria por numero de hospedes (nao por camas).</li>
 *     <li>Desconto progressivo para grupos maiores.</li>
 * </ul>
 *
 * <p><b>Regra de calculo</b>: o quarto familia comeca com o valor base e
 * acrescenta um percentual de 8% por hospede. Em seguida e aplicado um
 * desconto progressivo proporcional ao tamanho do grupo (ver
 * {@link #percentualDescontoGrupo(int)}), de modo a tornar o quarto familia
 * mais vantajoso que o aluguel de varios quartos individuais.
 */
@Entity
@DiscriminatorValue("FAMILIA")
public class QuartoFamilia extends Quarto {

    /** Acrescimo percentual aplicado por hospede sobre o valor base. */
    public static final double ACRESCIMO_POR_HOSPEDE = 0.08;

    @Min(0)
    private int camasSolteiro;

    @Min(0)
    private int camasCasal;

    @Min(0)
    private int camasQueenKing;

    @Min(value = 1, message = "Quarto familia deve ter ao menos 1 ambiente")
    private int quantidadeAmbientes;

    public QuartoFamilia() {}

    public QuartoFamilia(int camasSolteiro, int camasCasal, int camasQueenKing, int quantidadeAmbientes) {
        this.camasSolteiro = camasSolteiro;
        this.camasCasal = camasCasal;
        this.camasQueenKing = camasQueenKing;
        this.quantidadeAmbientes = quantidadeAmbientes;
    }

    @Override
    public TipoQuarto getTipo() {
        return TipoQuarto.FAMILIA;
    }

    @Override
    public int getCapacidadeMaxima() {
        // Cada cama de solteiro acomoda 1 pessoa; casal/queen/king acomodam 2.
        return camasSolteiro + (camasCasal * 2) + (camasQueenKing * 2);
    }

    @Override
    public double calcularDiaria(int numHospedes, boolean solicitouBerco) {
        if (numHospedes <= 0) {
            throw new IllegalArgumentException("Numero de hospedes deve ser positivo");
        }
        if (numHospedes > getCapacidadeMaxima()) {
            throw new IllegalArgumentException(
                "Numero de hospedes (" + numHospedes + ") excede a capacidade do quarto familia ("
                + getCapacidadeMaxima() + ")");
        }

        // 1. Acrescimo proporcional ao numero de hospedes
        double base = getValorBase() * (1 + ACRESCIMO_POR_HOSPEDE * numHospedes);

        // 2. Desconto progressivo para grupos
        double desconto = percentualDescontoGrupo(numHospedes);
        base = base * (1 - desconto);

        // 3. Adicionais comuns (AR / Hidro)
        return aplicarAdicionaisComuns(base);
    }

    /**
     * Percentual de desconto aplicado conforme o tamanho do grupo.
     * <ul>
     *     <li>1 a 2 hospedes: 0%</li>
     *     <li>3 hospedes: 5%</li>
     *     <li>4 a 5 hospedes: 10%</li>
     *     <li>6 ou mais hospedes: 15%</li>
     * </ul>
     */
    public static double percentualDescontoGrupo(int numHospedes) {
        if (numHospedes >= 6) return 0.15;
        if (numHospedes >= 4) return 0.10;
        if (numHospedes >= 3) return 0.05;
        return 0.0;
    }

    public int getCamasSolteiro() { return camasSolteiro; }
    public void setCamasSolteiro(int camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public int getCamasCasal() { return camasCasal; }
    public void setCamasCasal(int camasCasal) { this.camasCasal = camasCasal; }

    public int getCamasQueenKing() { return camasQueenKing; }
    public void setCamasQueenKing(int camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public int getQuantidadeAmbientes() { return quantidadeAmbientes; }
    public void setQuantidadeAmbientes(int quantidadeAmbientes) { this.quantidadeAmbientes = quantidadeAmbientes; }
}
