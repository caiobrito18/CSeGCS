package com.mylibrary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivroDTO {
    
    private Long id;
    
    @NotBlank(message = "Título é obrigatório")
    private String titulo;
    
    @NotBlank(message = "Autor é obrigatório")
    private String autor;
    
    @NotBlank(message = "ISBN é obrigatório")
    private String isbn;
    
    @NotNull(message = "Ano é obrigatório")
    private Integer ano;
    
    private LivroStatus status;
    
    @NotNull(message = "Categoria ID é obrigatório")
    private Long categoriaId;
    
    private String categoriaNome;

    public LivroDTO(Livro livro) {
        this.id = livro.getId();
        this.titulo = livro.getTitulo();
        this.autor = livro.getAutor();
        this.isbn = livro.getIsbn();
        this.ano = livro.getAno();
        this.status = livro.getStatus();
        this.categoriaId = livro.getCategoria() != null ? livro.getCategoria().getId() : null;
        this.categoriaNome = livro.getCategoria() != null ? livro.getCategoria().getNome() : null;
    }
}
