import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LivroService } from '../livro.service';
import { CategoriaService } from '../categoria.service';
import { Categoria } from '../categoria.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-livro-add',
  templateUrl: './livro-add.component.html',
  styleUrls: ['./livro-add.component.css']
})
export class LivroAddComponent implements OnInit, OnDestroy {
  form!: FormGroup;
  categorias: Categoria[] = [];
  loading = false;
  error: string | null = null;
  success = false;
  private destroy$ = new Subject<void>();
  currentYear = new Date().getFullYear();

  constructor(
    private formBuilder: FormBuilder,
    private livroService: LivroService,
    private categoriaService: CategoriaService,
    private router: Router
  ) {
    this.createForm();
  }

  ngOnInit(): void {
    this.loadCategorias();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createForm(): void {
    this.form = this.formBuilder.group({
      titulo: ['', [Validators.required, Validators.minLength(3)]],
      autor: ['', [Validators.required, Validators.minLength(2)]],
      isbn: ['', [Validators.required, Validators.pattern(/^[0-9\-]{10,17}$/)]],
      ano: ['', [Validators.required, Validators.min(1900), Validators.max(this.currentYear)]],
      categoriaId: ['', Validators.required]
    });
  }

  private loadCategorias(): void {
    this.categoriaService.listarTodas()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (categorias) => {
          this.categorias = categorias;
        },
        error: (err) => {
          this.error = 'Failed to load categories';
          console.error('Error loading categorias:', err);
        }
      });
  }

  get titulo() {
    return this.form.get('titulo');
  }

  get autor() {
    return this.form.get('autor');
  }

  get isbn() {
    return this.form.get('isbn');
  }

  get ano() {
    return this.form.get('ano');
  }

  get categoriaId() {
    return this.form.get('categoriaId');
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }

    this.loading = true;
    this.error = null;
    this.success = false;

    const formData = this.form.value;
    this.livroService.criar(formData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.success = true;
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/livros']);
          }, 1500);
        },
        error: (err) => {
          this.error = 'Failed to create book. Please check for duplicate ISBN or try again.';
          this.loading = false;
          console.error('Error creating book:', err);
        }
      });
  }

  cancel(): void {
    this.router.navigate(['/livros']);
  }
}
