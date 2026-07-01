package com.puc.stayhub.notificacao;

public class CanalEmail implements CanalNotificacao {
    @Override
    public void enviar(String destinatario, String assunto, String mensagem) {
        System.out.println("[EMAIL] para=" + destinatario + " | assunto=" + assunto + " | mensagem=" + mensagem);
    }

    @Override
    public String getNome() {
        return "EMAIL";
    }
}