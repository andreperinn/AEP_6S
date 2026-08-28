# Cultiva+

Prova de Conceito (PoC) desenvolvida para a Atividade de Estudo Programada (AEP) 2026.2 — 6º Semestre de Engenharia de Software (UniCesumar), integrando as disciplinas de **Banco de Dados NoSQL**, **Paradigmas de Linguagem**, **Processo de Software** e **Projeto, Implementação e Testes**.

## Identificação

| |                                                                  |
|---|------------------------------------------------------------------|
| **Curso** | Engenharia de Software                                           |
| **Série** | 6º Semestre                                                      |
| **Acadêmicos** | [André Perin Geraldo] [(RA: 24017529-2)] — [Marcos Vinicius de Azevedo Batista] (RA: [24055120-2]) |

## Problema

Hortas comunitárias urbanas em praças, terrenos cedidos, escolas ou condomínios — são uma ferramenta reconhecida de segurança alimentar e fortalecimento de vínculo comunitário. Na prática, porém, a maioria delas nasce com força e perde ritmo em poucos meses, não por falta de gente disposta a participar, e sim por falta de coordenação: não fica claro quem deveria cuidar de cada canteiro em determinado dia, o que está plantado em cada espaço, quando algo estará pronto para colher, e como dividir a produção entre os participantes sem gerar atrito.

O **Cultiva+** estrutura esse processo, hoje sustentado de forma informal (grupos de mensagens, cadernos, memória de alguém), em um sistema que dá visibilidade e organização ao trabalho coletivo da horta.

## ODS relacionado

**ODS 11 — Cidades e Comunidades Sustentáveis**

Hortas comunitárias são um exemplo concreto de infraestrutura urbana sustentável gerida pelos próprios moradores. O Cultiva+ ataca diretamente o motivo mais comum pelo qual essas iniciativas falham — a falta de organização —, aumentando a chance de que sobrevivam e cresçam ao longo do tempo.

## Escopo desta entrega (1ª Entrega)

Conforme os requisitos técnicos da 1ª entrega da AEP, esta versão trabalha com **uma única coleção NoSQL** (`canteiros`), com documentos homogêneos e estrutura simples (sem aninhamento), e opera com **CRUD básico** sobre essa coleção.

Funcionalidade implementada: cadastro, consulta, atualização e remoção de canteiros — cada canteiro registra o que está plantado, quem é o responsável e a previsão de colheita.

> A evolução prevista para a 2ª entrega (múltiplas coleções relacionadas — hortas, escalas de cuidado, colheitas — com documentos aninhados) está descrita no enunciado da AEP e será tratada na próxima etapa do projeto.

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

A forma mais simples de rodar localmente, sem precisar instalar MongoDB manualmente, é executar a classe utilitária de testes que já sobe um MongoDB real em container automaticamente:

```
src/test/java/com/cultivaplus/AEP_6S/TestAep6SApplication.java
```

Basta rodar essa classe (`Run` pela IDE, ou `mvn spring-boot:test-run`). A aplicação sobe em `http://localhost:8080`.

Alternativamente, para rodar a aplicação "de produção" (`Aep6SApplication`) contra um MongoDB próprio, configure a URI de conexão em `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/cultivaplus
```

### Rodando os testes e gerando o relatório de cobertura

```bash
mvn test
```

O relatório de cobertura (JaCoCo) é gerado automaticamente em `target/site/jacoco/index.html`.

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
