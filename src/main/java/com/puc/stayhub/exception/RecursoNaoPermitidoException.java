package com.puc.stayhub.exception; 
/** 
* Lancada quando o cliente solicita um recurso nao permitido para o tipo 
* de quarto escolhido. Exemplo: berco em quarto individual ou berco em 
* quarto duplo que nao possui berco disponivel. 
*/ 
public class RecursoNaoPermitidoException extends RuntimeException {    
    public RecursoNaoPermitidoException(String message) {        
        super(message);    
    } 
}