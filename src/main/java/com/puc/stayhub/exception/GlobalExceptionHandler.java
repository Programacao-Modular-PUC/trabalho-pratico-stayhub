package com.puc.stayhub.exception; 

import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.MethodArgumentNotValidException; 
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice; 
import org.springframework.web.server.ResponseStatusException; 

import java.time.LocalDateTime; 
import java.time.format.DateTimeParseException; 
import java.util.LinkedHashMap; 
import java.util.List; 
import java.util.Map; 

@RestControllerAdvice 
    public class GlobalExceptionHandler {    
        
        @ExceptionHandler(QuartoIndisponivelException.class)    
        public ResponseEntity<Map<String, Object>> 
        handleQuartoIndisponivel(QuartoIndisponivelException ex) {        
            return buildResponse(HttpStatus.CONFLICT, ex.getMessage());    
        }    
        
        @ExceptionHandler(CapacidadeExcedidaException.class)    
        public ResponseEntity<Map<String, Object>> 
        handleCapacidadeExcedida(CapacidadeExcedidaException ex) {        
            return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());    
        }    
        
        @ExceptionHandler(DataInvalidaException.class)    
        public ResponseEntity<Map<String, Object>> 
        handleDataInvalida(DataInvalidaException ex) {        
            return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());    
        }   
        
        @ExceptionHandler(RecursoNaoPermitidoException.class)    
        public ResponseEntity<Map<String, Object>> 
        handleRecursoNaoPermitido(RecursoNaoPermitidoException ex) {        
            return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());    
        }    
        
        @ExceptionHandler(ResponseStatusException.class)   
        public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {        
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());        
            return buildResponse(status, ex.getReason());    
        }   
        
        @ExceptionHandler(MethodArgumentNotValidException.class)    
        public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {        
            List<String> erros = ex.getBindingResult().getFieldErrors().stream()            
                .map(e -> e.getField() + ": " + e.getDefaultMessage())            
                .toList();        
            return buildResponse(HttpStatus.BAD_REQUEST, String.join("; ", erros));    
        }   
        
        @ExceptionHandler(IllegalArgumentException.class)    
        public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {        
            return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());    
        }   
        
        @ExceptionHandler(DateTimeParseException.class)    
        public ResponseEntity<Map<String, Object>> handleDateParse(DateTimeParseException ex) {        
            return buildResponse(HttpStatus.BAD_REQUEST,            "Formato de data invalido. Use o padrao yyyy-MM-dd. Detalhe: " + ex.getMessage());    
        }   
        
        @ExceptionHandler(NullPointerException.class)    
        public ResponseEntity<Map<String, Object>> handleNullPointer(NullPointerException ex) {        
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,            
                                 "Erro interno: campo obrigatorio nao informado. Detalhe: " + ex.getMessage());    
        }    
        
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGenerico(Exception ex) {        
            return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,            "Erro inesperado: " + ex.getMessage());   
        }    
        
        private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
            Map<String, Object> body = new LinkedHashMap<>();        
            body.put("timestamp", LocalDateTime.now().toString());        
            body.put("status", status.value());        
            body.put("error", status.getReasonPhrase());        
            body.put("message", message);        
            return ResponseEntity.status(status).body(body);    
            }
        }        
