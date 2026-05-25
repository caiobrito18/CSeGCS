package com.mylibrary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("EmprestimoController Integration Tests")
class EmprestimoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    private Categoria categoria;
    private Livro livro;
    private EmprestimoRequest request;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        livroRepository.deleteAll();
        categoriaRepository.deleteAll();

        categoria = Categoria.builder()
                .nome("Ficção Científica")
                .descricao("Livros de ficção científica")
                .build();
        categoria = categoriaRepository.save(categoria);

        livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livro = livroRepository.save(livro);

        request = EmprestimoRequest.builder()
                .livroId(livro.getId())
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
    }

    @Test
    @DisplayName("GET /api/emprestimos - Deve listar todos os empréstimos")
    void testListarTodosOsEmprestimos() throws Exception {
        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
        emprestimoRepository.save(emprestimo);

        mockMvc.perform(get("/api/emprestimos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nomePessoa", is("João Silva")))
                .andExpect(jsonPath("$[0].ativo", is(true)));
    }

    @Test
    @DisplayName("GET /api/emprestimos/ativos - Deve listar empréstimos ativos")
    void testListarEmprestimosAtivos() throws Exception {
        Emprestimo emprestimo1 = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
        emprestimoRepository.save(emprestimo1);

        Livro livro2 = Livro.builder()
                .titulo("Foundation")
                .autor("Isaac Asimov")
                .isbn("978-0-553-29438-1")
                .ano(1951)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livro2 = livroRepository.save(livro2);

        Emprestimo emprestimo2 = Emprestimo.builder()
                .livro(livro2)
                .nomePessoa("Maria Silva")
                .telefone("11987654322")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .dataDevolucaoEfetiva(LocalDate.now())
                .build();
        emprestimoRepository.save(emprestimo2);

        mockMvc.perform(get("/api/emprestimos/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nomePessoa", is("João Silva")))
                .andExpect(jsonPath("$[0].ativo", is(true)));
    }

    @Test
    @DisplayName("GET /api/emprestimos/atrasados - Deve listar empréstimos atrasados")
    void testListarEmprestimosAtrasados() throws Exception {
        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now().minusDays(15))
                .dataDevolucaoPrevista(LocalDate.now().minusDays(1))
                .build();
        emprestimoRepository.save(emprestimo);

        mockMvc.perform(get("/api/emprestimos/atrasados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nomePessoa", is("João Silva")))
                .andExpect(jsonPath("$[0].atrasado", is(true)));
    }

    @Test
    @DisplayName("GET /api/emprestimos/{id} - Deve obter empréstimo por ID")
    void testObterEmprestimoPorId() throws Exception {
        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
        emprestimo = emprestimoRepository.save(emprestimo);

        mockMvc.perform(get("/api/emprestimos/" + emprestimo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomePessoa", is("João Silva")))
                .andExpect(jsonPath("$.telefone", is("11987654321")))
                .andExpect(jsonPath("$.livroTitulo", is("Duna")));
    }

    @Test
    @DisplayName("GET /api/emprestimos/{id} - Deve retornar 404 para empréstimo inexistente")
    void testObterEmprestimoInexistente() throws Exception {
        mockMvc.perform(get("/api/emprestimos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve criar empréstimo com sucesso (201)")
    void testEmprestarComSucesso() throws Exception {
        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomePessoa", is("João Silva")))
                .andExpect(jsonPath("$.telefone", is("11987654321")))
                .andExpect(jsonPath("$.ativo", is(true)));

        // Verify livro status changed to EMPRESTADO
        Livro livroAtualizado = livroRepository.findById(livro.getId()).get();
        assert livroAtualizado.getStatus() == LivroStatus.EMPRESTADO;
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve retornar 409 ao emprestar livro EMPRESTADO")
    void testEmprestarLivroJaEmprestado() throws Exception {
        livro.setStatus(LivroStatus.EMPRESTADO);
        livroRepository.save(livro);

        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("CONFLICT_LIVRO_EMPRESTADO")));
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve retornar 404 para livro inexistente")
    void testEmprestarLivroInexistente() throws Exception {
        request.setLivroId(999L);

        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve retornar 400 para request inválido")
    void testEmprestarComRequestInvalido() throws Exception {
        EmprestimoRequest invalidRequest = EmprestimoRequest.builder()
                .livroId(null)
                .nomePessoa("")
                .telefone("")
                .dataDevolucaoPrevista(null)
                .build();

        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/emprestimos/{id}/devolver - Deve devolver livro com sucesso (204)")
    void testDevolverComSucesso() throws Exception {
        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
        livro.setStatus(LivroStatus.EMPRESTADO);
        livroRepository.save(livro);
        emprestimo = emprestimoRepository.save(emprestimo);

        mockMvc.perform(post("/api/emprestimos/" + emprestimo.getId() + "/devolver"))
                .andExpect(status().isNoContent());

        // Verify livro status changed to DISPONIVEL
        Livro livroAtualizado = livroRepository.findById(livro.getId()).get();
        assert livroAtualizado.getStatus() == LivroStatus.DISPONIVEL;
    }

    @Test
    @DisplayName("POST /api/emprestimos/{id}/devolver - Deve retornar 404 para empréstimo inexistente")
    void testDevolverEmprestimoInexistente() throws Exception {
        mockMvc.perform(post("/api/emprestimos/999/devolver"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/emprestimos/{id}/devolver - Deve retornar 400 ao devolver já devolvido")
    void testDevolverEmprestimoJaDevolvido() throws Exception {
        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now().minusDays(14))
                .dataDevolucaoPrevista(LocalDate.now())
                .dataDevolucaoEfetiva(LocalDate.now().minusDays(1))
                .build();
        emprestimo = emprestimoRepository.save(emprestimo);

        mockMvc.perform(post("/api/emprestimos/" + emprestimo.getId() + "/devolver"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/emprestimos/livro/{livroId} - Deve obter histórico do livro")
    void testObterHistoricoLivro() throws Exception {
        Emprestimo emprestimo1 = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now().minusDays(30))
                .dataDevolucaoPrevista(LocalDate.now().minusDays(16))
                .dataDevolucaoEfetiva(LocalDate.now().minusDays(15))
                .build();
        emprestimoRepository.save(emprestimo1);

        Emprestimo emprestimo2 = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("Maria Silva")
                .telefone("11987654322")
                .dataEmprestimo(LocalDate.now().minusDays(14))
                .dataDevolucaoPrevista(LocalDate.now())
                .build();
        emprestimoRepository.save(emprestimo2);

        mockMvc.perform(get("/api/emprestimos/livro/" + livro.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].livroTitulo", is("Duna")))
                .andExpect(jsonPath("$[1].livroTitulo", is("Duna")));
    }

    @Test
    @DisplayName("GET /api/emprestimos/livro/{livroId} - Deve retornar 404 para livro inexistente")
    void testObterHistoricoLivroInexistente() throws Exception {
        mockMvc.perform(get("/api/emprestimos/livro/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/dashboard - Deve retornar métricas do dashboard")
    void testGetDashboardMetrics() throws Exception {
        // Create multiple books
        Livro livro2 = Livro.builder()
                .titulo("Foundation")
                .autor("Isaac Asimov")
                .isbn("978-0-553-29438-1")
                .ano(1951)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livro2 = livroRepository.save(livro2);

        // Create loans
        Emprestimo emprestimo1 = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
        livro.setStatus(LivroStatus.EMPRESTADO);
        livroRepository.save(livro);
        emprestimoRepository.save(emprestimo1);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLivros", is(2)))
                .andExpect(jsonPath("$.totalDisponivel", is(1)))
                .andExpect(jsonPath("$.totalEmprestado", is(1)))
                .andExpect(jsonPath("$.emprestimosAtivos", is(1)))
                .andExpect(jsonPath("$.emprestimosAtrasados", is(0)))
                .andExpect(jsonPath("$.emprestimosList", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve validar nomePessoa obrigatório")
    void testEmprestarSemNomePessoa() throws Exception {
        request.setNomePessoa("");

        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve validar telefone obrigatório")
    void testEmprestarSemTelefone() throws Exception {
        request.setTelefone("");

        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve validar dataDevolucaoPrevista obrigatória")
    void testEmprestarSemDataDevolucaoPrevista() throws Exception {
        request.setDataDevolucaoPrevista(null);

        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve conter informações do livro no response")
    void testEmprestarRetornaInformacoesLivro() throws Exception {
        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.livroTitulo", is("Duna")))
                .andExpect(jsonPath("$.livroAutor", is("Frank Herbert")))
                .andExpect(jsonPath("$.livroIsbn", is("978-0-553-29438-0")));
    }

    @Test
    @DisplayName("POST /api/emprestimos/emprestar - Deve incrementar contador de empréstimos ativos")
    void testEmprestarIncrementaAtivos() throws Exception {
        mockMvc.perform(post("/api/emprestimos/emprestar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/emprestimos/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("POST /api/emprestimos/{id}/devolver - Deve remover de ativos após devolução")
    void testDevolverRemoveDeAtivos() throws Exception {
        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now())
                .dataDevolucaoPrevista(LocalDate.now().plusDays(14))
                .build();
        livro.setStatus(LivroStatus.EMPRESTADO);
        livroRepository.save(livro);
        emprestimo = emprestimoRepository.save(emprestimo);

        mockMvc.perform(post("/api/emprestimos/" + emprestimo.getId() + "/devolver"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/emprestimos/ativos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/dashboard - Deve identificar empréstimos atrasados")
    void testDashboardIdentificaAtrasados() throws Exception {
        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .nomePessoa("João Silva")
                .telefone("11987654321")
                .dataEmprestimo(LocalDate.now().minusDays(15))
                .dataDevolucaoPrevista(LocalDate.now().minusDays(1))
                .build();
        livro.setStatus(LivroStatus.EMPRESTADO);
        livroRepository.save(livro);
        emprestimoRepository.save(emprestimo);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emprestimosAtrasados", is(1)));
    }
}
