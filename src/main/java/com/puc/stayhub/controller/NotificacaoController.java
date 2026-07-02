package com.puc.stayhub.controller;

import com.puc.stayhub.log.LogService;
import com.puc.stayhub.notificacao.CentralNotificacoes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de leitura do historico da CentralNotificacoes e do LogService.
 * Ambos delegam aos respectivos Singletons e sao persistidos em disco
 * (data/events.jsonl e data/logs.jsonl), portanto sobrevivem a reinicios.
 */
@RestController
@RequestMapping("/notificacoes")
@CrossOrigin(origins = "*")
public class NotificacaoController {

    @GetMapping("/historico")
    public ResponseEntity<List<CentralNotificacoes.EventoSnapshot>> historico() {
        return ResponseEntity.ok(CentralNotificacoes.getInstancia().getHistoricoEventos());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<LogService.Entrada>> logs() {
        return ResponseEntity.ok(LogService.getInstancia().getEntradas());
    }
}