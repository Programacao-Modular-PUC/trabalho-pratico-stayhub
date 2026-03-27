# StayHub

Sistema de hospedagem desenvolvido como trabalho prático da disciplina de Programação Modular.

## Sobre o projeto

O StayHub é um sistema de gerenciamento de hospedagens que permite cadastrar residências, quartos e clientes, além de realizar reservas e controlar aluguéis.

O projeto foi desenvolvido com foco em boas práticas de desenvolvimento de software, utilizando conceitos de Programação Orientada a Objetos e arquitetura em camadas.

## Objetivo

Desenvolver um sistema completo que contemple:

- Modelagem orientada a objetos  
- Arquitetura em camadas (Controller, Service, Repository, Model)  
- API REST com Spring Boot  
- Persistência em banco de dados (MySQL)  
- Testes automatizados  
- Aplicação de padrões de projeto  

## Funcionalidades

O sistema permite:

- Cadastro de residências e quartos  
- Cadastro e autenticação de clientes  
- Realização de reservas e aluguéis  
- Cálculo automático de diárias  
- Controle de disponibilidade de quartos  
- Emissão de recibos  
- Histórico de hospedagens  

## Regras de Negócio

- As diárias iniciam às 12h  
- Entrada após 12h conta como diária completa  
- Saída após 12h adiciona uma nova diária  
- O valor da diária é calculado com base em:
  - Valor base  
  - Tipo do quarto  
  - Itens adicionais (ar-condicionado, hidromassagem)  
- Um quarto não pode ser alugado se já estiver ocupado  
- É possível realizar reservas futuras  
- Todo aluguel gera um pagamento associado  

## Tecnologias utilizadas

### Backend
- Java  
- Spring Boot  
- API REST  

### Banco de Dados
- MySQL  

### Frontend
- HTML  
- CSS  

## Integrantes

- Cauã Thomarco Thomaz Teixeira  
- Guilherme Augusto da Silva Machado  
- Sofia Figueiredo de Oliveira  

## Professor

- Glender Brás  

## Instituição

Pontifícia Universidade Católica de Minas Gerais (PUC Minas)  
Bacharelado em Engenharia de Software  
