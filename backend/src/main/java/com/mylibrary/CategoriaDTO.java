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
public class CategoriaDTO {
    
    private Long id;
    
    @NotBlank(message = "Nome da categoria é obrigatório")
    private String nome;
    
    private String descricao;
    
    private Long livrosCount;

    public CategoriaDTO(Categoria categoria) {
        this.id = categoria.getId();
        this.nome = categoria.getNome();
        this.descricao = categoria.getDescricao();
        this.livrosCount = (long) categoria.getLivros().size();
    }
}
