# MyLibrary - Gerencimento de Acervo Completo (GCS)

![CI/CD Pipeline](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue?style=flat-square)
![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square)
![Node.js](https://img.shields.io/badge/Node.js-20+-green?style=flat-square)
![Angular](https://img.shields.io/badge/Angular-17+-red?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0+-brightgreen?style=flat-square)

## Descrição do Projeto

O MyLibrary é um sistema completo de gerenciamento de acervo bibliotecário desenvolvido com tecnologias modernas. O projeto segue a arquitetura de três camadas, dividido entre:

- **Backend**: API RESTful desenvolvida com Spring Boot e Maven
- **Frontend**: Interface web responsiva desenvolvida com Angular

## Objetivo

Fornecer uma solução integrada para:

- Gerenciamento de livros e materiais
- Controle de empréstimos e devoluções
- Gestão de usuários e bibliotecários
- Relatórios e consultas avançadas

## Tecnologias

### Backend

- Java 11+
- Spring Boot 2.x/3.x
- Spring Data JPA
- Maven
- MySQL/PostgreSQL

### Frontend

- Angular 14+
- TypeScript
- Bootstrap/Material Design
- RxJS

## Estrutura do Projeto

```
mylibrary-gcs/
├── backend/              # API Spring Boot
│   ├── src/
│   │   ├── main/java/com/mylibrary
│   │   ├── main/resources
│   │   └── test/java/com/mylibrary
│   └── pom.xml
├── frontend/             # Aplicação Angular
│   ├── src/
│   │   ├── app/
│   │   └── assets/
│   ├── angular.json
│   └── package.json
├── README.md
├── CHANGELOG.md
└── .gitignore
```

## Como Começar

### Pré-requisitos

- JDK 11 ou superior
- Node.js 16+
- npm 8+
- Git

### Instalação

#### Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Frontend

```bash
cd frontend
npm install
ng serve
```

## Contribuição

Para contribuir, consulte as guidelines de contribuição do projeto.

## CI/CD Pipeline

Este projeto utiliza **GitHub Actions** para automação de builds, testes e deploy.

### Workflows

- **CI Pipeline** (`ci.yml`): Executa em cada push e pull request

  - Build backend com Maven
  - Build frontend com npm
  - Execução de testes
  - Análise de qualidade de código
  - Upload de artefatos
- **Docker Build** (`docker-build.yml`): Executa em releases (tags v*)

  - Build de imagens Docker
  - Pode ser estendido para push em registries

### Status das Builds

Verifique o status das builds: [GitHub Actions](../../actions)

### Documentação

- [DEVELOPMENT.md](DEVELOPMENT.md) - Configuração de desenvolvimento local
- [TESTING.md](TESTING.md) - Guia de testes
- [DEPLOYMENT.md](DEPLOYMENT.md) - Procedimentos de deploy
- [CI-CD.md](CI-CD.md) - Documentação completa do pipeline

## Contribuição

Para contribuir, consulte as guidelines de contribuição do projeto.

## Licença

## Autores

Caio de Paula Brito

**Versão**: 0.1.0
**Status**: Em Desenvolvimento
**Última Atualização**: 2024
