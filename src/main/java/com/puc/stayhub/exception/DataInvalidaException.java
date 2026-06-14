package com.puc.stayhub.exception; 
/** 
* Lancada quando as datas informadas para o aluguel sao invalidas 
* (datas nulas, data fim anterior ou igual a data inicio, etc.).
*/ 
public class DataInvalidaException extends RuntimeException {    
    public DataInvalidaException(String message) {        
        super(message);    
    } 
}