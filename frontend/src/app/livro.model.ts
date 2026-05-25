export interface Livro {
  id: number;
  titulo: string;
  autor: string;
  isbn: string;
  ano: number;
  status: 'DISPONIVEL' | 'EMPRESTADO';
  categoriaId: number;
  categoriaNome: string;
}

export interface CreateLivroRequest {
  titulo: string;
  autor: string;
  isbn: string;
  ano: number;
  categoriaId: number;
}

export type LivroStatus = 'DISPONIVEL' | 'EMPRESTADO' | 'TODOS';
