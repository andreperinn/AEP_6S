# Cultiva+

Prova de Conceito (PoC) desenvolvida para a Atividade de Estudo Programada (AEP) 2026.2 — 6º Semestre de Engenharia de Software (UniCesumar), integrando as disciplinas de **Banco de Dados NoSQL**, **Paradigmas de Linguagem**, **Processo de Software** e **Projeto, Implementação e Testes**.

## Identificação

| |                                                                  |
|---|------------------------------------------------------------------|
| **Curso** | Engenharia de Software                                           |
| **Série** | 6º Semestre                                                      |
| **Acadêmicos** | André Perin Geraldo (RA: 24017529-2) — Marcos Vinicius de Azevedo Batista (RA: 24055120-2) |

## Problema

Muita horta comunitária começa animada e morre em poucos meses. Quase nunca falta gente disposta falta combinação. Ninguém sabe direito quem rega o quê na terça,
o que foi plantado em cada canteiro, quando aquilo vai estar pronto pra colher e como dividir o que sair.

Hoje isso vive no grupo do WhatsApp, num caderno ou na memória de alguém. O Cultiva+ junta tudo num lugar só.

## ODS relacionado

**ODS 11 — Cidades e Comunidades Sustentáveis**

Horta comunitária é a própria comunidade cuidando de um espaço urbano. O ponto em que essas iniciativas geralmente travam é a organização, e é aí que o Cultiva+ entra.

## Evolução do projeto

| Entrega | Escopo |
|---|---|
| **1ª entrega** (tag `AEP-entrega1`) | Uma única coleção (`canteiros`), documentos simples e CRUD básico |
| **2ª entrega** (esta versão) | Três coleções relacionadas (`hortas`, `canteiros`, `escalas`), documentos aninhados e validação de integridade entre coleções |

## Modelo de dados (MongoDB)

O banco tem três coleções. Os relacionamentos são feitos por **referência** (o documento guarda o `id` do documento relacionado), e a API valida essas referências antes de gravar.

```
hortas (1) ────< canteiros (1) ────< escalas
   hortaId fica em canteiros       canteiroId fica em escalas
```

| Coleção | Descrição | Relacionamento | Parte aninhada |
|---|---|---|---|
| `hortas` | Espaço comunitário onde ficam os canteiros | — | `endereco` (subdocumento) |
| `canteiros` | O que está plantado, responsável e previsão de colheita | `hortaId` → `hortas` | — |
| `escalas` | Quem cuida de qual canteiro e quais cuidados faz | `canteiroId` → `canteiros` | `tarefas` (lista de subdocumentos) |

**Por que referência e não embutir tudo?** Canteiros e escalas mudam com frequência e são consultados isoladamente (por exemplo, todas as escalas de um canteiro). Embutir os canteiros dentro da horta e as escalas dentro do canteiro faria documentos crescerem sem limite e tornaria as atualizações mais pesadas. Já o endereço e as tarefas pertencem a um único documento pai, então ficam embutidos.

**Regras de integridade** (aplicadas na camada de serviço):
- Não é possível cadastrar ou atualizar um canteiro com `hortaId` inexistente (HTTP 400).
- Não é possível cadastrar ou atualizar uma escala com `canteiroId` inexistente (HTTP 400).
- Não é possível remover uma horta que ainda tem canteiros, nem um canteiro que ainda tem escalas (HTTP 409).

### Exemplo de documento (coleção `hortas`)

```json
{
  "id": "64f1a2b3c4d5e6f7a8b9c0d1",
  "nome": "Horta Comunitária Vila Verde",
  "descricao": "Horta mantida pelos moradores do bairro",
  "endereco": {
    "rua": "Rua A",
    "bairro": "Centro",
    "cidade": "Maringá",
    "estado": "PR"
  }
}
```

### Exemplo de documento (coleção `canteiros`)

```json
{
  "id": "64f1a2b3c4d5e6f7a8b9c0d2",
  "nome": "Canteiro 1",
  "hortaId": "64f1a2b3c4d5e6f7a8b9c0d1",
  "cultivo": "Alface",
  "responsavel": "Maria",
  "dataPlantio": "2026-08-01",
  "previsaoColheita": "2026-09-15",
  "status": "EM_CULTIVO"
}
```

### Exemplo de documento (coleção `escalas`)

```json
{
  "id": "64f1a2b3c4d5e6f7a8b9c0d3",
  "canteiroId": "64f1a2b3c4d5e6f7a8b9c0d2",
  "responsavel": "Maria",
  "tarefas": [
    { "tipo": "REGA", "data": "2026-09-10", "concluida": false, "observacao": "Regar de manhã" },
    { "tipo": "ADUBACAO", "data": "2026-09-12", "concluida": false, "observacao": null }
  ]
}
```

Valores aceitos: `status` (canteiro) = `EM_CULTIVO` ou `COLHIDO`; `tipo` (tarefa) = `REGA`, `ADUBACAO`, `CAPINA`, `PODA` ou `COLHEITA`.

## Tecnologias utilizadas

- **Java 21**
- **Spring Boot 4.1.1** (Spring Web / Spring Data MongoDB)
- **MongoDB** — banco de dados NoSQL orientado a documentos
- **Maven** — gerenciador de build e dependências
- **JUnit 5 + Testcontainers** — testes automatizados com MongoDB real em container Docker
- **JaCoCo** — relatório de cobertura de testes
- **springdoc-openapi (Swagger UI)** — documentação interativa da API, gerada automaticamente a partir do código

## Arquitetura do projeto

O código segue uma organização em camadas:

