package com.mylibrary;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LivroService {
    
    private final LivroRepository livroRepository;
    private final CategoriaRepository categoriaRepository;
    
    public LivroService(LivroRepository livroRepository, CategoriaRepository categoriaRepository) {
        this.livroRepository = livroRepository;
        this.categoriaRepository = categoriaRepository;
    }
    
    /**
     * Criar novo livro
     * RN03: Initial status must be DISPONIVEL
     */
    public LivroDTO criar(CreateLivroRequest request) {
        // Validate ISBN is unique
        if (livroRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new LivroException("Livro com ISBN '" + request.getIsbn() + "' já existe");
        }
        
        // Validate categoria exists
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new LivroException("Categoria não encontrada com ID: " + request.getCategoriaId()));
        
        try {
            Livro livro = new Livro();
            livro.setTitulo(request.getTitulo().trim());
            livro.setAutor(request.getAutor().trim());
            livro.setIsbn(request.getIsbn().trim());
            livro.setAno(request.getAno());
            livro.setStatus(LivroStatus.DISPONIVEL);
            livro.setCategoria(categoria);
            
            Livro livroSalvo = livroRepository.save(livro);
            return new LivroDTO(livroSalvo);
        } catch (DataIntegrityViolationException e) {
            throw new LivroException("Erro ao criar livro: " + e.getMessage(), e);
        }
    }
    
    /**
     * List all books
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> listar() {
        return livroRepository.findAll()
                .stream()
                .map(LivroDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * List books with filters for category and status
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> listarComFiltros(Long categoriaId, LivroStatus status) {
        List<Livro> livros;
        
        if (categoriaId != null && status != null) {
            livros = livroRepository.findByStatusAndCategoriaId(status, categoriaId);
        } else if (categoriaId != null) {
            livros = livroRepository.findByCategoriaId(categoriaId);
        } else if (status != null) {
            livros = livroRepository.findByStatus(status);
        } else {
            livros = livroRepository.findAll();
        }
        
        return livros.stream()
                .map(LivroDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Search books by title or author
     */
    @Transactional(readOnly = true)
    public List<LivroDTO> buscar(String titulo, String autor) {
        List<Livro> livros;
        
        if (titulo != null && !titulo.isEmpty() && autor != null && !autor.isEmpty()) {
            List<Livro> porTitulo = livroRepository.findByTituloContainingIgnoreCase(titulo);
            List<Livro> porAutor = livroRepository.findByAutorContainingIgnoreCase(autor);
            livros = porTitulo.stream()
                    .filter(porAutor::contains)
                    .collect(Collectors.toList());
        } else if (titulo != null && !titulo.isEmpty()) {
            livros = livroRepository.findByTituloContainingIgnoreCase(titulo);
        } else if (autor != null && !autor.isEmpty()) {
            livros = livroRepository.findByAutorContainingIgnoreCase(autor);
        } else {
            livros = livroRepository.findAll();
        }
        
        return livros.stream()
                .map(LivroDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get book by ID
     */
    @Transactional(readOnly = true)
    public LivroDTO obterPorId(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new LivroException("Livro não encontrado com ID: " + id));
        return new LivroDTO(livro);
    }
    
    /**
     * Delete a book
     * RN04: Only DISPONIVEL books can be deleted
     * RN05: Cannot delete EMPRESTADO books
     */
    public void deletar(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new LivroException("Livro não encontrado com ID: " + id));
        
        // Check if book is DISPONIVEL
        if (livro.getStatus() != LivroStatus.DISPONIVEL) {
            throw new LivroException("Não é possível deletar livro com status " + livro.getStatus() + 
                    ". Apenas livros com status DISPONIVEL podem ser deletados.");
        }
        
        livroRepository.delete(livro);
    }
}
