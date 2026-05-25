import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';
import { Emprestimo, EmprestimoRequest, DashboardMetrics } from './emprestimo.model';

@Injectable({
  providedIn: 'root'
})
export class EmprestimoService {
  private apiUrl = `${environment.apiUrl}/api/emprestimos`;

  constructor(private http: HttpClient) {}

  listar(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(this.apiUrl)
      .pipe(
        catchError(this.handleError)
      );
  }

  listarAtivos(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(`${this.apiUrl}/ativos`)
      .pipe(
        catchError(this.handleError)
      );
  }

  listarAtrasados(): Observable<Emprestimo[]> {
    return this.http.get<Emprestimo[]>(`${this.apiUrl}/atrasados`)
      .pipe(
        catchError(this.handleError)
      );
  }

  emprestar(request: EmprestimoRequest): Observable<Emprestimo> {
    return this.http.post<Emprestimo>(this.apiUrl, request)
      .pipe(
        catchError(this.handleError)
      );
  }

  devolverLivro(emprestimoId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${emprestimoId}/devolver`, {})
      .pipe(
        catchError(this.handleError)
      );
  }

  obterPorId(emprestimoId: number): Observable<Emprestimo> {
    return this.http.get<Emprestimo>(`${this.apiUrl}/${emprestimoId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  getDashboardMetrics(): Observable<DashboardMetrics> {
    return this.http.get<DashboardMetrics>(`${this.apiUrl}/dashboard/metrics`)
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
