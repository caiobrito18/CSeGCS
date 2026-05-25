export type EmprestimoStatus = 'ATIVO' | 'DEVOLVIDO' | 'ATRASADO';

export interface Emprestimo {
  id: number;
  livroId: number;
  livroTitulo: string;
  nomePessoa: string;
  telefonePessoa: string;
  dataEmprestimo: string;
  dataPrevistaDevolucao: string;
  dataDevolucao: string | null;
  status: EmprestimoStatus;
}

export interface EmprestimoRequest {
  livroId: number;
  nomePessoa: string;
  telefonePessoa: string;
  dataPrevistaDevolucao: string;
}

export interface DashboardMetrics {
  totalLivrosRegistrados: number;
  totalLivrosDisponiveis: number;
  totalLivrosEmprestados: number;
  totalEmprestimosAtivos: number;
  emprestimosMaisRecentes: Emprestimo[];
}
