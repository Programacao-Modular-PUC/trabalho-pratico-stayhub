# StayHub - Backend (Sprint 2)

Backend do sistema de hospedagem **StayHub**, desenvolvido para a disciplina de Programação Modular (PUC Minas) na Sprint 2.

## Tecnologias

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- MySQL 8 (perfil padrão)
- H2 (perfil opcional, para testes locais sem MySQL)
- Maven

## Arquitetura

O projeto segue uma arquitetura em camadas:

```
controller  →  service  →  repository  →  model (entidades JPA)
```

A herança entre os tipos de quarto (`Quarto`, `QuartoIndividual`, `QuartoDuplo`, `QuartoFamilia`) é mapeada via estratégia `SINGLE_TABLE` do JPA, usando uma coluna discriminadora `tipo`.

## Endpoints

| Recurso | Endpoint base |
|---------|---------------|
| Residências | `/residencias` |
| Quartos | `/quartos` |
| Clientes | `/clientes` |
| Aluguéis | `/alugueis` |

Cada recurso oferece os verbos REST padrão: `GET` (lista e busca por id), `POST` (criação), `PUT` (atualização) e `DELETE` (remoção). O endpoint `GET /quartos/{id}/diaria?hospedes=X&berco=Y` simula o cálculo de diária sem persistir aluguel.

## Regras de cálculo

### Quarto Individual
`diária = valorBase + (camasSolteiro - 1) × 25,00 + adicionais`

### Quarto Duplo
`diária = valorBase + adicionalCamaCasal + (berço ? 35,00 : 0) + adicionais`

Adicional de conforto: `CASAL = 40`, `QUEEN = 80`, `KING = 120`.

### Quarto Família
`diária = valorBase × (1 + 0,08 × numHospedes) × (1 - descontoGrupo) + adicionais`

Desconto progressivo: 3 hóspedes = 5%, 4–5 = 10%, 6+ = 15%.

### Adicionais comuns
- Ar-condicionado: +R$ 30
- Hidromassagem: +R$ 50

## Como executar

### 1. Com MySQL (perfil padrão)

Tendo MySQL rodando em `localhost:3306` com usuário `root` / senha `root`:

```bash
mvn spring-boot:run
```

O banco `stayhub` será criado automaticamente.

### 2. Com MySQL + dados de teste (perfil `dev`)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Cria o banco `stayhub_dev` do zero e popula com residências, quartos e clientes de exemplo.

### 3. Sem MySQL (perfil `h2`, em memória)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Console H2 disponível em `http://localhost:8080/h2-console`.

## Exemplos de requisição

### Criar Quarto Individual

```http
POST /quartos
Content-Type: application/json

{
  "tipo": "INDIVIDUAL",
  "residenciaId": 1,
  "valorBase": 150.00,
  "possuiAR": true,
  "possuiHidro": false,
  "camasSolteiro": 2
}
```

### Criar Quarto Duplo

```json
{
  "tipo": "DUPLO",
  "residenciaId": 1,
  "valorBase": 250.00,
  "possuiAR": true,
  "possuiHidro": true,
  "tipoCamaCasal": "QUEEN",
  "possuiBercoDisponivel": true
}
```

### Criar Quarto Família

```json
{
  "tipo": "FAMILIA",
  "residenciaId": 2,
  "valorBase": 400.00,
  "possuiAR": true,
  "possuiHidro": false,
  "camasSolteiro": 2,
  "camasCasal": 1,
  "camasQueenKing": 0,
  "quantidadeAmbientes": 2
}
```

### Criar Aluguel

```http
POST /alugueis
Content-Type: application/json

{
  "clienteId": 1,
  "quartoId": 3,
  "dataInicio": "2026-06-10",
  "dataFim": "2026-06-15",
  "numHospedes": 2,
  "solicitouBerco": true
}
```

## Integrantes

- Cauã Thomarco Thomaz Teixeira
- Guilherme Augusto da Silva Machado
- Sofia Figueiredo de Oliveira

## Professor

- Glender Brás
