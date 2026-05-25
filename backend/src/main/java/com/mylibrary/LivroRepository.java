package com.mylibrary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    
    List<Livro> findByAutorContainingIgnoreCase(String autor);
    
    List<Livro> findByCategoriaId(Long categoriaId);
    
    List<Livro> findByStatus(LivroStatus status);
    
    List<Livro> findByStatusAndCategoriaId(LivroStatus status, Long categoriaId);
    
    Optional<Livro> findByIsbn(String isbn);
    
    long countByStatus(LivroStatus status);
}
