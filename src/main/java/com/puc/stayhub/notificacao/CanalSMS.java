package com.puc.stayhub.notificacao;

public class CanalSMS implements CanalNotificacao {
    @Override
    public void enviar(String destinatario, String assunto, String mensagem) {
        System.out.println("[SMS] para=" + destinatario + " | " + mensagem);
    }

    @Override
    public String getNome() {
        return "SMS";
    }
}