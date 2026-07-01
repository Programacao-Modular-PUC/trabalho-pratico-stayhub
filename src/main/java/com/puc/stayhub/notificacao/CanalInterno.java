package com.puc.stayhub.notificacao;

import java.util.ArrayList;
import java.util.List;

public class CanalInterno implements CanalNotificacao {
    private final List<String> caixaEntrada = new ArrayList<>();

    @Override
    public void enviar(String destinatario, String assunto, String mensagem) {
        String registro = String.format("[INTERNO->%s] %s | %s", destinatario, assunto, mensagem);
        caixaEntrada.add(registro);
        System.out.println(registro);
    }

    public List<String> getCaixaEntrada() {
        return List.copyOf(caixaEntrada);
    }

    @Override
    public String getNome() {
        return "INTERNO";
    }
}