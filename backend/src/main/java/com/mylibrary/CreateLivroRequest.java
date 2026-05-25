package com.mylibrary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLivroRequest {
    
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 255, message = "Título deve ter entre 3 e 255 caracteres")
    private String titulo;
    
    @NotBlank(message = "Autor é obrigatório")
    @Size(min = 2, max = 255, message = "Autor deve ter entre 2 e 255 caracteres")
    private String autor;
    
    @NotBlank(message = "ISBN é obrigatório")
    @Size(min = 10, max = 17, message = "ISBN deve ter entre 10 e 17 caracteres")
    private String isbn;
    
    @NotNull(message = "Ano é obrigatório")
    @Min(value = 1900, message = "Ano deve ser a partir de 1900")
    @Max(value = 2100, message = "Ano deve ser até 2100")
    private Integer ano;
    
    @NotNull(message = "Categoria ID é obrigatório")
    @Positive(message = "Categoria ID deve ser positivo")
    private Long categoriaId;
}
