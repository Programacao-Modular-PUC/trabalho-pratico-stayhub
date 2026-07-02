package com.puc.stayhub.notificacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.puc.stayhub.model.Aluguel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton que atua como barramento central de eventos (Subject do Observer).
 *
 * Persistencia: cada evento publicado e serializado como snapshot em
 * data/events.jsonl, garantindo que o historico de notificacoes sobreviva a
 * reinicios da aplicacao. Snapshots sao usados (em vez das entidades JPA
 * originais) para evitar problemas de lazy loading e referencias ciclicas.
 *
 * Justificativa do Singleton: existe um unico canal logico de comunicacao
 * entre quem produz eventos (services) e quem os consome (notificadores).
 * Ter mais de uma instancia dividiria os observadores e faria com que
 * eventos fossem perdidos.
 */
public class CentralNotificacoes {

    public static class ClienteSnapshot {
        public String nome;
        public String cpf;
        public ClienteSnapshot() {}
        public ClienteSnapshot(String nome, String cpf) { this.nome = nome; this.cpf = cpf; }
    }

    public static class AluguelSnapshot {
        public Long id;
        public LocalDate dataInicio;
        public LocalDate dataFim;
        public Integer numHospedes;
        public ClienteSnapshot cliente;
        public AluguelSnapshot() {}
    }

    public static class EventoSnapshot {
        public TipoEventoReserva tipo;
        public String descricao;
        public LocalDateTime timestamp;
        public AluguelSnapshot aluguel;
        public EventoSnapshot() {}

        public static EventoSnapshot de(EventoReserva evento) {
            EventoSnapshot s = new EventoSnapshot();
            s.tipo = evento.getTipo();
            s.descricao = evento.getDescricao();
            s.timestamp = evento.getTimestamp();
            Aluguel a = evento.getAluguel();
            if (a != null) {
                s.aluguel = new AluguelSnapshot();
                s.aluguel.id = a.getId();
                s.aluguel.dataInicio = a.getDataInicio();
                s.aluguel.dataFim = a.getDataFim();
                s.aluguel.numHospedes = a.getNumHospedes();
                if (a.getCliente() != null) {
                    s.aluguel.cliente = new ClienteSnapshot(
                        a.getCliente().getNome(), a.getCliente().getCpf());
                }
            }
            return s;
        }
    }

    private static final Path ARQUIVO = Paths.get("data", "events.jsonl");
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static volatile CentralNotificacoes instancia;
    private final List<ObservadorReserva> observadores = Collections.synchronizedList(new ArrayList<>());
    private final List<EventoSnapshot> historicoEventos = Collections.synchronizedList(new ArrayList<>());

    private CentralNotificacoes() {
        carregarDoArquivo();
    }

    public static CentralNotificacoes getInstancia() {
        if (instancia == null) {
            synchronized (CentralNotificacoes.class) {
                if (instancia == null) instancia = new CentralNotificacoes();
            }
        }
        return instancia;
    }

    public void inscrever(ObservadorReserva obs)     { observadores.add(obs); }
    public void desinscrever(ObservadorReserva obs)  { observadores.remove(obs); }
    public List<ObservadorReserva> getObservadores() { return List.copyOf(observadores); }
    public List<EventoSnapshot> getHistoricoEventos() { return List.copyOf(historicoEventos); }

    public void publicar(EventoReserva evento) {
        EventoSnapshot snapshot = EventoSnapshot.de(evento);
        historicoEventos.add(snapshot);
        anexarNoArquivo(snapshot);
        synchronized (observadores) {
            for (ObservadorReserva obs : observadores) {
                try {
                    obs.notificar(evento);
                } catch (Exception e) {
                    System.err.println("Falha ao notificar observador: " + e.getMessage());
                }
            }
        }
    }

    public void limpar() {
        observadores.clear();
        historicoEventos.clear();
        try {
            Files.deleteIfExists(ARQUIVO);
        } catch (IOException ex) {
            System.err.println("CentralNotificacoes: falha ao remover arquivo: " + ex.getMessage());
        }
    }

    private void carregarDoArquivo() {
        if (!Files.exists(ARQUIVO)) return;
        try {
            for (String linha : Files.readAllLines(ARQUIVO)) {
                if (linha.isBlank()) continue;
                try {
                    historicoEventos.add(MAPPER.readValue(linha, EventoSnapshot.class));
                } catch (Exception ex) {
                    System.err.println("CentralNotificacoes: linha invalida ignorada: " + ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.err.println("CentralNotificacoes: falha ao ler arquivo: " + ex.getMessage());
        }
    }

    private void anexarNoArquivo(EventoSnapshot s) {
        try {
            Files.createDirectories(ARQUIVO.getParent());
            String json = MAPPER.writeValueAsString(s);
            try (BufferedWriter w = Files.newBufferedWriter(ARQUIVO,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(json);
                w.newLine();
            }
        } catch (IOException ex) {
            System.err.println("CentralNotificacoes: falha ao persistir: " + ex.getMessage());
        }
    }
}