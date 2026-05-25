# MyLibrary Frontend - Phase 2 Categoria Component

Angular 17+ frontend application for managing Categoria (Categories) with CRUD operations.

## Features

- **List Categories**: View all categories in a responsive table
- **Create Category**: Add new categories with validation
- **Delete Category**: Remove categories with confirmation
- **Reactive Forms**: Form validation with real-time feedback
- **Bootstrap 5**: Responsive and modern UI design
- **Error Handling**: Comprehensive error handling and user feedback

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── app.module.ts              # Main module with routing
│   │   ├── app.component.ts           # Root component
│   │   ├── app.component.html         # Main template with navigation
│   │   ├── app.component.css          # Component styles
│   │   ├── categoria.model.ts         # Data models and interfaces
│   │   ├── categoria.service.ts       # API service
│   │   ├── categoria-list.component.ts # List component
│   │   ├── categoria-list.component.html
│   │   ├── categoria-list.component.css
│   │   ├── categoria-add.component.ts # Add component
│   │   ├── categoria-add.component.html
│   │   └── categoria-add.component.css
│   ├── environments/
│   │   ├── environment.ts             # Dev configuration
│   │   └── environment.prod.ts        # Prod configuration
│   ├── main.ts                        # Entry point
│   ├── index.html                     # Root HTML
│   └── styles.css                     # Global styles
├── angular.json                       # Angular CLI config
├── tsconfig.json                      # TypeScript config
├── tsconfig.app.json                  # App TypeScript config
└── package.json                       # Dependencies
```

## Installation

```bash
cd frontend
npm install
```

## Running the Application

### Development Server

```bash
npm start
```

The application will run on `http://localhost:4200/`

### Build for Production

```bash
npm run build
```

## Configuration

### API Configuration

The API URL is configured in `src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

Change the `apiUrl` to point to your backend API.

## Components Overview

### CategoriaListComponent
- Displays all categories in a table
- Delete functionality with confirmation
- Loading and error states
- Refresh after operations

### CategoriaAddComponent
- Reactive form for creating new categories
- Form validation (required fields, minLength)
- Error handling and success feedback
- Navigation back to list after success

### CategoriaService
- Handles all API communication
- Methods:
  - `listarTodas()`: Get all categories
  - `criar(data)`: Create a new category
  - `deletar(id)`: Delete a category by ID
  - Error handling for all requests

## Form Validation

The add category form includes:
- **nome (Name)**: Required, minimum 3 characters
- **descricao (Description)**: Required

## API Endpoints

The service expects the following endpoints:

- `GET /api/categorias` - List all categories
- `POST /api/categorias` - Create new category
- `DELETE /api/categorias/{id}` - Delete category

## Data Models

### Categoria
```typescript
interface Categoria {
  id: number;
  nome: string;
  descricao: string;
  livrosCount: number;
}
```

### CreateCategoriaRequest
```typescript
interface CreateCategoriaRequest {
  nome: string;
  descricao: string;
}
```

## Technologies Used

- **Angular 17+**: Modern frontend framework
- **Reactive Forms**: Advanced form handling with validation
- **RxJS**: Reactive programming
- **Bootstrap 5**: UI framework
- **TypeScript**: Type-safe JavaScript

## Best Practices Implemented

- ✅ Reactive Forms with FormBuilder
- ✅ Proper error handling with HttpErrorResponse
- ✅ Unsubscribe pattern using takeUntil
- ✅ OnDestroy lifecycle hook for cleanup
- ✅ Component-based architecture
- ✅ Service for API communication
- ✅ Bootstrap responsive design
- ✅ Loading states
- ✅ User feedback (success/error messages)
- ✅ Confirmation dialogs for destructive actions
