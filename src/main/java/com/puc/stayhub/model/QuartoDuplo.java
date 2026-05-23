package com.puc.stayhub.model;

import jakarta.persistence.*;

/**
 * Quarto Duplo (Casal).
 * <ul>
 *     <li>Voltado para casais.</li>
 *     <li>Possui uma cama de casal nas variantes CASAL, QUEEN ou KING.</li>
 *     <li>Pode ou nao oferecer berco; quando oferecido o cliente decide se quer usar.</li>
 *     <li>Berco solicitado aplica taxa adicional sobre a diaria.</li>
 *     <li>O conforto da cama (Casal/Queen/King) tambem adiciona valor.</li>
 * </ul>
 */
@Entity
@DiscriminatorValue("DUPLO")
public class QuartoDuplo extends Quarto {

    /** Taxa adicional cobrada quando o cliente solicita berco. */
    public static final double TAXA_BERCO = 35.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cama_casal", length = 20)
    private TipoCamaCasal tipoCamaCasal;

    /** Indica se o quarto possui berco disponivel para solicitacao. */
    @Column(name = "possui_berco_disponivel")
    private boolean possuiBercoDisponivel;

    public QuartoDuplo() {}

    public QuartoDuplo(TipoCamaCasal tipoCamaCasal, boolean possuiBercoDisponivel) {
        this.tipoCamaCasal = tipoCamaCasal;
        this.possuiBercoDisponivel = possuiBercoDisponivel;
    }

    @Override
    public TipoQuarto getTipo() {
        return TipoQuarto.DUPLO;
    }

    @Override
    public int getCapacidadeMaxima() {
        // Casal + bebe (quando ha berco disponivel)
        return possuiBercoDisponivel ? 3 : 2;
    }

    @Override
    public double calcularDiaria(int numHospedes, boolean solicitouBerco) {
        if (solicitouBerco && !possuiBercoDisponivel) {
            throw new IllegalArgumentException("Este quarto duplo nao possui berco disponivel");
        }
        double base = getValorBase();
        if (tipoCamaCasal != null) {
            base += tipoCamaCasal.getAdicionalConforto();
        }
        if (solicitouBerco) {
            base += TAXA_BERCO;
        }
        return aplicarAdicionaisComuns(base);
    }

    public TipoCamaCasal getTipoCamaCasal() { return tipoCamaCasal; }
    public void setTipoCamaCasal(TipoCamaCasal tipoCamaCasal) { this.tipoCamaCasal = tipoCamaCasal; }

    public boolean isPossuiBercoDisponivel() { return possuiBercoDisponivel; }
    public void setPossuiBercoDisponivel(boolean possuiBercoDisponivel) { this.possuiBercoDisponivel = possuiBercoDisponivel; }
}
