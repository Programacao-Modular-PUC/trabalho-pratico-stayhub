# StayHub — Sistema de Hospedagem

Trabalho prático da disciplina de **Programação Modular** (PUC Minas — Bacharelado em Engenharia de Software, Professor Glender Brás). O sistema simula uma plataforma de hospedagem no estilo Airbnb/Booking, cobrindo modelagem de domínio, API REST, persistência, testes, tratamento de exceções e padrões de projeto (Sprints 1 a 4).

Este README documenta o estado **final** do projeto após a Sprint 4.

## Tecnologias

**Backend**
- Java 17
- Spring Boot 3.3
- Spring Data JPA + Hibernate
- PostgreSQL 15 (perfil padrão) · MySQL 8 (perfil `dev`) · H2 (perfil `h2`, em memória)
- Maven
- JUnit 5 + Mockito

**Frontend**
- HTML5 / CSS3 / JavaScript nativo (sem framework)
- Servido via `http-server` (Node.js) na porta 3000

## Como rodar tudo

Você precisa de **dois terminais** abertos em paralelo.

### Terminal 1 — Backend (H2 em memória, recomendado para demonstração)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

Sobe em `http://localhost:8080`. Console H2 disponível em `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:stayhub`, usuário `sa`, sem senha).

Para usar PostgreSQL (perfil padrão) basta rodar `mvn spring-boot:run` (requer Postgres em `localhost:5432` com usuário `postgres` / senha `1011`).

### Terminal 2 — Frontend

```bash
npm install   # apenas na primeira vez
npm run dev
```

Abre automaticamente `http://localhost:3000/stayhub.html`. O admin panel fica em ⚙️ **Admin** no canto superior direito.

## Arquitetura em camadas

```
controller  →  service  →  repository  →  model (entidades JPA)
                 │
                 ├─→ exception    (excecoes personalizadas + handler global)  [Sprint 3]
                 ├─→ notificacao  (Observer + Strategy + Singleton)           [Sprint 4]
                 ├─→ tarifa       (Strategy + Decorator + Singleton)          [Sprint 4]
                 ├─→ log          (Singleton)                                 [Sprint 4]
                 └─→ config       (bootstrap de observadores, dialect H2)    [Sprint 4]
```

- **controller** — endpoints REST (`@RestController`)
- **service** — regras de negócio
- **repository** — interfaces `JpaRepository`
- **model** — entidades JPA e a hierarquia polimórfica de `Quarto`
- **dto** — objetos de transporte de requisições

A herança entre os tipos de quarto (`Quarto`, `QuartoIndividual`, `QuartoDuplo`, `QuartoFamilia`) é mapeada via `SINGLE_TABLE` do JPA com a coluna discriminadora `tipo`. O cálculo da diária base é resolvido por polimorfismo em cada subclasse.

## Funcionalidades por Sprint

### Sprint 1 — Modelagem OO e interface web
- Entidades `Residencia`, `Quarto` (abstrata + 3 especializações), `Cliente` e `Aluguel`
- Cartões CRC em `Docs/`
- Landing page do StayHub (`stayhub.html`)

### Sprint 2 — API REST e persistência JPA
- CRUD de `Residencia`, `Quarto`, `Cliente` e `Aluguel`
- Perfis Spring: `default` (PostgreSQL), `dev` (MySQL com dados de exemplo), `h2` (em memória)

### Sprint 3 — Robustez e testes
- Exceções personalizadas + `GlobalExceptionHandler` (`@RestControllerAdvice`)
- Filtro de quartos por tipo (`GET /quartos?tipo=DUPLO`)
- Cancelamento de aluguel (`PATCH /alugueis/{id}/cancelar`)
- Histórico do cliente (`GET /alugueis/cliente/{id}/historico`)
- **31 testes** JUnit 5 + Mockito

### Sprint 4 — Padrões de projeto (GoF)
Duas funcionalidades das seis opções do enunciado, mais o Singleton obrigatório:

| Funcionalidade | Padrões aplicados | Novos endpoints REST |
|---|---|---|
| **Central de Notificações** (Opção 3) | Observer + Strategy + Singleton | `GET /notificacoes/historico`, `GET /notificacoes/logs` |
| **Tarifação Flexível** (Opção 1) | Strategy + Decorator + Singleton | `POST /tarifas/feriados`, `POST /tarifas/promocoes`, `POST /tarifas/simular` |
| **Singleton em recurso global** (obrigatório) | Singleton | — |

Os **três Singletons** aplicados: `CentralNotificacoes`, `GerenciadorTarifas` e `LogService` — cada um justificado por representar um recurso genuinamente global. Detalhes completos e justificativa das escolhas no relatório em `relatorio-final/relatorio_final.pdf`.

- **13 testes** adicionais (`CentralNotificacoesTest` + `GerenciadorTarifasTest`)
- Integração no `AluguelService.criar()`: calcula via `GerenciadorTarifas`, registra no `LogService` e publica `EventoReserva` na `CentralNotificacoes`

