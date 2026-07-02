package com.puc.stayhub.config;

import org.hibernate.dialect.H2Dialect;

/**
 * Dialect H2 customizado para compatibilidade com Hibernate 6.4+.
 *
 * O Hibernate 6.4+ passou a emitir `INSERT ... RETURNING id` para colunas
 * IDENTITY, mas as versoes 2.x do H2 usadas pelo Spring Boot 3.3 nao
 * reconhecem essa sintaxe nessa posicao. Sobrescrever supportsInsertReturning()
 * para retornar false faz o Hibernate usar Statement.getGeneratedKeys(), que
 * funciona corretamente em qualquer versao de H2.
 */
public class H2CompatDialect extends H2Dialect {

    @Override
    public boolean supportsInsertReturning() {
        return false;
    }

    @Override
    public boolean supportsInsertReturningGeneratedKeys() {
        return false;
    }
}