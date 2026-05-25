import { Component, OnInit } from '@angular/core';
import { EmprestimoService } from './emprestimo.service';
import { DashboardMetrics, Emprestimo } from './emprestimo.model';
import { traduzirStatus, obterCorStatus, formatarTelefone } from './emprestimo.utils';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  metrics: DashboardMetrics | null = null;
  loading = true;
  error: string | null = null;

  traduzirStatus = traduzirStatus;
  obterCorStatus = obterCorStatus;
  formatarTelefone = formatarTelefone;

  constructor(private emprestimoService: EmprestimoService) {}

  ngOnInit(): void {
    this.carregarMetricas();
  }

  carregarMetricas(): void {
    this.loading = true;
    this.error = null;
    
    this.emprestimoService.getDashboardMetrics().subscribe({
      next: (data) => {
        this.metrics = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar métricas do dashboard';
        this.loading = false;
        console.error(err);
      }
    });
  }
}
