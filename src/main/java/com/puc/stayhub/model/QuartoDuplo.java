package com.puc.stayhub.model; 
import com.puc.stayhub.exception.RecursoNaoPermitidoException; 
import jakarta.persistence.*; 

@Entity 
@DiscriminatorValue("DUPLO") 
public class QuartoDuplo extends Quarto {   
    public static final double TAXA_BERCO = 35.0;   
    
    @Enumerated(EnumType.STRING)    
    @Column(name = "tipo_cama_casal", length = 20)    
    private TipoCamaCasal tipoCamaCasal;    
    
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
        return possuiBercoDisponivel ? 3 : 2;    
    }    
    
    @Override    
    public double calcularDiaria(int numHospedes, boolean solicitouBerco) {        
        if (solicitouBerco && !possuiBercoDisponivel) {            
            throw new RecursoNaoPermitidoException(                
                "Este quarto duplo nao possui berco disponivel");        
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
    public void setPossuiBercoDisponivel(boolean possuiBercoDisponivel) {        
        this.possuiBercoDisponivel = possuiBercoDisponivel;    
    } 
}
