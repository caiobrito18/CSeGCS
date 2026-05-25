import { Component, OnInit, OnDestroy } from '@angular/core';
import { CategoriaService } from './categoria.service';
import { Categoria } from './categoria.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-categoria-list',
  templateUrl: './categoria-list.component.html',
  styleUrls: ['./categoria-list.component.css']
})
export class CategoriaListComponent implements OnInit, OnDestroy {
  categorias: Categoria[] = [];
  loading = true;
  error: string | null = null;
  private destroy$ = new Subject<void>();

  constructor(private categoriaService: CategoriaService) {}

  ngOnInit(): void {
    this.loadCategorias();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCategorias(): void {
    this.loading = true;
    this.error = null;
    this.categoriaService.listarTodas()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.categorias = data;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load categories. Please try again later.';
          this.loading = false;
          console.error('Error loading categories:', err);
        }
      });
  }

  deletarCategoria(id: number): void {
    if (confirm('Are you sure you want to delete this category?')) {
      this.categoriaService.deletar(id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.loadCategorias();
          },
          error: (err) => {
            this.error = 'Failed to delete category. Please try again.';
            console.error('Error deleting category:', err);
          }
        });
    }
  }
}
