import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EmprestimoService } from './emprestimo.service';
import { Emprestimo } from './emprestimo.model';
import { formatarTelefone, traduzirStatus, obterCorStatus, calcularDiasAtrasado } from './emprestimo.utils';

@Component({
  selector: 'app-emprestimo-view',
  templateUrl: './emprestimo-view.component.html',
  styleUrls: ['./emprestimo-view.component.css']
})
export class EmprestimoViewComponent implements OnInit {
  emprestimo: Emprestimo | null = null;
  loading = true;
  error: string | null = null;

  formatarTelefone = formatarTelefone;
  traduzirStatus = traduzirStatus;
  obterCorStatus = obterCorStatus;
  calcularDiasAtrasado = calcularDiasAtrasado;

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

  voltar(): void {
    this.router.navigate(['/emprestimos']);
  }

  devolver(): void {
    if (this.emprestimo) {
      this.router.navigate(['/emprestimos', this.emprestimo.id, 'devolver']);
    }
  }

  podeDevolver(): boolean {
    return this.emprestimo?.status === 'ATIVO' || this.emprestimo?.status === 'ATRASADO';
  }
}
