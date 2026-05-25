package com.mylibrary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CategoriaController {
    
    private final CategoriaService categoriaService;
    
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }
    
    /**
     * GET /api/categorias - List all categories
     */
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(categorias);
    }
    
    /**
     * GET /api/categorias/{id} - Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> obterPorId(@PathVariable Long id) {
        try {
            CategoriaDTO categoria = categoriaService.obterPorId(id);
            return ResponseEntity.ok(categoria);
        } catch (CategoriaException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * POST /api/categorias - Create new category
     * Returns 201 Created on success
     * Returns 409 Conflict if name already exists
     */
    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody CategoriaDTO dto) {
        try {
            CategoriaDTO novaCategoria = categoriaService.criar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
        } catch (CategoriaException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            error.put("codigo", "CATEGORIA_DUPLICADA");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }
    
    /**
     * PUT /api/categorias/{id} - Update category
     * Returns 200 OK on success
     * Returns 404 Not Found if category doesn't exist
     * Returns 409 Conflict if new name already exists
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        try {
            CategoriaDTO categoriaAtualizada = categoriaService.atualizar(id, dto);
            return ResponseEntity.ok(categoriaAtualizada);
        } catch (CategoriaException e) {
            if (e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            error.put("codigo", "VALIDACAO_FALHOU");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }
    }
    
    /**
     * DELETE /api/categorias/{id} - Delete category
     * Returns 204 No Content on success
     * Returns 404 Not Found if category doesn't exist
     * Returns 400 Bad Request if category has linked books
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable Long id) {
        try {
            categoriaService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (CategoriaException e) {
            if (e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("Não é possível deletar")) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", e.getMessage());
                error.put("codigo", "CATEGORIA_COM_LIVROS");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
