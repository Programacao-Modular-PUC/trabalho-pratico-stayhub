package com.puc.stayhub.notificacao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * * Singleton que atua como barramento central de eventos (Subject do
 * Observer). * * Justificativa do Singleton: existe um unico canal logico de
 * comunicacao entre * quem produz eventos (services) e quem os consome
 * (notificadores). Ter mais de * uma instancia dividiria os observadores e
 * faria com que eventos fossem perdidos.
 */
public class CentralNotificacoes {
    private static volatile CentralNotificacoes instancia;
    private final List<ObservadorReserva> observadores = Collections.synchronizedList(new ArrayList<>());
    private final List<EventoReserva> historicoEventos = Collections.synchronizedList(new ArrayList<>());

    private CentralNotificacoes() {
    }

    public static CentralNotificacoes getInstancia() {
        if (instancia == null) {
            synchronized (CentralNotificacoes.class) {
                if (instancia == null)
                    instancia = new CentralNotificacoes();
            }
        }
        return instancia;
    }

    public void inscrever(ObservadorReserva obs) {
        observadores.add(obs);
    }

    public void desinscrever(ObservadorReserva obs) {
        observadores.remove(obs);
    }

    public List<ObservadorReserva> getObservadores() {
        return List.copyOf(observadores);
    }

    public List<EventoReserva> getHistoricoEventos() {
        return List.copyOf(historicoEventos);
    }

    public void publicar(EventoReserva evento) {
        historicoEventos.add(evento);
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
    }
}