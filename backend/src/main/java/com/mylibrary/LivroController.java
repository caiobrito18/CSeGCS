package com.mylibrary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LivroController {
    
    private final LivroService livroService;
    
    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }
    
    /**
     * GET /api/livros - List all books
     * Can filter with query params: categoria={id}&status={status}
     * Can search with: titulo={titulo}&autor={autor}
     */
    @GetMapping
    public ResponseEntity<List<LivroDTO>> listar(
            @RequestParam(name = "categoria", required = false) Long categoriaId,
            @RequestParam(name = "status", required = false) LivroStatus status,
            @RequestParam(name = "titulo", required = false) String titulo,
            @RequestParam(name = "autor", required = false) String autor) {
        
        List<LivroDTO> livros;
        
        // If search parameters are provided, use search
        if ((titulo != null && !titulo.isEmpty()) || (autor != null && !autor.isEmpty())) {
            livros = livroService.buscar(titulo, autor);
        } else {
            // Otherwise use filters
            livros = livroService.listarComFiltros(categoriaId, status);
        }
        
        return ResponseEntity.ok(livros);
    }
    
    /**
     * GET /api/livros/{id} - Get book details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obterPorId(@PathVariable Long id) {
        try {
            LivroDTO livro = livroService.obterPorId(id);
            return ResponseEntity.ok(livro);
        } catch (LivroException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/livros - Create book
     * Returns 201 Created on success
     * Returns 409 Conflict if ISBN already exists
     * Returns 400 Bad Request if validation fails
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody CreateLivroRequest request) {
        try {
            LivroDTO novoLivro = livroService.criar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoLivro);
        } catch (LivroException e) {
            Map<String, String> error = new HashMap<>();
            if (e.getMessage().contains("já existe")) {
                error.put("erro", e.getMessage());
                error.put("codigo", "ISBN_DUPLICADO");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            } else if (e.getMessage().contains("Categoria não encontrada")) {
                error.put("erro", e.getMessage());
                error.put("codigo", "CATEGORIA_NAO_ENCONTRADA");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * DELETE /api/livros/{id} - Delete book (only if DISPONIVEL)
     * Returns 204 No Content on success
     * Returns 404 Not Found if book doesn't exist
     * Returns 400 Bad Request if book status is EMPRESTADO
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            livroService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (LivroException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("Não é possível deletar")) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", e.getMessage());
                error.put("codigo", "LIVRO_EMPRESTADO");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
