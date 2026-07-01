package com.puc.stayhub.notificacao;

import java.util.EnumSet;
import java.util.Set;

public class NotificadorCliente implements ObservadorReserva {
    private final CanalNotificacao canal;
    private final Set<TipoEventoReserva> eventosAssinados;

    public NotificadorCliente(CanalNotificacao canal) {
        this(canal, EnumSet.allOf(TipoEventoReserva.class));
    }

    public NotificadorCliente(CanalNotificacao canal, Set<TipoEventoReserva> eventosAssinados) {
        this.canal = canal;
        this.eventosAssinados = eventosAssinados;
    }

    @Override
    public void notificar(EventoReserva evento) {
        if (!interessadoEm(evento.getTipo()))
            return;
        var cliente = evento.getAluguel().getCliente();
        String destino = cliente.getEmail() != null ? cliente.getEmail() : cliente.getNome();
        String assunto = "StayHub - " + evento.getTipo().name();
        String msg = String.format("Ola %s! %s (Aluguel #%d)", cliente.getNome(), evento.getDescricao(),
                evento.getAluguel().getId());
        canal.enviar(destino, assunto, msg);
    }

    @Override
    public boolean interessadoEm(TipoEventoReserva tipo) {
        return eventosAssinados.contains(tipo);
    }

    public CanalNotificacao getCanal() {
        return canal;
    }
}