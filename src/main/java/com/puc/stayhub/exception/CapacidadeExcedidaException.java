package com.puc.stayhub.exception; 
/** 
 * Lancada quando o numero de hospedes informado excede a capacidade 
 * maxima do quarto selecionado. 
 */ 
public class CapacidadeExcedidaException extends RuntimeException {    
    public CapacidadeExcedidaException(String message) {        
        super(message);    
    }    
    public CapacidadeExcedidaException(int numHospedes, int capacidadeMaxima) {        
        super("Numero de hospedes (" + numHospedes            + ") excede a capacidade maxima do quarto (" + capacidadeMaxima + ")");    
    } 
}