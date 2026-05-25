package com.mylibrary;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;

    public EmprestimoService(EmprestimoRepository emprestimoRepository, LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
    }

    /**
     * Create a new loan (Emprestar)
     * RN05: After creating loan, Livro.status = EMPRESTADO
     * RN07: Cannot loan if Livro.status = EMPRESTADO
     */
    public EmprestimoDTO emprestar(EmprestimoRequest request) {
        // Validate livro exists
        Livro livro = livroRepository.findById(request.getLivroId())
                .orElseThrow(() -> new EmprestimoException("Livro não encontrado com ID: " + request.getLivroId()));

        // Validate livro is DISPONIVEL (RN07)
        if (livro.getStatus() != LivroStatus.DISPONIVEL) {
            throw new EmprestimoException(
                    "Não é possível emprestar livro com status " + livro.getStatus() +
                    ". Apenas livros com status DISPONIVEL podem ser emprestados.");
        }

        try {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setLivro(livro);
            emprestimo.setNomePessoa(request.getNomePessoa().trim());
            emprestimo.setTelefone(request.getTelefone().trim());
            emprestimo.setDataEmprestimo(LocalDate.now());
            emprestimo.setDataDevolucaoPrevista(request.getDataDevolucaoPrevista());

            // Save emprestimo
            Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);

            // Update livro status to EMPRESTADO (RN05)
            livro.setStatus(LivroStatus.EMPRESTADO);
            livroRepository.save(livro);

            return new EmprestimoDTO(emprestimoSalvo);
        } catch (EmprestimoException e) {
            throw e;
        } catch (Exception e) {
            throw new EmprestimoException("Erro ao criar empréstimo: " + e.getMessage(), e);
        }
    }

    /**
     * Return a book (Devolver)
     * RN06: After returning, Livro.status = DISPONIVEL
     * RN08: Cannot return already-returned loan
     */
    public void devolverLivro(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new EmprestimoException("Empréstimo não encontrado com ID: " + emprestimoId));

        // Verify not already returned (RN08)
        if (emprestimo.getDataDevolucaoEfetiva() != null) {
            throw new EmprestimoException("Este empréstimo já foi devolvido em " + emprestimo.getDataDevolucaoEfetiva());
        }

        try {
            // Set return date
            emprestimo.setDataDevolucaoEfetiva(LocalDate.now());
            emprestimoRepository.save(emprestimo);

            // Update livro status to DISPONIVEL (RN06)
            Livro livro = emprestimo.getLivro();
            livro.setStatus(LivroStatus.DISPONIVEL);
            livroRepository.save(livro);
        } catch (Exception e) {
            throw new EmprestimoException("Erro ao devolver empréstimo: " + e.getMessage(), e);
        }
    }

    /**
     * List all loans
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarTodos() {
        return emprestimoRepository.findAll()
                .stream()
                .map(EmprestimoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * List active loans (not returned)
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarAtivos() {
        return emprestimoRepository.findAllByDataDevolucaoEfetivaNula()
                .stream()
                .map(EmprestimoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * List overdue loans (RN08)
     * Overdue = dataDevolucaoPrevista < TODAY AND dataDevolucaoEfetiva IS NULL
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarAtrasados() {
        return emprestimoRepository.findOverdueLoans(LocalDate.now())
                .stream()
                .map(EmprestimoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get loan by ID
     */
    @Transactional(readOnly = true)
    public EmprestimoDTO obterPorId(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new EmprestimoException("Empréstimo não encontrado com ID: " + id));
        return new EmprestimoDTO(emprestimo);
    }

    /**
     * Get loan history for a specific book
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> obterHistoricoLivro(Long livroId) {
        // Verify livro exists
        livroRepository.findById(livroId)
                .orElseThrow(() -> new EmprestimoException("Livro não encontrado com ID: " + livroId));

        return emprestimoRepository.findByLivroId(livroId)
                .stream()
                .map(EmprestimoDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get dashboard metrics
     */
    @Transactional(readOnly = true)
    public DashboardDTO getDashboardMetrics() {
        // Get total books
        long totalLivros = livroRepository.count();

        // Get available books
        long totalDisponivel = livroRepository.countByStatus(LivroStatus.DISPONIVEL);

        // Get loaned books
        long totalEmprestado = livroRepository.countByStatus(LivroStatus.EMPRESTADO);

        // Get active loans
        long emprestimosAtivos = emprestimoRepository.countActiveLoans();

        // Get overdue loans
        List<EmprestimoDTO> atrasados = listarAtrasados();
        long emprestimosAtrasados = atrasados.size();

        // Get 5 most recent loans
        List<EmprestimoDTO> emprestimosList = listarTodos().stream()
                .sorted((a, b) -> b.getDataEmprestimo().compareTo(a.getDataEmprestimo()))
                .limit(5)
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalLivros(totalLivros)
                .totalDisponivel(totalDisponivel)
                .totalEmprestado(totalEmprestado)
                .emprestimosAtivos(emprestimosAtivos)
                .emprestimosAtrasados(emprestimosAtrasados)
                .emprestimosList(emprestimosList)
                .build();
    }
}
