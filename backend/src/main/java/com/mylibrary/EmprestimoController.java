package com.mylibrary;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    /**
     * GET /api/emprestimos - List all loans
     */
    @GetMapping("/api/emprestimos")
    public ResponseEntity<List<EmprestimoDTO>> listarTodos() {
        List<EmprestimoDTO> emprestimos = emprestimoService.listarTodos();
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * GET /api/emprestimos/ativos - List active loans
     */
    @GetMapping("/api/emprestimos/ativos")
    public ResponseEntity<List<EmprestimoDTO>> listarAtivos() {
        List<EmprestimoDTO> emprestimos = emprestimoService.listarAtivos();
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * GET /api/emprestimos/atrasados - List overdue loans
     */
    @GetMapping("/api/emprestimos/atrasados")
    public ResponseEntity<List<EmprestimoDTO>> listarAtrasados() {
        List<EmprestimoDTO> emprestimos = emprestimoService.listarAtrasados();
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * GET /api/emprestimos/livro/{livroId} - Get loan history for a specific book
     */
    @GetMapping("/api/emprestimos/livro/{livroId}")
    public ResponseEntity<List<EmprestimoDTO>> obterHistoricoLivro(@PathVariable Long livroId) {
        try {
            List<EmprestimoDTO> historico = emprestimoService.obterHistoricoLivro(livroId);
            return ResponseEntity.ok(historico);
        } catch (EmprestimoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/emprestimos/{id} - Get loan by ID
     */
    @GetMapping("/api/emprestimos/{id}")
    public ResponseEntity<?> obterPorId(@PathVariable Long id) {
        try {
            EmprestimoDTO emprestimo = emprestimoService.obterPorId(id);
            return ResponseEntity.ok(emprestimo);
        } catch (EmprestimoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/emprestimos/emprestar - Create a new loan
     * Returns 201 Created on success
     * Returns 400 Bad Request on business rule violation
     * Returns 409 Conflict if book is already loaned
     */
    @PostMapping("/api/emprestimos/emprestar")
    public ResponseEntity<?> emprestar(@Valid @RequestBody EmprestimoRequest request) {
        try {
            EmprestimoDTO emprestimo = emprestimoService.emprestar(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(emprestimo);
        } catch (EmprestimoException e) {
            // Check if it's a conflict (already loaned) or bad request
            if (e.getMessage().contains("status")) {
                Map<String, String> response = new HashMap<>();
                response.put("error", e.getMessage());
                response.put("code", "CONFLICT_LIVRO_EMPRESTADO");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /api/emprestimos/{id}/devolver - Return a book
     * Returns 204 No Content on success
     * Returns 400 Bad Request on business rule violation
     * Returns 404 Not Found if loan doesn't exist
     */
    @PostMapping("/api/emprestimos/{id}/devolver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> devolverLivro(@PathVariable Long id) {
        try {
            emprestimoService.devolverLivro(id);
            return ResponseEntity.noContent().build();
        } catch (EmprestimoException e) {
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * GET /api/dashboard - Get dashboard metrics
     */
    @GetMapping("/api/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardMetrics() {
        DashboardDTO dashboard = emprestimoService.getDashboardMetrics();
        return ResponseEntity.ok(dashboard);
    }
}
