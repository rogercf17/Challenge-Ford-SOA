# Challenge Ford FIAP 2026 — API de Inteligência Competitiva Automotiva

API RESTful desenvolvida em **Java 21 + Spring Boot** para o **Desafio 01 — Inteligência Competitiva Automotiva** Ford FIAP 2026.

A solução permite cadastrar veículos concorrentes, gerenciar suas especificações técnicas e gerar tabelas comparativas padronizadas — respondendo à necessidade da Ford de compreender o posicionamento da concorrência em termos de equipamentos e atributos técnicos.

---

## Equipe
| Nome | RM |
|---|---|
| Artur Alves Tenca | 555171 |
| Igor Brunelli Ralo | 555035 |
| João Pedro Signor Avelar | 558375 |
| Roger Cardoso Ferreira | 557230 |
| Victor Mattenhauer Lopes | 555753 |

---

## Arquitetura

```
Cliente (Swagger UI / App Mobile / Postman)
        │
        ▼
┌─────────────────────┐
│   Controller Layer  │  ← VeiculoController, EspecificacaoController
│  (REST Endpoints)   │
└────────┬────────────┘
         │
┌────────▼────────────┐
│   Service Layer     │  ← VeiculoService, EspecificacaoService
│  (Regras de negócio)│
└────────┬────────────┘
         │
┌────────▼────────────┐
│  Repository Layer   │  ← VeiculoRepository, EspecificacaoRepository
│  (Spring Data JPA)  │
└────────┬────────────┘
         │
┌────────▼────────────┐
│   PostgreSQL 16     │  ← Tabelas: veiculo, especificacao
│ (Flyway Migrations) │
└─────────────────────┘
```

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.6 | Framework web |
| Spring Data JPA | — | Persistência ORM |
| Spring Validation | — | Validação de entradas |
| PostgreSQL | 16+ | Banco de dados relacional |
| Flyway | — | Controle de migrações |
| Lombok | — | Redução de boilerplate |
| SpringDoc OpenAPI | 2.8.8 | Documentação Swagger |

---

## Pré-requisitos

- **Java 21** ou superior
- **Maven 3.9+** (ou usar o `./mvnw` incluso)
- **PostgreSQL 16+** rodando localmente na porta `5432`

---

## Configuração do Banco de Dados

Crie o banco antes de subir a aplicação:

```sql
CREATE DATABASE ford_challenge;
```

As configurações de conexão ficam em `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ford_challenge
spring.datasource.username=postgres
spring.datasource.password=123456
```

> **Obs.:** altere `username` e `password` conforme sua instalação local do PostgreSQL.

As tabelas são criadas automaticamente pelo **Flyway** na primeira execução:

| Migração | Descrição |
|---|---|
| `V1__create_tables.sql` | Cria as tabelas `veiculo` e `especificacao` com índices |
| `V2__seed_ranger_raptor.sql` | Insere a Ford Ranger Raptor 2024 como dado de validação |

---

## Como executar

```bash
# Clone o repositório
git clone <url-do-repositorio>
cd restapi

# Execute com Maven Wrapper (sem precisar instalar Maven)
./mvnw spring-boot:run

# Ou, no Windows:
mvnw.cmd spring-boot:run
```

A aplicação sobe em: `http://localhost:8080`

---

## Documentação da API (Swagger)

Após subir a aplicação, acesse:

| Interface | URL |
|---|---|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/api-docs |

---

## Endpoints

### Veículos — `/api/v1/veiculos`

| Método | Endpoint | Descrição | Status de sucesso |
|---|---|---|---|
| `POST` | `/api/v1/veiculos` | Cadastrar novo veículo | `201 Created` |
| `GET` | `/api/v1/veiculos` | Listar todos (paginado) | `200 OK` |
| `GET` | `/api/v1/veiculos/{id}` | Buscar por ID | `200 OK` |
| `GET` | `/api/v1/veiculos/buscar` | Buscar por marca/modelo/versão | `200 OK` |
| `PUT` | `/api/v1/veiculos/{id}` | Atualizar veículo completo | `200 OK` |
| `DELETE` | `/api/v1/veiculos/{id}` | Remover veículo | `204 No Content` |
| `GET` | `/api/v1/veiculos/comparar?ids=1,2` | Comparar veículos lado a lado | `200 OK` |

