package com.mylibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmprestimoService Unit Tests")
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private LivroRepository livroRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    private Categoria categoria;
    private Livro livro;
    private Emprestimo emprestimo;
    private EmprestimoRequest request;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Ficção Científica")
                .descricao("Livros de ficção científica")
                .livros(new HashSet<>())
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        livro = Livro.builder()
                .id(1L)
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        emprestimo = Emprestimo.builder()
                .id(1L)
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .dataDevolucaoEfetiva(null)
                .build();

        request = EmprestimoRequest.builder()
                .livroId(1L)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
    }

    @Test
    @DisplayName("Deve emprestar livro com dados válidos")
    void testEmprestarLivroComDadosValidos() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        EmprestimoDTO resultado = emprestimoService.emprestar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNomePessoa());
        assertEquals("11987654321", resultado.getTelefone());
        assertTrue(resultado.isAtivo());
        assertFalse(resultado.isAtrasado());

        verify(livroRepository).findById(1L);
        verify(emprestimoRepository).save(any(Emprestimo.class));
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve atualizar status do livro para EMPRESTADO após empréstimo (RN05)")
    void testStatusLivroAtualizadoParaEmprestado() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        emprestimoService.emprestar(request);

        verify(livroRepository).save(argThat(l -> l.getStatus() == LivroStatus.EMPRESTADO));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar emprestar livro EMPRESTADO (RN07)")
    void testNaoEmprestarLivroEmprestado() {
        livro.setStatus(LivroStatus.EMPRESTADO);
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));

        EmprestimoException exception = assertThrows(EmprestimoException.class, () -> {
            emprestimoService.emprestar(request);
        });

        assertTrue(exception.getMessage().contains("status"));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao emprestar livro inexistente")
    void testEmprestarLivroInexistente() {
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());

        EmprestimoException exception = assertThrows(EmprestimoException.class, () -> {
            request.setLivroId(999L);
            emprestimoService.emprestar(request);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("Deve devolver livro com sucesso")
    void testDevolverLivroComSucesso() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        assertDoesNotThrow(() -> emprestimoService.devolverLivro(1L));

        verify(emprestimoRepository).findById(1L);
        verify(emprestimoRepository).save(any(Emprestimo.class));
        verify(livroRepository).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve atualizar status do livro para DISPONIVEL após devolução (RN06)")
    void testStatusLivroAtualizadoParaDisponivel() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        emprestimoService.devolverLivro(1L);

        verify(livroRepository).save(argThat(l -> l.getStatus() == LivroStatus.DISPONIVEL));
    }

    @Test
    @DisplayName("Deve lançar exceção ao devolver empréstimo já devolvido (RN08)")
    void testNaoDevolverEmprestimoJaDevolvido() {
        emprestimo.setDataDevolucaoEfetiva(LocalDate.now().minusDays(1));
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        EmprestimoException exception = assertThrows(EmprestimoException.class, () -> {
            emprestimoService.devolverLivro(1L);
        });

        assertTrue(exception.getMessage().contains("já foi devolvido"));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao devolver empréstimo inexistente")
    void testDevolverEmprestimoInexistente() {
        when(emprestimoRepository.findById(999L)).thenReturn(Optional.empty());

        EmprestimoException exception = assertThrows(EmprestimoException.class, () -> {
            emprestimoService.devolverLivro(999L);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("Deve listar todos os empréstimos")
    void testListarTodosOsEmprestimos() {
        List<Emprestimo> emprestimos = List.of(emprestimo);
        when(emprestimoRepository.findAll()).thenReturn(emprestimos);

        List<EmprestimoDTO> resultado = emprestimoService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNomePessoa());

        verify(emprestimoRepository).findAll();
    }

    @Test
    @DisplayName("Deve listar empréstimos ativos")
    void testListarEmprestimosAtivos() {
        List<Emprestimo> emprestimos = List.of(emprestimo);
        when(emprestimoRepository.findAllByDataDevolucaoEfetivaNula()).thenReturn(emprestimos);

        List<EmprestimoDTO> resultado = emprestimoService.listarAtivos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isAtivo());

        verify(emprestimoRepository).findAllByDataDevolucaoEfetivaNula();
    }

    @Test
    @DisplayName("Deve listar empréstimos atrasados")
    void testListarEmprestimosAtrasados() {
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().minusDays(1));
        List<Emprestimo> emprestimos = List.of(emprestimo);
        when(emprestimoRepository.findOverdueLoans(LocalDate.now())).thenReturn(emprestimos);

        List<EmprestimoDTO> resultado = emprestimoService.listarAtrasados();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).isAtrasado());

        verify(emprestimoRepository).findOverdueLoans(LocalDate.now());
    }

    @Test
    @DisplayName("Deve obter empréstimo por ID")
    void testObterEmprestimoPorId() {
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        EmprestimoDTO resultado = emprestimoService.obterPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNomePessoa());

        verify(emprestimoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter empréstimo inexistente")
    void testObterEmprestimoInexistente() {
        when(emprestimoRepository.findById(999L)).thenReturn(Optional.empty());

        EmprestimoException exception = assertThrows(EmprestimoException.class, () -> {
            emprestimoService.obterPorId(999L);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("Deve obter histórico de empréstimos de um livro")
    void testObterHistoricoLivro() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        List<Emprestimo> emprestimos = List.of(emprestimo);
        when(emprestimoRepository.findByLivroId(1L)).thenReturn(emprestimos);

        List<EmprestimoDTO> resultado = emprestimoService.obterHistoricoLivro(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Duna", resultado.get(0).getLivroTitulo());

        verify(livroRepository).findById(1L);
        verify(emprestimoRepository).findByLivroId(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao obter histórico de livro inexistente")
    void testObterHistoricoLivroInexistente() {
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());

        EmprestimoException exception = assertThrows(EmprestimoException.class, () -> {
            emprestimoService.obterHistoricoLivro(999L);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }

    @Test
    @DisplayName("Deve obter métricas do dashboard")
    void testGetDashboardMetrics() {
        List<Emprestimo> emprestimos = List.of(emprestimo);
        when(livroRepository.count()).thenReturn(10L);
        when(livroRepository.countByStatus(LivroStatus.DISPONIVEL)).thenReturn(5L);
        when(livroRepository.countByStatus(LivroStatus.EMPRESTADO)).thenReturn(5L);
        when(emprestimoRepository.countActiveLoans()).thenReturn(5L);
        when(emprestimoRepository.findOverdueLoans(LocalDate.now())).thenReturn(List.of());
        when(emprestimoRepository.findAll()).thenReturn(emprestimos);

        DashboardDTO resultado = emprestimoService.getDashboardMetrics();

        assertNotNull(resultado);
        assertEquals(10L, resultado.getTotalLivros());
        assertEquals(5L, resultado.getTotalDisponivel());
        assertEquals(5L, resultado.getTotalEmprestado());
        assertEquals(5L, resultado.getEmprestimosAtivos());
        assertEquals(0L, resultado.getEmprestimosAtrasados());
        assertEquals(1, resultado.getEmprestimosList().size());

        verify(livroRepository).count();
        verify(livroRepository, times(2)).countByStatus(any(LivroStatus.class));
        verify(emprestimoRepository).countActiveLoans();
        verify(emprestimoRepository).findOverdueLoans(LocalDate.now());
    }

    @Test
    @DisplayName("Deve validar data de devolução prevista no futuro")
    void testDataDevolucaoPrevistaNoFuturo() {
        request.setDataDevolucaoPrevista(LocalDate.now().plusDays(14));
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        EmprestimoDTO resultado = emprestimoService.emprestar(request);

        assertTrue(resultado.getDataDevolucaoPrevista().isAfter(LocalDate.now()));
    }

    @Test
    @DisplayName("Deve conter informações do livro no DTO")
    void testEmprestimoDTOContemInformacoesLivro() {
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimo);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        EmprestimoDTO resultado = emprestimoService.emprestar(request);

        assertEquals("Duna", resultado.getLivroTitulo());
        assertEquals("Frank Herbert", resultado.getLivroAutor());
        assertEquals("978-0-553-29438-0", resultado.getLivroIsbn());
    }

    @Test
    @DisplayName("Deve identificar empréstimo como atrasado")
    void testIdentificarEmprestimoAtrasado() {
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().minusDays(1));
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        EmprestimoDTO resultado = emprestimoService.obterPorId(1L);

        assertTrue(resultado.isAtrasado());
    }

    @Test
    @DisplayName("Deve não considerar empréstimo devolvido como ativo")
    void testEmprestimoDevolvidiNaoEhAtivo() {
        emprestimo.setDataDevolucaoEfetiva(LocalDate.now());
        when(emprestimoRepository.findById(1L)).thenReturn(Optional.of(emprestimo));

        EmprestimoDTO resultado = emprestimoService.obterPorId(1L);

        assertFalse(resultado.isAtivo());
    }
}
