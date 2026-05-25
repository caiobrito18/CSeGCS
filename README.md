# MyLibrary - Gerencimento de Acervo Completo (GCS)

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

## Licença

Este projeto é propriedade da instituição SENAI.

## Autores

Desenvolvido como projeto educacional - SENAI.

---

**Versão**: 0.1.0  
**Status**: Em Desenvolvimento  
**Última Atualização**: 2024

<!-- Phase 2: CRUD Categorias adicionado -->
