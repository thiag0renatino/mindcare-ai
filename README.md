<h1 align="center">
  <br>
  MindCare AI — Backend
  <br>
</h1>

<p align="center">
  API RESTful para triagem inteligente, monitoramento de bem-estar e gestão corporativa de saúde utilizando IA generativa.
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img alt="Spring AI" src="https://img.shields.io/badge/Spring_AI-Azure_OpenAI-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
</p>

---

## Ecossistema MindCare

Este repositório contém o **backend (API)** do MindCare AI. O sistema é composto por dois repositórios:

| Componente | Repositório | Tecnologia |
|---|---|---|
| API — este repositório | [mindcare-ai](https://github.com/thiag0renatino/mindcare-ai) | Spring Boot |
| Interface Web | [mindcare-ai-ui](https://github.com/thiag0renatino/mindcare-ai-ui) | Angular |

> A interface web consome diretamente esta API. Para executar o sistema completo, inicialize o backend primeiro e em seguida a UI, apontando para `http://localhost:8080`.

---

## Descrição do Projeto

O **MindCare AI** é uma solução desenvolvida para melhorar o acompanhamento da saúde física e mental dos trabalhadores dentro do ambiente corporativo.
A ausência de um sistema automatizado de triagem, histórico de saúde e integração entre empresas, colaboradores e profissionais compromete:

- a **agilidade** na detecção de riscos;
- a **precisão** na classificação de casos;
- o **encaminhamento eficiente** para profissionais ou ações;
- a **visão unificada** do bem-estar organizacional.

Este projeto propõe uma solução robusta que possibilita:

- **Triagem inteligente**, interpretando relatos de colaboradores e classificando risco como baixo, moderado ou alto;
- **Armazenamento completo do histórico** de triagens, encaminhamentos e acompanhamentos;
- **API RESTful** moderna com Java Spring Boot, seguindo boas práticas (DTOs, serviços, validações, exceções globais etc.);
- **Integração ativa com IA**, responsável por interpretar relatos e sugerir ações com encaminhamentos por especialidade;
- **Estrutura organizada** de entidades relacionadas (Empresa, Usuário, Triagem, Encaminhamento, Acompanhamento).

📌 **Público-alvo:** Departamentos de RH, profissionais de saúde e equipes responsáveis pelo bem-estar corporativo.

---

## Como a MindCheck AI está funcionando

O fluxo de triagem inteligente utiliza o Spring AI com Azure OpenAI (configuradas via variáveis `AZURE_OPENAI_*`) e funciona da seguinte forma:

1. **Endpoint protegido** `POST /api/mindcheck-ai/analises` recebe o relato e dados opcionais sobre sintomas, humor e rotina. O usuário é identificado automaticamente via token JWT.
2. **Rate limiting via Redis**: cada usuário pode realizar no máximo **10 análises por hora**. Excedido o limite, a API retorna erro até a janela ser renovada.
3. **Prompting estruturado**: o `MindCheckAiService` monta uma instrução fixa para o modelo gerar um JSON contendo risco, sugestoes, encaminhamentos e justificativa. Qualquer JSON inválido dispara uma `MindCheckAiException`.
4. **Persistência automática**: a resposta é convertida em `MindCheckAiResponseDTO`, uma nova `Triagem` é salva e, quando o risco é `MODERADO` ou `ALTO`, **um `Encaminhamento` é criado automaticamente para cada especialidade sugerida pela IA**, com prioridade proporcional ao risco.
5. **Retorno completo**: o payload da IA já vem acrescido dos dados da triagem persistida e da lista de encaminhamentos gerados.

Esse fluxo garante que toda análise realizada pela IA deixe registros no banco (triagem e encaminhamentos).

---

### Credenciais obrigatórias para IA

Para executar o endpoint `/api/mindcheck-ai/analises` é necessário configurar, no arquivo `.env`, as credenciais da Azure OpenAI utilizadas pelo Spring AI:

```env
AZURE_OPENAI_API_KEY=<sua-chave>
AZURE_OPENAI_ENDPOINT=https://<resource>.openai.azure.com/
AZURE_OPENAI_DEPLOYMENT=<deployment-gpt4o-ou-outro>
```

Sem esses valores, o `ChatClient` não consegue gerar o diagnóstico automatizado.

**Como provisionar no Azure**
1. No portal Azure, crie um recurso "Foundry" (Azure AI Foundry / AI Studio), escolhendo uma região disponível e um Resource Group.
2. Dentro do recurso, acesse `Keys & Endpoint` para copiar o `Endpoint` (`AZURE_OPENAI_ENDPOINT`) e gerar a chave (`AZURE_OPENAI_API_KEY`).
3. Na aba `Deployments`, crie um novo deployment para o modelo desejado (ex.: GPT-4o) e use o nome definido como o valor da variável `AZURE_OPENAI_DEPLOYMENT`.
4. Para mais detalhes, consulte a documentação oficial: [Criar recurso](https://learn.microsoft.com/azure/ai-services/openai/how-to/create-resource).

### Variáveis de ambiente

| Variável | Descrição | Obrigatório |
|---|---|---|
| `DB_USERNAME` / `DB_PASSWORD` | Credenciais do banco MySQL | Sim |
| `JWT_SECRET` | Chave para assinar tokens JWT | Sim |
| `AZURE_OPENAI_API_KEY` | Chave de acesso à Azure OpenAI | Sim |
| `AZURE_OPENAI_ENDPOINT` | Endpoint do recurso Azure OpenAI | Sim |
| `AZURE_OPENAI_DEPLOYMENT` | Nome do deployment do modelo (ex.: gpt-4o) | Sim |
| `REDIS_HOST` / `REDIS_PORT` | Host e porta do Redis (default: `localhost`/`6379`) | Não |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | Host e porta do RabbitMQ (default: `localhost`/`5672`) | Não |
| `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | Credenciais do broker (default: `guest`/`guest`) | Não |
| `MINDCHECK_EXCHANGE` / `MINDCHECK_QUEUE` / `MINDCHECK_ROUTING_KEY` | Identificadores das filas utilizadas | Não |

> Azure OpenAI e Redis são obrigatórios. RabbitMQ e demais variáveis já possuem defaults e só precisam ser configurados se o ambiente diferir do padrão local.

### Infraestrutura local (Docker)

Para desenvolvimento local, os serviços de infraestrutura podem ser iniciados com Docker:

```bash
# Redis (obrigatório)
docker run -d --name mindcheck-redis -p 6379:6379 redis:7

# RabbitMQ (opcional — apenas se quiser testar a mensageria)
docker run -d --name mindcheck-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### Mensageria assíncrona

- Cada triagem da MindCheck AI publica um `TriagemAvaliacaoEvent` no RabbitMQ (`mindcheck.triagem.exchange`).
- O listener `MindCheckAiEventListener` consome os eventos para processar alertas/dashboards sem bloquear a requisição.
- Caso não queira iniciar o RabbitMQ localmente, defina `mindcheck.rabbitmq.enabled=false` no `application.properties` (já é o valor padrão) para desabilitar todos os beans de mensageria.

#### Como testar a mensageria
1. Inicie o RabbitMQ com o comando acima (usuário `guest/guest`).
2. Defina `mindcheck.rabbitmq.enabled=true` no `application.properties`.
3. Rode a API (`mvn spring-boot:run`) e acesse `http://localhost:15672` para confirmar a fila `mindcheck.triagem.queue`.
4. Autentique-se e chame `POST /api/mindcheck-ai/analises` com payload válido.
5. No painel do RabbitMQ, verifique que a fila recebeu a mensagem e logo ficou vazia (listener consumiu).
6. Veja os logs da aplicação: `MindCheckAiEventListener` deve registrar o risco e as especialidades recomendadas.

---

## Tecnologias e Ferramentas Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring AI + Azure OpenAI (GPT-4o)** – análise automática de relatos com temperatura calibrada (`0.2`)
- **Spring Security + JWT** – autenticação e autorização
- **Spring Data JPA** com **MySQL**
- **Redis** – rate limiting por usuário e cache de idempotência das análises
- **Spring AMQP (RabbitMQ)** – mensageria assíncrona
- **MapStruct** – mapeamento DTO ↔ entidade
- **Springdoc OpenAPI / Swagger UI** – documentação interativa
- **Maven**

### Módulos Spring Utilizados

- `Spring Web` – construção da API RESTful
- `Spring Data JPA` – persistência de dados
- `Spring Data Redis` – integração com Redis para cache e rate limiting
- `Spring Validation` – validação das requisições
- `Spring Security` – autenticação e autorização com JWT
- `Spring HATEOAS` – enriquecimento hipertextual das respostas
- `Springdoc OpenAPI` – documentação automática da API

### Persistência de Dados

- **MySQL** – banco de dados relacional utilizado no projeto

---

## Documentação e Testes da API

- **Swagger / OpenAPI** – documentação interativa
- **Swagger UI** – testes manuais diretamente no browser
- **Postman** – testes externos

A documentação estará disponível em:

👉 `http://localhost:8080/swagger-ui/index.html`

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- MySQL
- Redis
- Criar arquivo `.env` na raiz do projeto contendo:

```env
# Banco de dados
DB_USERNAME=
DB_PASSWORD=

# Segurança
JWT_SECRET=

# Azure OpenAI (obrigatório para o endpoint de IA)
AZURE_OPENAI_API_KEY=
AZURE_OPENAI_ENDPOINT=
AZURE_OPENAI_DEPLOYMENT=

# Redis (opcional — padrão: localhost:6379)
REDIS_HOST=
REDIS_PORT=

# RabbitMQ (opcional — padrão: localhost:5672 / guest:guest)
RABBITMQ_HOST=
RABBITMQ_PORT=
RABBITMQ_USERNAME=
RABBITMQ_PASSWORD=
MINDCHECK_EXCHANGE=
MINDCHECK_QUEUE=
MINDCHECK_ROUTING_KEY=
```

---

## ▶️ Como Executar

```bash
# Clone o repositório
git clone https://github.com/thiag0renatino/mindcare-ai.git
cd mindcare-ai

# Compile o projeto
mvn clean install

# Execute a aplicação
mvn spring-boot:run
```

API disponível em:
👉 `http://localhost:8080`

---

## Endpoints principais

- **Auth**
  - `POST /auth/register` → Registra novo usuário
  - `POST /auth/login` → Autentica usuário e gera token JWT
  - `PUT /auth/refresh-token` → Gera novo token a partir do refresh token
  - `POST /auth/logout` → Invalida o token atual via Redis
- **MindCheck AI**
  - `POST /api/mindcheck-ai/analises` → Requer Bearer token; executa o fluxo de IA, persiste a triagem, cria um encaminhamento por especialidade sugerida e retorna o resultado completo.
- Demais recursos (Triagem, Encaminhamento, Acompanhamento, Empresa etc.) estão detalhados no Swagger e também exigem autenticação JWT.

---

## Licença

Projeto – FIAP Global Solution 2025.
