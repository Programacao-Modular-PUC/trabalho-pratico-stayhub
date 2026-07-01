package com.puc.stayhub.notificacao;

public class CanalWhatsApp implements CanalNotificacao {
    @Override
    public void enviar(String destinatario, String assunto, String mensagem) {
        System.out.println("[WHATSAPP] para=" + destinatario + " | titulo=" + assunto + " | mensagem=" + mensagem);
    }

    @Override
    public String getNome() {
        return "WHATSAPP";
    }
}