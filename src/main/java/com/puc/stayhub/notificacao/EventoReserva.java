package com.puc.stayhub.notificacao;

import java.time.LocalDateTime;

import com.puc.stayhub.model.Aluguel;

public class EventoReserva {
    private final TipoEventoReserva tipo;
    private final Aluguel aluguel;
    private final LocalDateTime timestamp;
    private final String descricao;

    public EventoReserva(TipoEventoReserva tipo, Aluguel aluguel, String descricao) {
        this.tipo = tipo;
        this.aluguel = aluguel;
        this.descricao = descricao;
        this.timestamp = LocalDateTime.now();
    }

    public TipoEventoReserva getTipo() {
        return tipo;
    }

    public Aluguel getAluguel() {
        return aluguel;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescricao() {
        return descricao;
    }
}