package com.puc.stayhub.notificacao;

/**
 * * Strategy: contrato para qualquer canal de envio de notificacao. * Novos
 * canais (Push, Telegram, etc.) podem ser adicionados sem modificar o codigo
 * existente.
 */
public interface CanalNotificacao {
    void enviar(String destinatario, String assunto, String mensagem);

    String getNome();
}