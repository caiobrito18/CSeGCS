import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LivroService } from '../livro.service';
import { Livro } from '../livro.model';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-livro-view',
  templateUrl: './livro-view.component.html',
  styleUrls: ['./livro-view.component.css']
})
export class LivroViewComponent implements OnInit, OnDestroy {
  livro: Livro | null = null;
  loading = false;
  error: string | null = null;
  success = false;
  successMessage = '';
  private destroy$ = new Subject<void>();
  private livroId: number = 0;

  constructor(
    private livroService: LivroService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.params.pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.livroId = params['id'];
        if (this.livroId) {
          this.loadLivro();
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadLivro(): void {
    this.loading = true;
    this.error = null;

    this.livroService.obterPorId(this.livroId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (livro) => {
          this.livro = livro;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load book';
          this.loading = false;
          console.error('Error loading book:', err);
        }
      });
  }

  canDelete(): boolean {
    return this.livro?.status === 'DISPONIVEL' || false;
  }

  delete(): void {
    if (!this.livro || !this.canDelete()) {
      this.error = 'Cannot delete a borrowed book';
      return;
    }

    if (confirm(`Are you sure you want to delete "${this.livro.titulo}"?`)) {
      this.loading = true;
      this.error = null;

      this.livroService.deletar(this.livro.id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.success = true;
            this.successMessage = 'Book deleted successfully';
            this.loading = false;
            setTimeout(() => {
              this.router.navigate(['/livros']);
            }, 1500);
          },
          error: (err) => {
            this.error = 'Failed to delete book';
            this.loading = false;
            console.error('Error deleting book:', err);
          }
        });
    }
  }

  goBack(): void {
    this.router.navigate(['/livros']);
  }
}
