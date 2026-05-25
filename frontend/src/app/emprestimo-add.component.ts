import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { EmprestimoService } from './emprestimo.service';
import { LivroService } from './livro.service';
import { Livro } from './livro.model';

@Component({
  selector: 'app-emprestimo-add',
  templateUrl: './emprestimo-add.component.html',
  styleUrls: ['./emprestimo-add.component.css']
})
export class EmprestimoAddComponent implements OnInit {
  form!: FormGroup;
  livros: Livro[] = [];
  loading = true;
  submitting = false;
  error: string | null = null;
  success = false;

  constructor(
    private fb: FormBuilder,
    private emprestimoService: EmprestimoService,
    private livroService: LivroService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.carregarLivros();
  }

  initializeForm(): void {
    const today = new Date().toISOString().split('T')[0];
    
    this.form = this.fb.group({
      livroId: ['', Validators.required],
      nomePessoa: ['', [Validators.required, Validators.minLength(3)]],
      telefonePessoa: ['', [Validators.required, (control: any) => this.validarTelefone(control)]],
      dataPrevistaDevolucao: ['', [Validators.required, (control: any) => this.validarDataFutura(control)]]
    });
  }

  carregarLivros(): void {
    this.loading = true;
    // Carrega apenas livros disponíveis
    this.livroService.listarComFiltros(null, 'DISPONIVEL').subscribe({
      next: (data) => {
        this.livros = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erro ao carregar livros';
        this.loading = false;
        console.error(err);
      }
    });
  }

  validarTelefone(control: any): { [key: string]: any } | null {
    if (!control.value) {
      return null;
    }
    const telefone = control.value.replace(/\D/g, '');
    if (telefone.length < 10 || telefone.length > 11) {
      return { 'telefoneInvalido': true };
    }
    return null;
  }

  validarDataFutura(control: any): { [key: string]: any } | null {
    if (!control.value) {
      return null;
    }
    const data = new Date(control.value);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    data.setHours(0, 0, 0, 0);
    
    if (data <= hoje) {
      return { 'dataPassada': true };
    }
    return null;
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.error = 'Preencha todos os campos obrigatórios corretamente';
      return;
    }

    this.submitting = true;
    this.error = null;

    const request = {
      livroId: parseInt(this.form.value.livroId),
      nomePessoa: this.form.value.nomePessoa,
      telefonePessoa: this.form.value.telefonePessoa,
      dataPrevistaDevolucao: this.form.value.dataPrevistaDevolucao
    };

    this.emprestimoService.emprestar(request).subscribe({
      next: () => {
        this.success = true;
        this.submitting = false;
        setTimeout(() => {
          this.router.navigate(['/emprestimos']);
        }, 1500);
      },
      error: (err) => {
        this.error = 'Erro ao registrar empréstimo';
        this.submitting = false;
        console.error(err);
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/emprestimos']);
  }

  get livroId() { return this.form.get('livroId'); }
  get nomePessoa() { return this.form.get('nomePessoa'); }
  get telefonePessoa() { return this.form.get('telefonePessoa'); }
  get dataPrevistaDevolucao() { return this.form.get('dataPrevistaDevolucao'); }
}
