package com.mylibrary;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "emprestimos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "livro_id", nullable = false)
    @NotNull(message = "Livro é obrigatório")
    private Livro livro;

    @NotBlank(message = "Nome da pessoa é obrigatório")
    @Column(length = 100, nullable = false)
    private String nomePessoa;

    @NotBlank(message = "Telefone é obrigatório")
    @Column(length = 20, nullable = false)
    private String telefone;

    @NotNull(message = "Data de empréstimo é obrigatória")
    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate dataEmprestimo;

    @NotNull(message = "Data de devolução prevista é obrigatória")
    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate dataDevolucaoPrevista;

    @Column(columnDefinition = "DATE")
    private LocalDate dataDevolucaoEfetiva;

    @PrePersist
    void prePersist() {
        if (this.dataEmprestimo == null) {
            this.dataEmprestimo = LocalDate.now();
        }
    }
}
