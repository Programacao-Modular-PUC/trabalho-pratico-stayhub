package com.puc.stayhub.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton responsavel pelo log centralizado da aplicacao.
 *
 * Justificativa do Singleton: o historico de log e um recurso compartilhado
 * por toda a aplicacao; multiplas instancias fragmentariam o rastro de
 * auditoria e depuracao.
 */
public class LogService {

    public enum Nivel { INFO, WARN, ERROR }

    public static final class Entrada {
        private final LocalDateTime timestamp;
        private final Nivel nivel;
        private final String origem;
        private final String mensagem;

        public Entrada(Nivel nivel, String origem, String mensagem) {
            this.timestamp = LocalDateTime.now();
            this.nivel = nivel;
            this.origem = origem;
            this.mensagem = mensagem;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public Nivel getNivel() { return nivel; }
        public String getOrigem() { return origem; }
        public String getMensagem() { return mensagem; }

        @Override
        public String toString() {
            return String.format("[%s] %s %s - %s",
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                nivel, origem, mensagem);
        }
    }

    private static volatile LogService instancia;
    private final List<Entrada> entradas = Collections.synchronizedList(new ArrayList<>());

    private LogService() {}

    public static LogService getInstancia() {
        if (instancia == null) {
            synchronized (LogService.class) {
                if (instancia == null) instancia = new LogService();
            }
        }
        return instancia;
    }

    public void info(String origem, String mensagem) {
        registrar(Nivel.INFO, origem, mensagem);
    }

    public void warn(String origem, String mensagem) {
        registrar(Nivel.WARN, origem, mensagem);
    }

    public void error(String origem, String mensagem) {
        registrar(Nivel.ERROR, origem, mensagem);
    }

    private void registrar(Nivel nivel, String origem, String mensagem) {
        Entrada e = new Entrada(nivel, origem, mensagem);
        entradas.add(e);
        System.out.println(e);
    }

    public List<Entrada> getEntradas() {
        return List.copyOf(entradas);
    }

    public void limpar() {
        entradas.clear();
    }
}