import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { Livro, CreateLivroRequest } from './livro.model';

@Injectable({
  providedIn: 'root'
})
export class LivroService {
  private apiUrl = `${environment.apiUrl}/api/livros`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Livro[]> {
    return this.http.get<Livro[]>(this.apiUrl)
      .pipe(
        catchError(this.handleError)
      );
  }

  listarComFiltros(categoriaId: number | null, status: string): Observable<Livro[]> {
    let url = this.apiUrl;
    const params = [];

    if (categoriaId !== null && categoriaId !== 0) {
      params.push(`categoriaId=${categoriaId}`);
    }

    if (status && status !== 'TODOS') {
      params.push(`status=${status}`);
    }

    if (params.length > 0) {
      url += '?' + params.join('&');
    }

    return this.http.get<Livro[]>(url)
      .pipe(
        catchError(this.handleError)
      );
  }

  buscar(titulo: string, autor: string): Observable<Livro[]> {
    const params = [];

    if (titulo && titulo.trim()) {
      params.push(`titulo=${encodeURIComponent(titulo.trim())}`);
    }

    if (autor && autor.trim()) {
      params.push(`autor=${encodeURIComponent(autor.trim())}`);
    }

    let url = `${this.apiUrl}/search`;
    if (params.length > 0) {
      url += '?' + params.join('&');
    }

    return this.http.get<Livro[]>(url)
      .pipe(
        catchError(this.handleError)
      );
  }

  obterPorId(id: number): Observable<Livro> {
    return this.http.get<Livro>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  criar(data: CreateLivroRequest): Observable<Livro> {
    return this.http.post<Livro>(this.apiUrl, data)
      .pipe(
        catchError(this.handleError)
      );
  }

  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    return throwError(() => new Error(errorMessage));
  }
}
