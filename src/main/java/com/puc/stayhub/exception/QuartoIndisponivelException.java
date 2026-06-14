package com.puc.stayhub.exception; 
/** 
 * Lancada quando o quarto solicitado nao esta disponivel no periodo informado 
 * (existe sobreposicao com outro aluguel ativo). 
 */ 
public class QuartoIndisponivelException extends RuntimeException {    
    public QuartoIndisponivelException(String message) {        
        super(message);    
    }    
    public QuartoIndisponivelException(Long quartoId) {        
        super("Quarto indisponivel no periodo solicitado (id=" + quartoId + ")");    
    } 
}