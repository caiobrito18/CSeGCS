import { Component, OnInit } from '@angular/core';
import { EmprestimoService } from './emprestimo.service';
import { Emprestimo } from './emprestimo.model';
import { calcularDiasAtrasado, formatarTelefone } from './emprestimo.utils';

interface EmprestimoComAtraso extends Emprestimo {
  diasAtrasado: number;
}

@Component({
  selector: 'app-emprestimo-overdue',
  templateUrl: './emprestimo-overdue.component.html',
  styleUrls: ['./emprestimo-overdue.component.css']
})
export class EmprestimoOverdueComponent implements OnInit {
  emprestimos: EmprestimoComAtraso[] = [];
  loading = true;
  error: string | null = null;

  formatarTelefone = formatarTelefone;

  constructor(private emprestimoService: EmprestimoService) {}

  ngOnInit(): void {
    this.carregarAtrasados();
  }

  carregarAtrasados(): void {
    this.loading = true;
    this.error = null;

    this.emprestimoService.listarAtrasados().subscribe({
      next: (data) => {
        this.emprestimos = data
          .map(emp => ({
            ...emp,
            diasAtrasado: calcularDiasAtrasado(emp.dataPrevistaDevolucao)
          }))
          .sort((a, b) => b.diasAtrasado - a.diasAtrasado);
        
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar empréstimos atrasados';
        this.loading = false;
        console.error(err);
      }
    });
  }

  exportarPDF(): void {
    const printWindow = window.open('', '', 'height=600,width=800');
    if (!printWindow) return;

    let html = '<h2>Relatório de Empréstimos Atrasados</h2>';
    html += '<table border="1" cellpadding="10" cellspacing="0" style="width: 100%; border-collapse: collapse;">';
    html += '<thead>';
    html += '<tr style="background-color: #f0f0f0;">';
    html += '<th>Livro</th>';
    html += '<th>Pessoa</th>';
    html += '<th>Telefone</th>';
    html += '<th>Data Prevista</th>';
    html += '<th>Dias Atrasado</th>';
    html += '</tr>';
    html += '</thead>';
    html += '<tbody>';

    this.emprestimos.forEach(emp => {
      html += '<tr>';
      html += `<td>${emp.livroTitulo}</td>`;
      html += `<td>${emp.nomePessoa}</td>`;
      html += `<td>${this.formatarTelefone(emp.telefonePessoa)}</td>`;
      html += `<td>${new Date(emp.dataPrevistaDevolucao).toLocaleDateString('pt-BR')}</td>`;
      html += `<td style="text-align: center; color: red; font-weight: bold;">${emp.diasAtrasado}</td>`;
      html += '</tr>';
    });

    html += '</tbody>';
    html += '</table>';

    printWindow.document.write(html);
    printWindow.document.close();
    printWindow.focus();
    printWindow.print();
  }

  imprimirRelatório(): void {
    window.print();
  }
}
