package com.puc.stayhub.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton responsavel pelo log centralizado da aplicacao.
 *
 * Persistencia: as entradas sao gravadas em data/logs.jsonl no formato
 * JSON Lines (uma entrada por linha). Ao inicializar, o singleton relê o
 * arquivo e restaura o historico, garantindo que os logs sobrevivam a
 * reinicios da aplicacao.
 *
 * Justificativa do Singleton: o historico de log e um recurso compartilhado
 * por toda a aplicacao; multiplas instancias fragmentariam o rastro de
 * auditoria e depuracao.
 */
public class LogService {

    public enum Nivel { INFO, WARN, ERROR }

    public static final class Entrada {
        private LocalDateTime timestamp;
        private Nivel nivel;
        private String origem;
        private String mensagem;

        public Entrada() {}
        public Entrada(Nivel nivel, String origem, String mensagem) {
            this.timestamp = LocalDateTime.now();
            this.nivel = nivel;
            this.origem = origem;
            this.mensagem = mensagem;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public Nivel getNivel() { return nivel; }
        public void setNivel(Nivel nivel) { this.nivel = nivel; }
        public String getOrigem() { return origem; }
        public void setOrigem(String origem) { this.origem = origem; }
        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }

        @Override
        public String toString() {
            return String.format("[%s] %s %s - %s",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                nivel, origem, mensagem);
        }
    }

    private static final Path ARQUIVO = Paths.get("data", "logs.jsonl");
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static volatile LogService instancia;
    private final List<Entrada> entradas = Collections.synchronizedList(new ArrayList<>());

    private LogService() {
        carregarDoArquivo();
    }

    public static LogService getInstancia() {
        if (instancia == null) {
            synchronized (LogService.class) {
                if (instancia == null) instancia = new LogService();
            }
        }
        return instancia;
    }

    public void info(String origem, String mensagem)  { registrar(Nivel.INFO, origem, mensagem); }
    public void warn(String origem, String mensagem)  { registrar(Nivel.WARN, origem, mensagem); }
    public void error(String origem, String mensagem) { registrar(Nivel.ERROR, origem, mensagem); }

    private void registrar(Nivel nivel, String origem, String mensagem) {
        Entrada e = new Entrada(nivel, origem, mensagem);
        entradas.add(e);
        anexarNoArquivo(e);
        System.out.println(e);
    }

    public List<Entrada> getEntradas() {
        return List.copyOf(entradas);
    }

    public void limpar() {
        entradas.clear();
        try {
            Files.deleteIfExists(ARQUIVO);
        } catch (IOException ex) {
            System.err.println("LogService: falha ao remover arquivo: " + ex.getMessage());
        }
    }

    private void carregarDoArquivo() {
        if (!Files.exists(ARQUIVO)) return;
        try {
            for (String linha : Files.readAllLines(ARQUIVO)) {
                if (linha.isBlank()) continue;
                try {
                    entradas.add(MAPPER.readValue(linha, Entrada.class));
                } catch (Exception ex) {
                    System.err.println("LogService: linha invalida ignorada: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("LogService: falha ao ler arquivo: " + ex.getMessage());
        }
    }

    private void anexarNoArquivo(Entrada e) {
        try {
            Files.createDirectories(ARQUIVO.getParent());
            String json = MAPPER.writeValueAsString(e);
            try (BufferedWriter w = Files.newBufferedWriter(ARQUIVO,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(json);
                w.newLine();
            }
        } catch (IOException ex) {
            System.err.println("LogService: falha ao persistir: " + ex.getMessage());
        }
    }
}