package com.puc.stayhub.notificacao;

import com.puc.stayhub.model.Aluguel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CentralNotificacoes - Singleton + Observer + Strategy")
class CentralNotificacoesTest {

    private CentralNotificacoes central;

    @BeforeEach
    void setUp() {
        central = CentralNotificacoes.getInstancia();
        central.limpar();
    }

    @Test
    @DisplayName("getInstancia sempre retorna a mesma instancia (Singleton)")
    void singleton_retornaSempreMesmaInstancia() {
        CentralNotificacoes a = CentralNotificacoes.getInstancia();
        CentralNotificacoes b = CentralNotificacoes.getInstancia();
        assertSame(a, b);
    }

    @Test
    @DisplayName("Observadores inscritos recebem os eventos publicados")
    void publicar_notificaObservadoresInscritos() {
        List<EventoReserva> recebidos = new ArrayList<>();
        ObservadorReserva obs = new ObservadorReserva() {
            @Override public void notificar(EventoReserva e) { recebidos.add(e); }
            @Override public boolean interessadoEm(TipoEventoReserva t) { return true; }
        };
        central.inscrever(obs);

        EventoReserva evento = new EventoReserva(
            TipoEventoReserva.RESERVA_CRIADA, new Aluguel(), "reserva ok");
        central.publicar(evento);

        assertEquals(1, recebidos.size());
        assertSame(evento, recebidos.get(0));
    }

    @Test
    @DisplayName("Desinscrever remove o observador da lista de notificacoes")
    void desinscrever_removeObservador() {
        List<EventoReserva> recebidos = new ArrayList<>();
        ObservadorReserva obs = new ObservadorReserva() {
            @Override public void notificar(EventoReserva e) { recebidos.add(e); }
            @Override public boolean interessadoEm(TipoEventoReserva t) { return true; }
        };
        central.inscrever(obs);
        central.desinscrever(obs);

        central.publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CRIADA, new Aluguel(), "irrelevante"));

        assertTrue(recebidos.isEmpty());
    }

    @Test
    @DisplayName("Historico registra todos os eventos publicados na ordem correta")
    void historico_armazenaEventosPublicados() {
        central.publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CRIADA, new Aluguel(), "a"));
        central.publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CANCELADA, new Aluguel(), "b"));

        List<CentralNotificacoes.EventoSnapshot> historico = central.getHistoricoEventos();
        assertEquals(2, historico.size());
        assertEquals(TipoEventoReserva.RESERVA_CRIADA, historico.get(0).tipo);
        assertEquals(TipoEventoReserva.RESERVA_CANCELADA, historico.get(1).tipo);
    }

    @Test
    @DisplayName("Excecao em um observador nao impede que os demais sejam notificados")
    void publicar_erroEmObservadorNaoAborta() {
        List<EventoReserva> recebidos = new ArrayList<>();
        ObservadorReserva quebrado = new ObservadorReserva() {
            @Override public void notificar(EventoReserva e) { throw new RuntimeException("boom"); }
            @Override public boolean interessadoEm(TipoEventoReserva t) { return true; }
        };
        ObservadorReserva saudavel = new ObservadorReserva() {
            @Override public void notificar(EventoReserva e) { recebidos.add(e); }
            @Override public boolean interessadoEm(TipoEventoReserva t) { return true; }
        };
        central.inscrever(quebrado);
        central.inscrever(saudavel);

        central.publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CRIADA, new Aluguel(), "continua"));

        assertEquals(1, recebidos.size());
    }

    @Test
    @DisplayName("Strategy de canal e respeitada pelo NotificadorCliente")
    void notificadorCliente_usaCanalInjetado() {
        List<String> destinos = new ArrayList<>();
        CanalNotificacao canalFake = new CanalNotificacao() {
            @Override public void enviar(String dest, String assunto, String msg) { destinos.add(dest); }
            @Override public String getNome() { return "FAKE"; }
        };
        Aluguel al = new Aluguel();
        al.setId(1L);
        var cliente = new com.puc.stayhub.model.Cliente("Fulano", "00000000000",
            "fulano@example.com", "31000000000");
        al.setCliente(cliente);
        NotificadorCliente notificador = new NotificadorCliente(
            canalFake, EnumSet.of(TipoEventoReserva.RESERVA_CRIADA));
        central.inscrever(notificador);

        central.publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CRIADA, al, "criada"));
        central.publicar(new EventoReserva(
            TipoEventoReserva.RESERVA_CANCELADA, al, "cancelada"));

        assertEquals(1, destinos.size(), "canal deveria receber somente o evento assinado");
        assertEquals("fulano@example.com", destinos.get(0));
    }
}