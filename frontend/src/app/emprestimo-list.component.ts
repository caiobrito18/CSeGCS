import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { EmprestimoService } from './emprestimo.service';
import { Emprestimo, EmprestimoStatus } from './emprestimo.model';
import { traduzirStatus, obterCorStatus, formatarTelefone } from './emprestimo.utils';

@Component({
  selector: 'app-emprestimo-list',
  templateUrl: './emprestimo-list.component.html',
  styleUrls: ['./emprestimo-list.component.css']
})
export class EmprestimoListComponent implements OnInit {
  emprestimos: Emprestimo[] = [];
  filtroAtual: 'TODOS' | 'ATIVOS' | 'ATRASADOS' = 'TODOS';
  loading = true;
  error: string | null = null;

  traduzirStatus = traduzirStatus;
  obterCorStatus = obterCorStatus;
  formatarTelefone = formatarTelefone;

  constructor(
    private emprestimoService: EmprestimoService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarEmprestimos();
  }

  carregarEmprestimos(): void {
    this.loading = true;
    this.error = null;

    let request;
    
    switch (this.filtroAtual) {
      case 'ATIVOS':
        request = this.emprestimoService.listarAtivos();
        break;
      case 'ATRASADOS':
        request = this.emprestimoService.listarAtrasados();
        break;
      default:
        request = this.emprestimoService.listar();
    }

    request.subscribe({
      next: (data) => {
        this.emprestimos = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar empréstimos';
        this.loading = false;
        console.error(err);
      }
    });
  }

  filtrar(tipo: 'TODOS' | 'ATIVOS' | 'ATRASADOS'): void {
    this.filtroAtual = tipo;
    this.carregarEmprestimos();
  }

  verDetalhes(emprestimoId: number): void {
    this.router.navigate(['/emprestimos', emprestimoId]);
  }

  irParaDevolver(emprestimoId: number): void {
    this.router.navigate(['/emprestimos', emprestimoId, 'devolver']);
  }

  devolver(emprestimoId: number): void {
    if (confirm('Tem certeza que deseja marcar este livro como devolvido?')) {
      this.emprestimoService.devolverLivro(emprestimoId).subscribe({
        next: () => {
          this.carregarEmprestimos();
        },
        error: (err) => {
          this.error = 'Erro ao devolver livro';
          console.error(err);
        }
      });
    }
  }

  podeDevolver(emprestimo: Emprestimo): boolean {
    return emprestimo.status === 'ATIVO' || emprestimo.status === 'ATRASADO';
  }
}
