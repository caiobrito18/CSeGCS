import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { LivroService } from '../livro.service';
import { CategoriaService } from '../categoria.service';
import { Livro, LivroStatus } from '../livro.model';
import { Categoria } from '../categoria.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-livro-list',
  templateUrl: './livro-list.component.html',
  styleUrls: ['./livro-list.component.css']
})
export class LivroListComponent implements OnInit, OnDestroy {
  livros: Livro[] = [];
  categorias: Categoria[] = [];
  loading = false;
  error: string | null = null;
  success = false;
  successMessage = '';
  filterForm!: FormGroup;
  selectedCategoriaId: number | null = null;
  selectedStatus: LivroStatus = 'TODOS';
  searchTitulo = '';
  searchAutor = '';
  private destroy$ = new Subject<void>();

  constructor(
    private livroService: LivroService,
    private categoriaService: CategoriaService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.createFilterForm();
  }

  ngOnInit(): void {
    this.loadCategorias();
    this.loadLivros();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createFilterForm(): void {
    this.filterForm = this.formBuilder.group({
      categoriaId: [null],
      status: ['TODOS'],
      titulo: [''],
      autor: ['']
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
          console.error('Error loading categorias:', err);
        }
      });
  }

  loadLivros(): void {
    this.loading = true;
    this.error = null;

    const searchTitulo = this.filterForm.get('titulo')?.value || '';
    const searchAutor = this.filterForm.get('autor')?.value || '';
    const categoriaId = this.filterForm.get('categoriaId')?.value;
    const status = this.filterForm.get('status')?.value || 'TODOS';

    // If search is provided, use search endpoint
    if (searchTitulo.trim() || searchAutor.trim()) {
      this.livroService.buscar(searchTitulo, searchAutor)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (livros) => {
            this.livros = livros;
            this.loading = false;
          },
          error: (err) => {
            this.error = 'Failed to search books';
            this.loading = false;
            console.error('Error searching books:', err);
          }
        });
    } else {
      // Use filters endpoint
      this.livroService.listarComFiltros(categoriaId, status)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (livros) => {
            this.livros = livros;
            this.loading = false;
          },
          error: (err) => {
            this.error = 'Failed to load books';
            this.loading = false;
            console.error('Error loading books:', err);
          }
        });
    }
  }

  applyFilters(): void {
    this.loadLivros();
  }

  clearFilters(): void {
    this.filterForm.reset({
      categoriaId: null,
      status: 'TODOS',
      titulo: '',
      autor: ''
    });
    this.selectedCategoriaId = null;
    this.selectedStatus = 'TODOS';
    this.searchTitulo = '';
    this.searchAutor = '';
    this.loadLivros();
  }

  canDelete(livro: Livro): boolean {
    return livro.status === 'DISPONIVEL';
  }

  delete(livro: Livro): void {
    if (!this.canDelete(livro)) {
      this.error = 'Cannot delete a borrowed book';
      return;
    }

    if (confirm(`Are you sure you want to delete "${livro.titulo}"?`)) {
      this.loading = true;
      this.error = null;

      this.livroService.deletar(livro.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.success = true;
            this.successMessage = 'Book deleted successfully';
            this.loading = false;
            setTimeout(() => {
              this.success = false;
              this.loadLivros();
            }, 2000);
          },
          error: (err) => {
            this.error = 'Failed to delete book';
            this.loading = false;
            console.error('Error deleting book:', err);
          }
        });
    }
  }

  viewBook(livro: Livro): void {
    this.router.navigate(['/livros', livro.id]);
  }

  addBook(): void {
    this.router.navigate(['/livros/add']);
  }
}
