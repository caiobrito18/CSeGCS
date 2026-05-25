import { EmprestimoStatus } from './emprestimo.model';

export function calcularDiasAtrasado(dataPrevista: string): number {
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  
  const prevista = new Date(dataPrevista);
  prevista.setHours(0, 0, 0, 0);
  
  const diferenca = hoje.getTime() - prevista.getTime();
  return Math.max(0, Math.floor(diferenca / (1000 * 60 * 60 * 24)));
}

export function formatarTelefone(telefone: string): string {
  const cleaned = telefone.replace(/\D/g, '');
  
  if (cleaned.length === 11) {
    return `(${cleaned.substring(0, 2)}) ${cleaned.substring(2, 7)}-${cleaned.substring(7)}`;
  }
  
  if (cleaned.length === 10) {
    return `(${cleaned.substring(0, 2)}) ${cleaned.substring(2, 6)}-${cleaned.substring(6)}`;
  }
  
  return telefone;
}

export function traduzirStatus(status: EmprestimoStatus): string {
  const statusMap: Record<EmprestimoStatus, string> = {
    'ATIVO': 'Ativo',
    'DEVOLVIDO': 'Devolvido',
    'ATRASADO': 'Atrasado'
  };
  return statusMap[status] || status;
}

export function obterCorStatus(status: EmprestimoStatus): string {
  const corMap: Record<EmprestimoStatus, string> = {
    'ATIVO': 'success',
    'DEVOLVIDO': 'secondary',
    'ATRASADO': 'danger'
  };
  return corMap[status] || 'secondary';
}
