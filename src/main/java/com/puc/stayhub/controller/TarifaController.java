package com.puc.stayhub.controller;

import com.puc.stayhub.tarifa.ContextoTarifacao;
import com.puc.stayhub.tarifa.GerenciadorTarifas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Endpoints para gerenciar as regras dinamicas de tarifacao (feriados,
 * promocoes) e simular o calculo do valor da diaria. Todos delegam ao
 * Singleton {@link GerenciadorTarifas}.
 */
@RestController
@RequestMapping("/tarifas")
@CrossOrigin(origins = "*")
public class TarifaController {

    public static class FeriadoRequest {
        public LocalDate data;
    }

    public static class PromocaoRequest {
        public String nome;
        public double percentual;
        public LocalDate inicio;
        public LocalDate fim;
    }

    public static class SimulacaoRequest {
        public double valorBase;
        public LocalDate dataInicio;
        public LocalDate dataFim;
        public int qtdReservasCliente;
    }

    @PostMapping("/feriados")
    public ResponseEntity<Set<LocalDate>> adicionarFeriado(@RequestBody FeriadoRequest req) {
        GerenciadorTarifas.getInstancia().adicionarFeriado(req.data);
        return ResponseEntity.ok(GerenciadorTarifas.getInstancia().getFeriados());
    }

    @DeleteMapping("/feriados")
    public ResponseEntity<Set<LocalDate>> removerFeriado(@RequestBody FeriadoRequest req) {
        GerenciadorTarifas.getInstancia().removerFeriado(req.data);
        return ResponseEntity.ok(GerenciadorTarifas.getInstancia().getFeriados());
    }

    @GetMapping("/feriados")
    public ResponseEntity<Set<LocalDate>> listarFeriados() {
        return ResponseEntity.ok(GerenciadorTarifas.getInstancia().getFeriados());
    }

    @PostMapping("/promocoes")
    public ResponseEntity<Map<String, Object>> adicionarPromocao(@RequestBody PromocaoRequest req) {
        GerenciadorTarifas.getInstancia().adicionarPromocao(
            req.nome, req.percentual, req.inicio, req.fim);
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("nome", req.nome);
        resp.put("percentual", req.percentual);
        resp.put("inicio", req.inicio);
        resp.put("fim", req.fim);
        resp.put("status", "promocao adicionada");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/simular")
    public ResponseEntity<Map<String, Object>> simular(@RequestBody SimulacaoRequest req) {
        ContextoTarifacao ctx = new ContextoTarifacao(
            req.valorBase, req.dataInicio, req.dataFim, null, req.qtdReservasCliente);
        double valor = GerenciadorTarifas.getInstancia().calcular(ctx);
        String descricao = GerenciadorTarifas.getInstancia().montarRegra().descricao();
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("valorBase", req.valorBase);
        resp.put("valorFinal", valor);
        resp.put("regrasAplicadas", descricao);
        return ResponseEntity.ok(resp);
    }
}