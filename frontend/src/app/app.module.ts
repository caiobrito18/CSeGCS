import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { CategoriaListComponent } from './categoria-list.component';
import { CategoriaAddComponent } from './categoria-add.component';
import { LivroListComponent } from './livro-list.component';
import { LivroAddComponent } from './livro-add.component';
import { LivroViewComponent } from './livro-view.component';
import { StatusPipe } from './status.pipe';
import { DashboardComponent } from './dashboard.component';
import { EmprestimoListComponent } from './emprestimo-list.component';
import { EmprestimoAddComponent } from './emprestimo-add.component';
import { EmprestimoReturnComponent } from './emprestimo-return.component';
import { EmprestimoViewComponent } from './emprestimo-view.component';
import { EmprestimoOverdueComponent } from './emprestimo-overdue.component';

const routes: Routes = [
  { path: '', component: CategoriaListComponent },
  { path: 'add', component: CategoriaAddComponent },
  { path: 'livros', component: LivroListComponent },
  { path: 'livros/add', component: LivroAddComponent },
  { path: 'livros/:id', component: LivroViewComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'emprestimos', component: EmprestimoListComponent },
  { path: 'emprestimos/emprestar', component: EmprestimoAddComponent },
  { path: 'emprestimos/atrasados', redirectTo: 'relatorio/atrasados' },
  { path: 'emprestimos/:id/devolver', component: EmprestimoReturnComponent },
  { path: 'emprestimos/:id', component: EmprestimoViewComponent },
  { path: 'relatorio/atrasados', component: EmprestimoOverdueComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent,
    CategoriaListComponent,
    CategoriaAddComponent,
    LivroListComponent,
    LivroAddComponent,
    LivroViewComponent,
    StatusPipe,
    DashboardComponent,
    EmprestimoListComponent,
    EmprestimoAddComponent,
    EmprestimoReturnComponent,
    EmprestimoViewComponent,
    EmprestimoOverdueComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
