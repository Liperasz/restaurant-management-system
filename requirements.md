# Especificações Técnicas do Projeto Final

Este documento descreve os requisitos técnicos obrigatórios e as diretrizes arquiteturais para o desenvolvimento do projeto final da disciplina de Paradigmas.

## 1. Tecnologias Base (Backend)
- **Linguagem:** Java (JDK 26)
- **Framework Principal:** Spring Boot
- **Gerenciador de Dependências:** Maven
- **Banco de Dados Relacional:** PostgreSQL (Fortemente recomendado pelo professor, pois os exemplos e aulas serão baseados nele). Contudo, o uso de outros SGBDs (como MySQL) é permitido, a critério da equipe.

## 2. Dependências Obrigatórias (Spring Initializr)
- Spring Web
- Spring Data JPA
- PostgreSQL Driver *(Nota: Caso a equipe opte por outro banco de dados, como MySQL, é necessário substituir esta dependência pelo driver correspondente)*
- Lombok

## 3. Segurança e Autenticação
- O sistema deve possuir um mecanismo de autenticação obrigatório.
- **Protocolos Aceitos:** Basic Auth (Foco das explicações do professor) ou JWT (JSON Web Token). A implementação de pelo menos um dos dois é obrigatória.
- Rotas da API devem ser protegidas, exigindo registro e login prévio para acesso aos endpoints.

## 4. Persistência de Dados e Banco de Dados
- **ORM:** Hibernate (via Spring Data JPA).
- **Estratégia de DDL:** Utilizar `spring.jpa.hibernate.ddl-auto=update` durante a fase de desenvolvimento para não perder dados a cada execução.
- **Relacionamentos:** Implementar relacionamentos entre entidades de forma adequada (ex: `@ManyToMany` utilizando `@JoinTable` para tabelas associativas).
- **Validação:** Implementar validações a nível de modelo/entidade (ex: validação de campos em branco, tamanhos mínimos/máximos, valores não negativos).

## 5. Estrutura Arquitetural (Camadas)
O projeto backend deve obrigatoriamente seguir a separação de responsabilidades nos seguintes pacotes lógicos:
- `config`: Configurações globais do Spring (segurança, CORS, internacionalização).
- `controller`: Definição dos endpoints da API, interceptação de requisições HTTP e retorno de status.
- `service`: Encapsulamento das regras de negócio, validações lógicas e fluxo de dados.
- `repository`: Interfaces de comunicação com o banco de dados (estendendo `JpaRepository`).
- `model`: Entidades de domínio mapeadas para tabelas físicas do banco de dados (anotações JPA).

## 6. Frontend
- **Tecnologia:** Livre (React, Python, HTML/JS puro, etc).
- **Objetivo:** Consumir e testar a API construída. O foco avaliativo está na funcionalidade plena do backend; o frontend serve como interface de consumo e validação visual das requisições.

## 7. Escopo do Domínio
- O tema do sistema é de livre escolha (ex: supermercado, petshop, loja).
- **Complexidade Mínima:** O modelo de dados não pode ser trivial (exige-se mais do que um sistema simples de apenas 5 tabelas independentes). Deve conter relacionamentos bem estabelecidos entre as entidades.

## 8. Ferramentas de Apoio e Infraestrutura
- **IDE:** IntelliJ IDEA (Utilizado pelo professor e recomendado pela integração com Spring), porém Eclipse, VS Code ou NetBeans são plenamente aceitos.
- **Administração de Banco de Dados:** pgAdmin (ou DBeaver).
- **Testes de Requisições:** Postman ou Insomnia.
- **Versionamento:** O uso de Git/GitHub é opcional, podendo a equipe optar por gerenciar os arquivos via Google Drive padrão, conforme preferência.
