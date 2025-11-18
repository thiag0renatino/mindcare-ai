# mindcare-ai

> API RESTful para triagem inteligente, monitoramento de bem-estar e gestão corporativa de saúde utilizando IA.

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
- **Integração ativa com IA**, responsável por interpretar relatos e sugerir ações;
- **Estrutura organizada** de entidades relacionadas (Empresa, Usuário, Triagem, Encaminhamento, Acompanhamento).

📌 **Público-alvo:** Departamentos de RH, profissionais de saúde e equipes responsáveis pelo bem-estar corporativo.

---

## Como a MindCheck AI está funcionando

O fluxo de triagem inteligente utiliza o Spring AI com Azure OpenAI (configuradas via variáveis `AZURE_OPENAI_*`) e funciona da seguinte forma:

1. **Endpoint protegido** `POST /api/mindcheck-ai/analises` recebe o `usuarioId`, relato e dados opcionais sobre sintomas, humor e rotina.
2. **Prompting estruturado**: o `MindCheckAiService` monta uma instrução fixa para o modelo gerar um JSON contendo `risco`, `sugestoes`, `encaminhamentos` e `justificativa` — qualquer resposta inválida dispara uma `MindCheckAiException`.
3. **Persistência automática**: a resposta é convertida em `MindCheckAiResponseDTO`, uma nova `Triagem` é salva e, quando o risco é `MODERADO` ou `ALTO`, um `Encaminhamento` é criado com prioridade proporcional ao risco.
4. **Retorno completo**: o payload da IA já vem acrescido dos dados da triagem persistida e, se houver, do encaminhamento gerado.

Esse fluxo garante que toda análise realizada pela IA deixe rastros no banco (triagem e encaminhamento).

---

### Credenciais obrigatórias para IA

Para executar o endpoint `/api/mindcheck-ai/analises` é necessário configurar, no arquivo `.env`, as credenciais da Azure OpenAI utilizadas pelo Spring AI:

```env
AZURE_OPENAI_API_KEY=<sua-chave>
AZURE_OPENAI_ENDPOINT=https://<resource>.openai.azure.com/
AZURE_OPENAI_DEPLOYMENT=<deployment-gpt4o-ou-outro>
```

Sem esses valores, o `ChatClient` não consegue gerar o diagnóstico automatizado.

### Variáveis de ambiente necessárias

| Variável | Descrição |
| --- | --- |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciais do banco Oracle |
| `JWT_SECRET` | Chave para assinar tokens JWT |
| `AZURE_OPENAI_API_KEY` | Chave da Azure OpenAI usada pelo Spring AI |
| `AZURE_OPENAI_ENDPOINT` | Endpoint do recurso Azure OpenAI |
| `AZURE_OPENAI_DEPLOYMENT` | Deployment (modelo) habilitado no Azure OpenAI |

---

## Tecnologias e Ferramentas Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring AI + Azure OpenAI (GPT-4o)** – análise automática de relatos
- **Spring Security + JWT** – autenticação/autorização
- **Spring Data JPA** com **Oracle** (prod) e **H2** (dev)
- **MapStruct** – mapeamento DTO ↔ entidade
- **Springdoc OpenAPI / Swagger UI**
- **Maven**

### Módulos Spring Utilizados

- `Spring Web` – construção da API RESTful
- `Spring Data JPA` – persistência de dados
- `Spring Validation` – validação das requisições
- `Spring Security` – autenticação e autorização com JWT
- `Spring HATEOAS` – enriquecimento hipertextual das respostas (em implementação)
- `Springdoc OpenAPI` – documentação automática da API

### Persistência de Dados

- **Oracle Database** – ambiente oficial do projeto

---

## Documentação e Testes da API

- **Swagger / OpenAPI** – documentação interativa
- **Swagger UI** – testes manuais
- **Postman** – testes externos

A documentação estará disponível em:

👉 `http://localhost:8080/swagger-ui/index.html`

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Oracle Database
- Criar arquivo `.env` na raiz do projeto contendo:

```env
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
AZURE_OPENAI_API_KEY=
AZURE_OPENAI_ENDPOINT=
AZURE_OPENAI_DEPLOYMENT=
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
  - `PUT /auth/refresh-token` → Gera novo token a partir do refresh
- **MindCheck AI**
  - `POST /api/mindcheck-ai/analises` → Requer Bearer token; chama o fluxo de IA, insere triagem/encaminhamento e retorna o resultado completo.
- Demais recursos (Triagem, Encaminhamento, Empresa etc.) estão detalhados no Swagger e também exigem autenticação JWT.

---


## Alunos

- **Thiago Renatino Paulino** — RM556934
- **Cauan Matos Moura** — RM558821
- **Gustavo Roberto** — RM558033

---

## Licença

Projeto acadêmico – FIAP Global Solution 2025.
