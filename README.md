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

## Escopo desta entrega (1ª Entrega)

A 1ª entrega pede uma coleção só, com documentos homogêneos e sem aninhamento, e CRUD básico em cima dela. É o que está aqui: a coleção `canteiros`, onde cada documento guarda o que foi plantado, quem é o responsável e a previsão de colheita.

Na 2ª entrega isso cresce para várias coleções relacionadas (hortas, escalas de cuidado, colheitas) com documentos aninhados.

### Exemplo de documento (coleção `canteiros`)

```json
{
  "id": "64f1a2b3c4d5e6f7a8b9c0d1",
  "nome": "Canteiro 1",
  "horta": "Horta Comunitária Vila Verde",
  "cultivo": "Alface",
  "responsavel": "Maria",
  "dataPlantio": "2026-08-01",
  "previsaoColheita": "2026-09-15",
  "status": "EM_CULTIVO"
}
```

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
model/         → Canteiro: documento mapeado para a coleção MongoDB (@Document)
enums/         → Status: estados possíveis de um canteiro
repository/    → CanteiroRepository: acesso a dados via Spring Data MongoDB
service/       → CanteiroService: regras de negócio
controller/    → CanteiroController: endpoints REST (API HTTP)
```

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

## Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/canteiros` | Cadastra um novo canteiro |
| `GET` | `/canteiros` | Lista todos os canteiros |
| `GET` | `/canteiros/{id}` | Busca um canteiro pelo id |
| `PUT` | `/canteiros/{id}` | Atualiza um canteiro existente |
| `DELETE` | `/canteiros/{id}` | Remove um canteiro |

### Exemplo de requisição (cadastro)

```
POST /canteiros
Content-Type: application/json

{
  "nome": "Canteiro 1",
  "horta": "Horta Comunitária Vila Verde",
  "cultivo": "Alface",
  "responsavel": "Maria",
  "dataPlantio": "2026-08-01",
  "previsaoColheita": "2026-09-15",
  "status": "EM_CULTIVO"
}
```
