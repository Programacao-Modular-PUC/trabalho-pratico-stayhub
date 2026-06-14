# StayHub - Backend (Sprint 3)

Backend do sistema de hospedagem **StayHub**, desenvolvido para a disciplina de Programação Modular (PUC Minas). A Sprint 3 acrescenta tratamento de exceções personalizadas, testes unitários com JUnit e novos requisitos funcionais (filtro por tipo, cancelamento e histórico de alugueis).

## Tecnologias

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- MySQL 8 (perfil padrão)
- H2 (perfil opcional, para testes locais sem MySQL)
- Maven
- JUnit 5 + Mockito (testes)

## Arquitetura

O projeto segue uma arquitetura em camadas, com separação por pacote:

```
controller  →  service  →  repository  →  model (entidades JPA)
                  │
                  └──→ exception (exceções personalizadas + handler global)
```

- **controller** — endpoints REST (`@RestController`)
- **service** — regras de negócio
- **repository** — interfaces `JpaRepository`
- **model** — entidades JPA, enums e a hierarquia polimórfica de `Quarto`
- **dto** — objetos de transporte de requisições
- **exception** — exceções customizadas e `GlobalExceptionHandler` (`@RestControllerAdvice`)

A herança entre os tipos de quarto (`Quarto`, `QuartoIndividual`, `QuartoDuplo`, `QuartoFamilia`) é mapeada via estratégia `SINGLE_TABLE` do JPA, usando a coluna discriminadora `tipo`. O cálculo da diária é resolvido por polimorfismo: cada subclasse implementa `calcularDiaria(numHospedes, solicitouBerco)`.

## Endpoints

### Recursos base

| Recurso | Endpoint base | Verbos |
|---------|---------------|--------|
| Residências | `/residencias` | `GET`, `POST`, `PUT`, `DELETE` |
| Quartos | `/quartos` | `GET`, `POST`, `DELETE` |
| Clientes | `/clientes` | `GET`, `POST`, `PUT`, `DELETE` |
| Aluguéis | `/alugueis` | `GET`, `POST`, `DELETE` |

### Endpoints adicionados na Sprint 3

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `GET` | `/quartos?tipo=DUPLO` | Filtra quartos pelo tipo (`INDIVIDUAL`, `DUPLO`, `FAMILIA`) |
| `GET` | `/quartos?residenciaId=1&tipo=FAMILIA` | Filtra por residência **e** tipo |
| `PATCH` | `/alugueis/{id}/cancelar` | Cancela um aluguel (muda status para `CANCELADO`) |
| `GET` | `/alugueis/cliente/{id}/historico` | Histórico completo do cliente ordenado por data (inclui cancelados) |

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

## Tratamento de Exceções

A Sprint 3 introduz exceções personalizadas no pacote `com.puc.stayhub.exception` e um `GlobalExceptionHandler` centralizado, que padroniza o JSON de erro:

```json
{
  "timestamp": "2026-06-14T19:59:14.407582700",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente nao encontrado: 999"
}
```

### Mapeamento de exceções → códigos HTTP

| Exceção | Código HTTP | Quando ocorre |
|---------|-------------|---------------|
| `QuartoIndisponivelException` | 409 Conflict | Sobreposição de datas com aluguel ativo |
| `CapacidadeExcedidaException` | 400 Bad Request | `numHospedes` excede a capacidade do quarto |
| `DataInvalidaException` | 400 Bad Request | Datas nulas, invertidas, no passado |
| `RecursoNaoPermitidoException` | 422 Unprocessable Entity | Berço em quarto individual / berço indisponível no duplo |
| `MethodArgumentNotValidException` | 400 Bad Request | Falha de validação `@Valid` |
| `IllegalArgumentException` | 400 Bad Request | Pré-condição violada (ex.: `numHospedes <= 0`) |
| `DateTimeParseException` | 400 Bad Request | Formato de data inválido |
| `NullPointerException` | 500 Internal Server Error | Campo obrigatório ausente |
| `ResponseStatusException` | Conforme status | 404 (entidade inexistente) / 409 (conflitos de estado) |

## Testes (JUnit 5 + Mockito)

A suíte cobre os 4 pontos exigidos pela Sprint 3:

- **Cálculo de diária** por tipo de quarto (Individual, Duplo, Família)
- **Regras de berço** (proibido no Individual, opcional no Duplo)
- **Limites de hóspedes** (capacidade máxima por tipo)
- **Disponibilidade** (sobreposição de datas no `AluguelService`)

Estrutura dos testes:

```
src/test/java/com/puc/stayhub/
├── model/
│   ├── QuartoIndividualTest.java   (6 testes)
│   ├── QuartoDuploTest.java        (5 testes)
│   ├── QuartoFamiliaTest.java      (7 testes)
│   └── AluguelTest.java            (5 testes)
└── service/
    └── AluguelServiceTest.java     (8 testes — usa Mockito)
```

**Total: 31 testes, todos verdes.**

### Rodar os testes

```bash
mvn test
```

### Gerar o relatório HTML do JUnit

```bash
mvn surefire-report:report
```

Relatório formatado em `target/reports/surefire.html`. Logs `.txt` e `.xml` em `target/surefire-reports/`. Para a entrega, imprima o HTML como PDF (`Ctrl+P`).

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

### Cancelar Aluguel (Sprint 3)

```http
PATCH /alugueis/5/cancelar
```

### Histórico do cliente (Sprint 3)

```http
GET /alugueis/cliente/1/historico
```

Retorna todos os aluguéis do cliente (ativos, concluídos e cancelados) ordenados por `dataInicio` decrescente.

### Filtrar quartos por tipo (Sprint 3)

```http
GET /quartos?tipo=DUPLO
GET /quartos?residenciaId=1&tipo=FAMILIA
```

## Diagrama de classes

Disponível em `Docs/diagrama-classes.md` (Mermaid) e `Docs/diagrama-classes.puml` (PlantUML).

## Integrantes

- Cauã Thomarco Thomaz Teixeira — Sprint 3: Testes unitários JUnit
- Guilherme Augusto da Silva Machado — Sprint 3: Filtro por tipo, cancelamento e histórico
- Sofia Figueiredo de Oliveira — Sprint 3: Exceções personalizadas e tratamento global

## Professor

- Glender Brás