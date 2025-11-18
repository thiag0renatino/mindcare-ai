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

- **Triagem inteligente**, interpretando relatos de colaboradores (texto/voz) e classificando risco como baixo, moderado ou alto;
- **Armazenamento completo do histórico** de triagens, encaminhamentos e acompanhamentos;
- **API RESTful** moderna com Java Spring Boot, seguindo boas práticas (DTOs, serviços, validações, exceções globais etc.);
- **Integração futura com IA**, responsável por interpretar relatos e sugerir ações;
- **Estrutura organizada** de entidades relacionadas (Empresa, Usuário, Skill, Triagem, Encaminhamento, Acompanhamento).

📌 **Público-alvo:** Departamentos de RH, profissionais de saúde e equipes responsáveis pelo bem-estar corporativo.

---

## Tecnologias e Ferramentas Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Maven**
- **JWT** (autenticação)
- **MapStruct** (mapeamento automático DTO ↔ entidade)

### Módulos Spring Utilizados

- `Spring Web` – construção da API RESTful
- `Spring Data JPA` – persistência de dados
- `Spring Validation` – validação das requisições
- `Spring Security` – autenticação e autorização com JWT
- `Spring HATEOAS` – enriquecimento hipertextual das respostas (em implementação)
- `Springdoc OpenAPI` – documentação automática da API

### Persistência de Dados

- **Oracle Database** – ambiente oficial do projeto
- **H2 Database** – disponível para perfil de desenvolvimento (opcional)

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



### 🔑 Auth Controller
- `POST /auth/login` → Autentica usuário e gera token JWT
- `POST /auth/register` → Registra novo usuário
- `PUT /auth/refresh-token` → Gera novo token a partir do refresh

---

## Banco de Dados

- **Oracle Database** (principal)
- Configurações em `application.properties`
- Estrutura e scripts SQL incluídos no projeto (em /database)

---

## Alunos

- **Thiago Renatino Paulino** — RM556934
- **Cauan Matos Moura** — RM558821
- **Gustavo Roberto** — RM558033

---

## Licença

Projeto acadêmico – FIAP Global Solution 2025.