```
model/         → Horta, Canteiro, Escala: documentos mapeados para coleções (@Document)
                 Endereco e Tarefa: objetos aninhados dentro de Horta e Escala
enums/         → Status (canteiro) e TipoCuidado (tarefa)
repository/    → HortaRepository, CanteiroRepository, EscalaRepository (Spring Data MongoDB)
service/       → HortaService, CanteiroService, EscalaService: regras de negócio e integridade
controller/    → HortaController, CanteiroController, EscalaController: endpoints REST
                 GlobalExceptionHandler: converte exceções em respostas HTTP (404, 400, 409)
exception/     → ReferenciaInvalidaException e RecursoEmUsoException
```

Cada camada tem uma responsabilidade: o controller só trata HTTP, o service concentra as regras (inclusive a validação das referências entre coleções) e o repository só acessa o banco.

## Metodologia de trabalho

O projeto é organizado em um quadro Kanban no **Trello**:

**https://trello.com/b/Ug9FrTzI/aep-20262-6s**

O quadro tem as colunas *A fazer*, *Em andamento*, *Concluído* e *Entregas*. Cada tarefa vira um cartão, classificado por etiqueta de demanda (Banco de Dados, Código, Teste, Documentação, entre outras), e vai avançando entre as colunas conforme é concluída. Os cartões concluídos indicam o commit correspondente.

No GitHub, o trabalho da 2ª entrega foi feito no branch `entrega-2`, em commits pequenos, um por funcionalidade (modelos, tratamento de erros, relacionamento do canteiro, CRUD de horta, CRUD de escala e documentação), e depois integrado ao `main`. A versão de cada entrega é identificada por tag (`AEP-entrega1` e `AEP-entrega2`).

## Como executar

### Pré-requisitos

- JDK 21+
- Maven
- Docker Desktop em execução (necessário para o MongoDB via Testcontainers)

### Rodando a aplicação

A forma mais simples de rodar localmente, sem precisar instalar MongoDB manualmente, é executar a classe de testes que já sobe um MongoDB real em container automaticamente:

```
src/test/java/com/cultivaplus/AEP_6S/TestAep6SApplication.java
```

Basta rodar essa classe (`Run` pela IDE, ou `mvn spring-boot:test-run`). A aplicação sobe em `http://localhost:8080`.

Com a aplicação no ar, a forma mais simples de testar e explorar a API é pelo **Swagger UI**, disponível em:

```
http://localhost:8080/swagger-ui.html
```

Lá é possível ver todos os endpoints documentados e executar requisições reais direto do navegador, sem precisar de Postman ou qualquer ferramenta externa.

Alternativamente, para rodar a aplicação "de produção" (`Aep6SApplication`) contra um MongoDB próprio, configure a URI de conexão em `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/cultivaplus
```

### Rodando os testes e gerando o relatório de cobertura

```bash
mvn test
```

O relatório de cobertura (JaCoCo) é gerado automaticamente em `target/site/jacoco/index.html`. O build também aplica uma checagem automática: o `mvn test` **falha** se a cobertura de linhas ficar abaixo de 70%, garantindo que o requisito mínimo da AEP seja sempre respeitado.

Na versão da 2ª entrega, a suíte tem **68 testes**: testes unitários dos services (Mockito, com o repositório simulado) e testes de integração dos controllers (MockMvc com MongoDB real em container Docker). A cobertura de linhas medida é de cerca de **90%**.

## Endpoints da API

Cada recurso tem o CRUD completo:

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/hortas`, `/canteiros`, `/escalas` | Cadastra um novo registro |
| `GET` | `/hortas`, `/canteiros`, `/escalas` | Lista todos os registros |
| `GET` | `/canteiros?hortaId={id}` | Lista os canteiros de uma horta |
| `GET` | `/escalas?canteiroId={id}` | Lista as escalas de um canteiro |
| `GET` | `/hortas/{id}`, `/canteiros/{id}`, `/escalas/{id}` | Busca um registro pelo id |
| `PUT` | `/hortas/{id}`, `/canteiros/{id}`, `/escalas/{id}` | Atualiza um registro existente |
| `DELETE` | `/hortas/{id}`, `/canteiros/{id}`, `/escalas/{id}` | Remove um registro |

Códigos de resposta de erro: `404` (id não encontrado), `400` (referência para horta/canteiro inexistente) e `409` (remoção bloqueada porque existem registros dependentes).

### Roteiro de uso (ordem para testar no Swagger)

Como os documentos se referenciam, a ordem de criação importa:

1. `POST /hortas` com o endereço aninhado. Copie o `id` retornado.
2. `POST /canteiros` informando esse `id` em `hortaId`. Copie o `id` do canteiro.
3. `POST /escalas` informando o `id` do canteiro em `canteiroId` e a lista de `tarefas`.
4. `GET /canteiros?hortaId=...` e `GET /escalas?canteiroId=...` mostram os relacionamentos funcionando.
5. `DELETE /hortas/{id}` retorna `409`, pois a horta ainda tem canteiros.

### Exemplo de requisição (cadastro de horta)

```
POST /hortas
Content-Type: application/json

{
  "nome": "Horta Comunitária Vila Verde",
  "descricao": "Horta mantida pelos moradores do bairro",
  "endereco": {
    "rua": "Rua A",
    "bairro": "Centro",
    "cidade": "Maringá",
    "estado": "PR"
  }
}
```

### Exemplo de requisição (cadastro de escala)

```
POST /escalas
Content-Type: application/json

{
  "canteiroId": "<id do canteiro>",
  "responsavel": "Maria",
  "tarefas": [
    { "tipo": "REGA", "data": "2026-09-10", "concluida": false, "observacao": "Regar de manhã" }
  ]
}
```
