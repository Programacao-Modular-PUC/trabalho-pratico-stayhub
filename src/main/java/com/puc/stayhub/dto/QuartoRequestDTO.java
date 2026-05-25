package com.puc.stayhub.dto;

import com.puc.stayhub.model.TipoCamaCasal;
import com.puc.stayhub.model.TipoQuarto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class QuartoRequestDTO {

    @NotNull(message = "Tipo do quarto e obrigatorio")
    private TipoQuarto tipo;

    @NotNull(message = "Valor base e obrigatorio")
    @Positive(message = "Valor base deve ser positivo")
    private Double valorBase;

    private boolean possuiAR;
    private boolean possuiHidro;

    @NotNull(message = "Id da residencia e obrigatorio")
    private Long residenciaId;

    // QuartoIndividual e QuartoFamilia
    private Integer camasSolteiro;

    // QuartoDuplo
    private TipoCamaCasal tipoCamaCasal;
    private Boolean possuiBercoDisponivel;

    // QuartoFamilia
    private Integer camasCasal;
    private Integer camasQueenKing;
    private Integer quantidadeAmbientes;

    public TipoQuarto getTipo() { return tipo; }
    public void setTipo(TipoQuarto tipo) { this.tipo = tipo; }

    public Double getValorBase() { return valorBase; }
    public void setValorBase(Double valorBase) { this.valorBase = valorBase; }

    public boolean isPossuiAR() { return possuiAR; }
    public void setPossuiAR(boolean possuiAR) { this.possuiAR = possuiAR; }

    public boolean isPossuiHidro() { return possuiHidro; }
    public void setPossuiHidro(boolean possuiHidro) { this.possuiHidro = possuiHidro; }

    public Long getResidenciaId() { return residenciaId; }
    public void setResidenciaId(Long residenciaId) { this.residenciaId = residenciaId; }

    public Integer getCamasSolteiro() { return camasSolteiro; }
    public void setCamasSolteiro(Integer camasSolteiro) { this.camasSolteiro = camasSolteiro; }

    public TipoCamaCasal getTipoCamaCasal() { return tipoCamaCasal; }
    public void setTipoCamaCasal(TipoCamaCasal tipoCamaCasal) { this.tipoCamaCasal = tipoCamaCasal; }

    public Boolean getPossuiBercoDisponivel() { return possuiBercoDisponivel; }
    public void setPossuiBercoDisponivel(Boolean possuiBercoDisponivel) {
        this.possuiBercoDisponivel = possuiBercoDisponivel;
    }

    public Integer getCamasCasal() { return camasCasal; }
    public void setCamasCasal(Integer camasCasal) { this.camasCasal = camasCasal; }

    public Integer getCamasQueenKing() { return camasQueenKing; }
    public void setCamasQueenKing(Integer camasQueenKing) { this.camasQueenKing = camasQueenKing; }

    public Integer getQuantidadeAmbientes() { return quantidadeAmbientes; }
    public void setQuantidadeAmbientes(Integer quantidadeAmbientes) {
        this.quantidadeAmbientes = quantidadeAmbientes;
    }
}