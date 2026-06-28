# 🦐 Camarada Camarão — Sistema de Gestão de Restaurante

Sistema web completo para gestão de um restaurante, com controle de cardápio, pedidos, estoque, funcionários e feedback de clientes. Composto por uma API REST em Java (Spring Boot) e uma interface web em React, orquestrados via Docker Compose.

---

## Sumário

- [Tecnologias](#tecnologias)
- [Arquitetura do Projeto](#arquitetura-do-projeto)
- [Banco de Dados](#banco-de-dados)
- [API — Endpoints](#api--endpoints)
- [Autenticação](#autenticação)
- [Segurança](#segurança)
- [Controle de Acesso por Perfil](#controle-de-acesso-por-perfil)
- [Requisitos](#requisitos)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Como Rodar Localmente](#como-rodar-localmente)
- [Estrutura de Pastas](#estrutura-de-pastas)

---

## Tecnologias

### Backend
| Tecnologia | Versão | Função |
|---|---|---|
| Java | 26 | Linguagem principal |
| Spring Boot | 4.1.0 | Framework web |
| Spring Data JPA | — | ORM / acesso ao banco |
| Spring Security | — | Autenticação e autorização |
| Spring Validation | — | Validação de dados de entrada |
| Lombok | 1.18.46 | Redução de boilerplate |
| MySQL Connector/J | — | Driver JDBC |
| Maven | — | Gerenciador de dependências |

### Frontend
| Tecnologia | Versão | Função |
|---|---|---|
| React | 19 | Biblioteca de UI |
| Vite | 8 | Bundler e servidor de dev |
| React Router DOM | 7 | Roteamento client-side |
| Axios | 1.x | Requisições HTTP |
| Vanilla CSS | — | Estilização |

### Infraestrutura
| Tecnologia | Função |
|---|---|
| Docker | Containerização |
| Docker Compose | Orquestração dos serviços |
| MySQL 8.4 | Banco de dados relacional |
| Nginx | Servidor do frontend em produção |

---

## Arquitetura do Projeto

O projeto segue uma arquitetura **monorepo com três serviços independentes** orquestrados pelo Docker Compose:

```
┌─────────────────────────────────────────────────────┐
│                   Docker Compose                    │
│                                                     │
│  ┌──────────┐    ┌──────────────┐    ┌──────────┐  │
│  │  MySQL   │◄───│  Spring Boot │◄───│  React   │  │
│  │  :3306   │    │  API :8080   │    │  :3000   │  │
│  └──────────┘    └──────────────┘    └──────────┘  │
└─────────────────────────────────────────────────────┘
```

O backend segue a **arquitetura em camadas** padrão do Spring:

```
controller  ←  recebe as requisições HTTP e delega para os serviços
service     ←  contém toda a lógica de negócio
repository  ←  interface com o banco via Spring Data JPA
model       ←  entidades JPA (mapeamento objeto-relacional)
dto         ←  objetos de transferência de dados (entrada e saída)
config      ←  segurança, CORS e tratamento global de erros
```

---

## Banco de Dados

O sistema utiliza **8 tabelas** com relacionamentos `@ManyToOne`, `@ManyToMany` e tabelas associativas explícitas:

| Tabela | Descrição |
|---|---|
| `users` | Clientes, atendentes e administradores (perfil unificado com coluna `role`) |
| `menu_items` | Itens do cardápio com preço e status ativo/inativo |
| `ingredients` | Ingredientes disponíveis |
| `stock` | Lotes de ingredientes em estoque com validade |
| `orders` | Pedidos realizados pelos clientes |
| `order_items` | Tabela associativa entre pedido e item do cardápio (com quantidade e preço snapshot) |
| `menu_item_ingredients` | Tabela associativa entre item do cardápio e ingredientes |
| `feedbacks` | Avaliações dos clientes vinculadas a um pedido e a um usuário |

### Enums

| Enum | Valores |
|---|---|
| `Role` | `CUSTOMER`, `ATTENDANT`, `ADMINISTRATOR` |
| `Gender` | `MALE`, `FEMALE`, `OTHER` |
| `OrderStatus` | `PENDING`, `PREPARING`, `DELIVERED`, `CANCELLED` |

---

## API — Endpoints

Todas as rotas são prefixadas com `/api`.

### Usuários — `/api/users`
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/api/users/register` | Cadastro de novo cliente | Público |
| `GET` | `/api/users/me` | Perfil do usuário autenticado | Qualquer autenticado |
| `GET` | `/api/users/attendants` | Listar atendentes | ADMINISTRATOR |
| `POST` | `/api/users/attendants` | Criar atendente | ADMINISTRATOR |
| `PUT` | `/api/users/attendants/{id}` | Atualizar atendente | ADMINISTRATOR |
| `DELETE` | `/api/users/attendants/{id}` | Remover atendente | ADMINISTRATOR |

### Cardápio — `/api/menu`
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/api/menu` | Listar itens ativos do cardápio | Público |
| `GET` | `/api/menu/{id}` | Detalhe de um item | Público |
| `POST` | `/api/menu` | Criar item | ADMINISTRATOR |
| `PUT` | `/api/menu/{id}` | Atualizar item | ADMINISTRATOR |
| `DELETE` | `/api/menu/{id}` | Desativar item (soft delete) | ADMINISTRATOR |

### Pedidos — `/api/orders`
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/api/orders` | Fazer um pedido | CUSTOMER |
| `GET` | `/api/orders` | Listar pedidos ativos | ATTENDANT, ADMINISTRATOR |
| `GET` | `/api/orders/history` | Histórico completo de pedidos | ADMINISTRATOR |
| `GET` | `/api/orders/mine` | Meus pedidos | CUSTOMER |
| `PUT` | `/api/orders/{id}/status` | Atualizar status do pedido | ATTENDANT, ADMINISTRATOR |

### Estoque — `/api/stock`
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/api/stock` | Listar estoque | ADMINISTRATOR |
| `POST` | `/api/stock` | Adicionar lote | ADMINISTRATOR |
| `PUT` | `/api/stock/{id}` | Atualizar lote | ADMINISTRATOR |

### Ingredientes — `/api/ingredients`
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/api/ingredients` | Listar ingredientes | ATTENDANT, ADMINISTRATOR |
| `POST` | `/api/ingredients` | Criar ingrediente | ADMINISTRATOR |

### Feedback — `/api/feedbacks`
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `POST` | `/api/feedbacks` | Enviar avaliação | CUSTOMER |
| `GET` | `/api/feedbacks` | Listar todas as avaliações | ADMINISTRATOR |

### Relatórios — `/api/reports`
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| `GET` | `/api/reports/top-items` | Itens mais vendidos (mês/ano) | ADMINISTRATOR |
| `GET` | `/api/reports/least-items` | Itens menos vendidos | ADMINISTRATOR |

---

## Autenticação

A API usa **JWT (JSON Web Token)**. 

O fluxo na prática:

1. `POST /api/auth/login` com `{ "email": "...", "password": "..." }`
2. Se as credenciais baterem, a API devolve `{ "token": "eyJ...", "role": "...", "email": "..." }`
3. O frontend guarda só o token no `sessionStorage` (a senha some depois do login)
4. A partir daí, cada requisição protegida vai com o cabeçalho:

```
Authorization: Bearer <token>
```

O token dura **8 horas** por padrão. Dá pra mudar isso pela variável `JWT_EXPIRATION_MS` no `backend/.env`.

### Formato de erro padrão

Todo erro da API segue a mesma estrutura, independente de onde veio:

```json
{
  "status": 400,
  "error": "Descrição do erro",
  "details": {
    "campo": "mensagem de validação"
  }
}
```

---

## Segurança

Antes era **HTTP Basic Auth** — o tipo mais simples de autenticação, onde o `email:senha` vai codificado em Base64 em *toda* requisição. Funciona, mas tem problemas sérios: a senha fica trafegando o tempo todo e qualquer script malicioso na página consegue ler as credenciais do `sessionStorage`.

Durante o desenvolvimento, a autenticação foi migrada pra JWT e outras melhorias foram feitas junto. Segue o que mudou e por quê:

**Migração para JWT**  
A senha só vai pro servidor uma vez — no login. O servidor devolve um token assinado com HMAC-SHA256. Nas próximas requisições, só o token circula. Se o token vazar (improvável, mas possível), ele expira em 8 horas e não carrega a senha em si.

**Senha nunca aparece nas respostas da API**  
Os controllers antes retornavam as entidades JPA direto — incluindo o objeto `User` com o campo `password` (mesmo que hashado, não é informação que o cliente precisa ver). Foi adicionado `@JsonIgnore` no campo e todos os controllers foram ajustados pra devolver DTOs de resposta dedicados. Agora é impossível a senha vazar em algum endpoint de pedidos ou feedbacks.

**Validação de dados de entrada**  
O cadastro de usuário passou a ter validações tanto no frontend quanto no backend. CPF aceita só números, telefone tem limite de 11 dígitos, senha exige mínimo 6 caracteres com ao menos uma letra maiúscula. Se alguém tentar mandar uma data mal-formatada pro servidor, em vez de um erro 500 genérico, volta um 400 com mensagem clara. Essa camada extra existe porque validação só no front não é suficiente — qualquer pessoa com Postman consegue bypassar.

**Transições de status de pedido controladas**  
Antes era possível mover um pedido de qualquer status pra qualquer outro — incluindo coisas sem sentido como `DELIVERED → PENDING`. Agora existe uma máquina de estados simples: `PENDING → PREPARING → DELIVERED`, com possibilidade de cancelar em `PENDING` ou `PREPARING`. Tentar ir na direção contrária retorna 400.

**Feedback só depois da entrega**  
Um cliente conseguia enviar avaliação de um pedido que ainda estava sendo preparado. Isso foi corrigido — o sistema agora valida se o pedido está com status `DELIVERED` antes de aceitar o feedback.

**Separação de responsabilidades nos services**  
Alguns services estavam acessando repositórios de outros domínios diretamente (ex: `OrderService` injetando `UserRepository`). Isso foi ajustado pra usar os services correspondentes — `OrderService` chama `UserService`. Parece detalhe, mas facilita bastante na hora de adicionar lógica de negócio em um lugar só.

**Secret JWT via variável de ambiente**  
O `JWT_SECRET` nunca está no código. Ele vem do `backend/.env` e precisa ter pelo menos 32 caracteres. O valor padrão no repositório é um placeholder — troque antes de usar.

---

## Controle de Acesso por Perfil

| Perfil | Capacidades |
|---|---|
| **CUSTOMER** | Visualizar cardápio, fazer pedidos, acompanhar seus pedidos, enviar feedback |
| **ATTENDANT** | Visualizar e atualizar status dos pedidos ativos, visualizar ingredientes |
| **ADMINISTRATOR** | Acesso total — gerencia cardápio, estoque, ingredientes, atendentes, feedbacks e relatórios |

> O cadastro público cria sempre um usuário com perfil `CUSTOMER`. Atendentes e administradores são criados manualmente por um administrador.

---

## Requisitos

Para rodar o projeto **com Docker** (recomendado):
- [Docker](https://docs.docker.com/get-docker/) 24+
- [Docker Compose](https://docs.docker.com/compose/install/) 2.x

Para rodar o **frontend em modo de desenvolvimento** (sem Docker):
- [Node.js](https://nodejs.org/) 20+

---

## Variáveis de Ambiente

O projeto usa arquivos `.env` para todas as configurações sensíveis. Nenhuma senha ou URL está hardcoded no código.

### `.env` (raiz do projeto) — usado pelo Docker Compose
```env
# Senha do root do MySQL
DB_ROOT_PASSWORD=sua_senha_aqui

# Nome do banco de dados
DB_NAME=restaurant_db

# Porta exposta do MySQL no host
DB_PORT=3306

# Porta da API Spring Boot no host
API_PORT=8080

# Porta do frontend no host
WEB_PORT=3000
```

### `backend/.env` — usado pelo container da API
```env
# URL de conexão com o banco (use "mysql" como host dentro do Docker)
DB_URL=jdbc:mysql://mysql:3306/restaurant_db

DB_USERNAME=root
DB_PASSWORD=sua_senha_aqui

SERVER_PORT=8080

# Origin permitida pelo CORS (URL do frontend)
CORS_ALLOWED_ORIGIN=http://localhost:3000

# Senha do usuário administrador criado automaticamente no seed
ADMIN_PASSWORD=senha_admin

# Senhas dos usuários de seed (atendente e cliente de exemplo)
ATTENDANT_PASSWORD=senha_atendente
CUSTOMER_PASSWORD=senha_cliente

# JWT — substitua JWT_SECRET por uma string aleatória forte (mín. 32 caracteres)
JWT_SECRET=sua-chave-secreta-aqui-minimo-32-chars
JWT_EXPIRATION_MS=28800000
```

### `frontend/.env` — usado em desenvolvimento local (sem Docker)
```env
# URL base da API
VITE_API_URL=http://localhost:8080/api
```

> Os arquivos `.env.example` com os campos sem valor estão disponíveis em cada diretório como templates.

---

## Como Rodar Localmente

### 1. Clone o repositório

```bash
git clone https://github.com/Liperasz/restaurant-management-system.git
cd restaurant-management-system
```

### 2. Configure as variáveis de ambiente

Copie os templates e preencha com seus valores:

```bash
# Raiz do projeto (usado pelo Docker Compose)
cp .env.example .env

# Backend (API Spring Boot)
cp backend/.env.example backend/.env

# Frontend (apenas para desenvolvimento sem Docker)
cp frontend/.env.example frontend/.env
```

Edite cada arquivo `.env` e defina as senhas conforme necessário.

### 3. Suba os serviços com Docker Compose

```bash
docker compose up --build
```

Isso irá:
1. Subir o banco de dados MySQL 8.4 com volume persistente
2. Compilar e iniciar a API Spring Boot (aguarda o MySQL estar saudável)
3. Compilar o frontend React e servir via Nginx

### 4. Acesse a aplicação

| Serviço | URL padrão |
|---|---|
| Frontend (interface web) | http://localhost:3000 |
| API REST | http://localhost:8080 |
| Banco de dados (MySQL) | localhost:3306 |

> As portas podem ser alteradas no arquivo `.env` da raiz via `WEB_PORT`, `API_PORT` e `DB_PORT`.

---

### Rodando o Frontend em modo de desenvolvimento (opcional)

Caso queira hot-reload no frontend sem precisar rebuildar o Docker:

```bash
# Com o backend rodando via Docker Compose:
docker compose up mysql api

# Em outro terminal, rode o frontend localmente:
cd frontend
npm install
npm run dev
```

O servidor de desenvolvimento ficará disponível em `http://localhost:5173`.

---

### Seed de dados

Ao iniciar pela primeira vez, a API cria automaticamente um conjunto de dados iniciais:
- Usuário **administrador** (email: `admin@camaradacamarao.com`)
- Usuário **atendente** de exemplo
- Usuário **cliente** de exemplo
- Itens de cardápio e ingredientes de exemplo

As senhas de cada usuário seed são configuradas nas variáveis `ADMIN_PASSWORD`, `ATTENDANT_PASSWORD` e `CUSTOMER_PASSWORD` no `backend/.env`.

---

## Estrutura de Pastas

```
restaurant-management-system/
├── backend/                          # API Spring Boot
│   ├── src/main/java/com/camaradacamarao/api/
│   │   ├── config/                   # SecurityConfig, CorsConfig, GlobalExceptionHandler
│   │   ├── controller/               # Controllers REST (UserController, OrderController, ...)
│   │   ├── dto/                      # DTOs de entrada e saída
│   │   ├── model/                    # Entidades JPA
│   │   │   └── enums/                # Role, Gender, OrderStatus
│   │   ├── repository/               # Interfaces Spring Data JPA
│   │   └── service/                  # Lógica de negócio
│   ├── src/main/resources/
│   │   └── application.properties    # Configuração do Spring (lê variáveis de ambiente)
│   ├── .env.example                  # Template de variáveis de ambiente
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                         # Interface React
│   ├── src/
│   │   ├── api/                      # Instância do Axios com interceptor de autenticação
│   │   ├── components/               # Componentes reutilizáveis (Navbar, ProtectedRoute, ...)
│   │   ├── context/                  # AuthContext (login, logout, perfil do usuário)
│   │   └── pages/
│   │       ├── Login.jsx             # Tela de login
│   │       ├── Register.jsx          # Cadastro de novo cliente
│   │       ├── Menu.jsx              # Cardápio público
│   │       ├── admin/                # Gerenciar cardápio, estoque, funcionários, relatórios
│   │       ├── attendant/            # Pedidos ativos e atualização de status
│   │       └── customer/             # Fazer pedido, meus pedidos, feedback
│   ├── .env.example
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
├── .env.example                      # Template para Docker Compose
├── .gitignore
└── docker-compose.yml                # Orquestração: MySQL + API + Frontend
```
