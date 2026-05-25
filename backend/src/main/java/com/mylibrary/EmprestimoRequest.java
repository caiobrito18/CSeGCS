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
public class EmprestimoRequest {

    @NotNull(message = "Livro ID é obrigatório")
    private Long livroId;

    @NotBlank(message = "Nome da pessoa é obrigatório")
    private String nomePessoa;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @NotNull(message = "Data de devolução prevista é obrigatória")
    private LocalDate dataDevolucaoPrevista;
}