**Total: 44 testes, todos verdes.**

## Endpoints REST — visão consolidada

### CRUD base (Sprints 1-2)

| Recurso | Endpoint | Verbos |
|---|---|---|
| Residências | `/residencias` | GET, POST, PUT, DELETE |
| Quartos | `/quartos` | GET, POST, DELETE |
| Clientes | `/clientes` | GET, POST, PUT, DELETE |
| Aluguéis | `/alugueis` | GET, POST, DELETE |

### Sprint 3

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/quartos?tipo=DUPLO` | Filtra quartos por tipo |
| `GET` | `/quartos?residenciaId=1&tipo=FAMILIA` | Filtra por residência e tipo |
| `PATCH` | `/alugueis/{id}/cancelar` | Cancela um aluguel (status → CANCELADO) |
| `GET` | `/alugueis/cliente/{id}/historico` | Histórico completo do cliente |

### Sprint 4

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/tarifas/feriados` | Cadastra feriado (aplica +20% em diárias nessa data) |
| `DELETE` | `/tarifas/feriados` | Remove feriado |
| `GET` | `/tarifas/feriados` | Lista feriados ativos |
| `POST` | `/tarifas/promocoes` | Cadastra promoção temporária (`nome`, `percentual`, `inicio`, `fim`) |
| `POST` | `/tarifas/simular` | Simula valor final da diária dada a cadeia de regras ativas |
| `GET` | `/notificacoes/historico` | Retorna todos os `EventoReserva` publicados |
| `GET` | `/notificacoes/logs` | Retorna todas as entradas do `LogService` |

## Frontend — admin panel

O frontend expõe **todas** as funcionalidades do backend em `http://localhost:3000/stayhub.html` → botão ⚙️ **Admin**. São 6 tabs:

- 🏠 **Residências** — CRUD
- 🛏️ **Quartos** — CRUD + filtro por tipo (Sprint 3)
- 👤 **Clientes** — CRUD
- 📋 **Aluguéis** — CRUD, cancelamento com badge de status, busca de histórico por cliente (Sprint 3)
- 🎫 **Tarifas** — cadastro de feriados/promoções e simulação com detalhamento da cadeia de decoradores (Sprint 4)
- 🔔 **Notificações** — histórico de eventos publicados pela CentralNotificacoes e log da aplicação (Sprint 4)

## Regras de cálculo da diária base (por tipo de quarto)

```
Individual: valorBase + (camasSolteiro - 1) × 25 + adicionais
Duplo:      valorBase + adicionalCamaCasal + (berço ? 35 : 0) + adicionais
Família:    valorBase × (1 + 0,08 × numHospedes) × (1 - descontoGrupo) + adicionais
```

- Adicionais comuns: AR +R$ 30, hidromassagem +R$ 50
- Adicional de cama do duplo: CASAL = 40, QUEEN = 80, KING = 120
- Desconto de grupo (família): 3 hóspedes = 5%, 4-5 = 10%, 6+ = 15%

A partir da Sprint 4 esse valor base é passado por uma **cadeia dinâmica de decoradores** (`AltaTemporada`, `BaixaTemporada`, `Feriado`, `PromocaoTemporaria`, `DescontoClienteFrequente`) montada pelo `GerenciadorTarifas`.

## Testes

Executa a suíte completa:

```bash
mvn test
```

Distribuição:

- `QuartoIndividualTest` — 6
- `QuartoDuploTest` — 5
- `QuartoFamiliaTest` — 7
- `AluguelTest` — 5
- `AluguelServiceTest` — 8
- `CentralNotificacoesTest` — 6 (Sprint 4)
- `GerenciadorTarifasTest` — 7 (Sprint 4)

**Total: 44 testes.**

## Documentação

- **Relatório final:** `relatorio-final/relatorio_final.pdf` (fonte LaTeX em `relatorio_final.tex` para Overleaf; gerador Python em `gerar_pdf_relatorio.py`)
- **Diagrama UML:** `relatorio-final/diagrama_final.puml` (renderizar em <https://www.plantuml.com/plantuml/uml>)
- **Cartões CRC:** `Docs/Cartões CRC.docx`

## Integrantes

- **Cauã Thomarco Thomaz Teixeira** — Sprint 3: testes unitários JUnit; Sprint 4: integração no `AluguelService`, diagrama UML e relatório final
- **Guilherme Augusto da Silva Machado** — Sprint 3: filtro por tipo, cancelamento e histórico; Sprint 4: Tarifação Flexível (Strategy + Decorator + Singleton `GerenciadorTarifas`) e endpoints `/tarifas`
- **Sofia Figueiredo de Oliveira** — Sprint 3: exceções personalizadas e tratamento global; Sprint 4: Central de Notificações (Observer + Strategy + Singleton `CentralNotificacoes`), `LogService`, endpoints `/notificacoes`

## Professor

Glender Brás
