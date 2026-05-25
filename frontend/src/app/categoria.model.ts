export interface Categoria {
  id: number;
  nome: string;
  descricao: string;
  livrosCount: number;
}

export interface CreateCategoriaRequest {
  nome: string;
  descricao: string;
}