### Especificações — `/api/v1/veiculos/{veiculoId}/especificacoes`

| Método | Endpoint | Descrição | Status de sucesso |
|---|---|---|---|
| `GET` | `/{veiculoId}/especificacoes` | Listar todas as especificações do veículo | `200 OK` |
| `GET` | `/{veiculoId}/especificacoes/{especId}` | Buscar especificação por ID | `200 OK` |
| `POST` | `/{veiculoId}/especificacoes` | Adicionar/atualizar especificação (upsert) | `201 Created` |
| `DELETE` | `/{veiculoId}/especificacoes/{especId}` | Remover especificação | `204 No Content` |

---

## Exemplos de Request/Response

### Cadastrar veículo

**Request:**
```http
POST /api/v1/veiculos
Content-Type: application/json

{
  "marca": "Toyota",
  "modelo": "Hilux",
  "versao": "GR-Sport",
  "ano": 2024
}
```

**Response `201`:**
```json
{
  "id": 2,
  "marca": "Toyota",
  "modelo": "Hilux",
  "versao": "GR-Sport",
  "ano": 2024,
  "especificacoes": []
}
```

### Adicionar especificação

**Request:**
```http
POST /api/v1/veiculos/2/especificacoes
Content-Type: application/json

{
  "atributo": "Motor",
  "valor": "2.8 Diesel Turbo"
}
```

**Response `201`:**
```json
{
  "id": 1,
  "atributo": "Motor",
  "valor": "2.8 Diesel Turbo"
}
```

### Comparar veículos

**Request:**
```http
GET /api/v1/veiculos/comparar?ids=1,2
```

**Response `200`:**
```json
{
  "veiculos": ["Ford Ranger Raptor 2024", "Toyota Hilux GR-Sport 2024"],
  "atributos": {
    "Motor": ["2.0 Bi-Turbo EcoBlue", "2.8 Diesel Turbo"],
    "Potência (cv)": ["213 cv", "Não disponível"]
  }
}
```

---

## Tratamento de Erros

A API retorna erros no formato padronizado:

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Veículo não encontrado com id: 99",
  "timestamp": "2026-05-15T10:30:00"
}
```

| Código | Situação |
|---|---|
| `400 Bad Request` | Dados de entrada inválidos (validação) |
| `404 Not Found` | Recurso não encontrado |
| `409 Conflict` | Veículo já cadastrado (marca+modelo+versão duplicados) |
| `422 Unprocessable Entity` | Violação de regra de negócio |
| `500 Internal Server Error` | Erro inesperado no servidor |

---

## Estrutura do Projeto

```
src/main/java/com/example/challenge/restapi/
├── config/
│   └── OpenApiConfig.java          # Configuração Swagger/OpenAPI
├── controller/
│   ├── VeiculoController.java      # Endpoints de veículos
│   └── EspecificacaoController.java # Endpoints de especificações
├── DTOs/
│   ├── request/
│   │   ├── VeiculoRequest.java
│   │   └── EspecificacaoRequest.java
│   └── response/
│       ├── VeiculoResponse.java
│       ├── EspecificacaoResponse.java
│       └── ComparacaoResponse.java
├── exception/
│   ├── GlobalExceptionHandler.java  # Tratamento centralizado de erros
│   ├── BusinessException.java
│   └── ResourceNotFoundException.java
├── model/
│   ├── Veiculo.java
│   └── Especificacao.java
├── repository/
│   ├── VeiculoRepository.java
│   └── EspecificacaoRepository.java
├── service/
│   ├── VeiculoService.java
│   └── EspecificacaoService.java
└── RestapiApplication.java

src/main/resources/
├── application.properties
└── db/migration/
    ├── V1__create_tables.sql
    └── V2__seed_ranger_raptor.sql
```

---

## Validação da Solução

O dado de validação proposto no desafio (Ford Ranger Raptor) é inserido automaticamente pela migração `V2`. Para verificar:

```http
GET /api/v1/veiculos/1
```

Deve retornar todas as 34 especificações técnicas da Ranger Raptor 2024.
