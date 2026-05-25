package com.mylibrary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoDTO {

    private Long id;

    @NotNull(message = "Livro ID é obrigatório")
    private Long livroId;

    private String livroTitulo;

    private String livroAutor;

    private String livroIsbn;

    @NotBlank(message = "Nome da pessoa é obrigatório")
    private String nomePessoa;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotNull(message = "Data de empréstimo é obrigatória")
    private LocalDate dataEmprestimo;

    @NotNull(message = "Data de devolução prevista é obrigatória")
    private LocalDate dataDevolucaoPrevista;

    private LocalDate dataDevolucaoEfetiva;

    private boolean ativo;

    private boolean atrasado;

    public EmprestimoDTO(Emprestimo emprestimo) {
        this.id = emprestimo.getId();
        this.livroId = emprestimo.getLivro().getId();
        this.livroTitulo = emprestimo.getLivro().getTitulo();
        this.livroAutor = emprestimo.getLivro().getAutor();
        this.livroIsbn = emprestimo.getLivro().getIsbn();
        this.nomePessoa = emprestimo.getNomePessoa();
        this.telefone = emprestimo.getTelefone();
        this.dataEmprestimo = emprestimo.getDataEmprestimo();
        this.dataDevolucaoPrevista = emprestimo.getDataDevolucaoPrevista();
        this.dataDevolucaoEfetiva = emprestimo.getDataDevolucaoEfetiva();
        this.ativo = emprestimo.getDataDevolucaoEfetiva() == null;
        this.atrasado = this.ativo && emprestimo.getDataDevolucaoPrevista().isBefore(LocalDate.now());
    }
}
