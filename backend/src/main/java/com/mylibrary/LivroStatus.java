package com.mylibrary;

public enum LivroStatus {
    DISPONIVEL("Available"),
    EMPRESTADO("Loaned");

    private final String descricao;

    LivroStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
