package com.puc.stayhub.notificacao;

public interface ObservadorReserva {
    void notificar(EventoReserva evento);

    boolean interessadoEm(TipoEventoReserva tipo);
}