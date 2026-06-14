package com.puc.stayhub.model; 

import com.puc.stayhub.exception.CapacidadeExcedidaException; 
import jakarta.persistence.DiscriminatorValue; 
import jakarta.persistence.Entity; 
import jakarta.validation.constraints.Min; 

@Entity 
@DiscriminatorValue("FAMILIA")
public class QuartoFamilia extends Quarto {    
    
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
        return camasSolteiro + (camasCasal * 2) + (camasQueenKing * 2);    
    }    
    
    @Override    
    public double calcularDiaria(int numHospedes, boolean solicitouBerco) {        
        if (numHospedes <= 0) {            
            throw new IllegalArgumentException("Numero de hospedes deve ser positivo");        
        }        
        if (numHospedes > getCapacidadeMaxima()) {           
            throw new CapacidadeExcedidaException(numHospedes, getCapacidadeMaxima());       
        }        
        double base = getValorBase() * (1 + ACRESCIMO_POR_HOSPEDE * numHospedes);        
        double desconto = percentualDescontoGrupo(numHospedes);        
        base = base * (1 - desconto);        
        return aplicarAdicionaisComuns(base);    
    }    
    
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
    public void setQuantidadeAmbientes(int quantidadeAmbientes) {        
        this.quantidadeAmbientes = quantidadeAmbientes;    
    } 
}
