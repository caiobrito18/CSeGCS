import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EmprestimoService } from './emprestimo.service';
import { Emprestimo } from './emprestimo.model';
import { formatarTelefone, traduzirStatus } from './emprestimo.utils';

@Component({
  selector: 'app-emprestimo-return',
  templateUrl: './emprestimo-return.component.html',
  styleUrls: ['./emprestimo-return.component.css']
})
export class EmprestimoReturnComponent implements OnInit {
  emprestimo: Emprestimo | null = null;
  loading = true;
  submitting = false;
  confirmado = false;
  error: string | null = null;
  success = false;

  formatarTelefone = formatarTelefone;
  traduzirStatus = traduzirStatus;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private emprestimoService: EmprestimoService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.carregarEmprestimo(parseInt(id));
    }
  }

  carregarEmprestimo(id: number): void {
    this.loading = true;
    this.emprestimoService.obterPorId(id).subscribe({
      next: (data) => {
        this.emprestimo = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar empréstimo';
        this.loading = false;
        console.error(err);
      }
    });
  }

  devolver(): void {
    if (!this.emprestimo || !this.confirmado) {
      return;
    }

    this.submitting = true;
    this.error = null;

    this.emprestimoService.devolverLivro(this.emprestimo.id).subscribe({
      next: () => {
        this.success = true;
        this.submitting = false;
        setTimeout(() => {
          this.router.navigate(['/emprestimos']);
        }, 1500);
      },
      error: (err) => {
        this.error = 'Erro ao devolver livro';
        this.submitting = false;
        console.error(err);
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/emprestimos']);
  }
}
