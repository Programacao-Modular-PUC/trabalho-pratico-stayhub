package com.puc.stayhub.notificacao;

import java.util.EnumSet;
import java.util.Set;

public class NotificadorProprietario implements ObservadorReserva {
    private final CanalNotificacao canal;
    private final String emailProprietario;
    private final Set<TipoEventoReserva> eventosAssinados;

    public NotificadorProprietario(CanalNotificacao canal, String emailProprietario) {
        this(canal, emailProprietario, EnumSet.of(TipoEventoReserva.RESERVA_CRIADA, TipoEventoReserva.RESERVA_CANCELADA,
                TipoEventoReserva.PAGAMENTO_CONFIRMADO));
    }

    public NotificadorProprietario(CanalNotificacao canal, String emailProprietario,
            Set<TipoEventoReserva> eventosAssinados) {
        this.canal = canal;
        this.emailProprietario = emailProprietario;
        this.eventosAssinados = eventosAssinados;
    }

    @Override
    public void notificar(EventoReserva evento) {
        if (!interessadoEm(evento.getTipo()))
            return;
        String assunto = "[Proprietario] " + evento.getTipo().name();
        String msg = String.format("Evento %s no aluguel #%d - Cliente: %s", evento.getTipo().name(),
                evento.getAluguel().getId(), evento.getAluguel().getCliente().getNome());
        canal.enviar(emailProprietario, assunto, msg);
    }

    @Override
    public boolean interessadoEm(TipoEventoReserva tipo) {
        return eventosAssinados.contains(tipo);
    }
}