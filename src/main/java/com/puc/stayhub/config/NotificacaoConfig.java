package com.puc.stayhub.config;

import com.puc.stayhub.notificacao.CanalEmail;
import com.puc.stayhub.notificacao.CanalInterno;
import com.puc.stayhub.notificacao.CentralNotificacoes;
import com.puc.stayhub.notificacao.NotificadorCliente;
import com.puc.stayhub.notificacao.NotificadorProprietario;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * Registra os observadores padrao da CentralNotificacoes na inicializacao
 * da aplicacao. Como a Central e um Singleton, os observadores permanecem
 * ativos durante todo o ciclo de vida.
 */
@Configuration
public class NotificacaoConfig {

    @PostConstruct
    public void registrarObservadores() {
        CentralNotificacoes central = CentralNotificacoes.getInstancia();
        central.inscrever(new NotificadorCliente(new CanalEmail()));
        central.inscrever(new NotificadorProprietario(new CanalInterno(), "proprietario@stayhub.com"));
    }
}