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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("LivroController Integration Tests")
class LivroControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private LivroRepository livroRepository;
    
    private Categoria categoria;
    private CreateLivroRequest request;
    
    @BeforeEach
    void setUp() {
        livroRepository.deleteAll();
        categoriaRepository.deleteAll();
        
        categoria = Categoria.builder()
                .nome("Ficção Científica")
                .descricao("Livros de ficção científica")
                .build();
        categoria = categoriaRepository.save(categoria);
        
        request = CreateLivroRequest.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .categoriaId(categoria.getId())
                .build();
    }
    
    @Test
    @DisplayName("GET /api/livros - Deve listar todos os livros")
    void testListarTodosOsLivros() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livroRepository.save(livro);
        
        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].titulo", is("Duna")));
    }
    
    @Test
    @DisplayName("GET /api/livros?categoria={id} - Deve filtrar por categoria")
    void testFiltrarPorCategoria() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livroRepository.save(livro);
        
        mockMvc.perform(get("/api/livros?categoria=" + categoria.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].categoriaNome", is("Ficção Científica")));
    }
    
    @Test
    @DisplayName("GET /api/livros?status=DISPONIVEL - Deve filtrar por status")
    void testFiltrarPorStatus() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livroRepository.save(livro);
        
        mockMvc.perform(get("/api/livros?status=DISPONIVEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    @DisplayName("GET /api/livros?categoria={id}&status=DISPONIVEL - Deve filtrar por ambos")
    void testFiltrarPorCategoriaEStatus() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livroRepository.save(livro);
        
        mockMvc.perform(get("/api/livros?categoria=" + categoria.getId() + "&status=DISPONIVEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    @DisplayName("GET /api/livros?titulo=Duna - Deve buscar por título")
    void testBuscarPorTitulo() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livroRepository.save(livro);
        
        mockMvc.perform(get("/api/livros?titulo=Duna"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    @DisplayName("GET /api/livros?autor=Frank - Deve buscar por autor")
    void testBuscarPorAutor() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livroRepository.save(livro);
        
        mockMvc.perform(get("/api/livros?autor=Frank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
    
    @Test
    @DisplayName("POST /api/livros - Deve criar livro com dados válidos (201 Created)")
    void testCriarLivro() throws Exception {
        String json = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo", is("Duna")))
                .andExpect(jsonPath("$.status", is("DISPONIVEL")));
    }
    
    @Test
    @DisplayName("POST /api/livros - Deve retornar 409 Conflict ao duplicar ISBN")
    void testCriarLivroComIsbnDuplicado() throws Exception {
        Livro livroExistente = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livroRepository.save(livroExistente);
        
        String json = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo", is("ISBN_DUPLICADO")));
    }
    
    @Test
    @DisplayName("POST /api/livros - Deve retornar 400 Bad Request ao informar categoria inexistente")
    void testCriarLivroComCategoriaInexistente() throws Exception {
        request.setCategoriaId(999L);
        String json = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo", is("CATEGORIA_NAO_ENCONTRADA")));
    }
    
    @Test
    @DisplayName("DELETE /api/livros/{id} - Deve deletar livro DISPONIVEL (204 No Content)")
    void testDeletarLivroDisponivel() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livro = livroRepository.save(livro);
        
        mockMvc.perform(delete("/api/livros/" + livro.getId()))
                .andExpect(status().isNoContent());
    }
    
    @Test
    @DisplayName("DELETE /api/livros/{id} - Deve retornar 400 ao deletar livro EMPRESTADO")
    void testDeletarLivroEmprestado() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.EMPRESTADO)
                .categoria(categoria)
                .build();
        livro = livroRepository.save(livro);
        
        mockMvc.perform(delete("/api/livros/" + livro.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo", is("LIVRO_EMPRESTADO")));
    }
    
    @Test
    @DisplayName("GET /api/livros/{id} - Deve obter livro por ID")
    void testObterPorId() throws Exception {
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.DISPONIVEL)
                .categoria(categoria)
                .build();
        livro = livroRepository.save(livro);
        
        mockMvc.perform(get("/api/livros/" + livro.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(livro.getId().intValue())))
                .andExpect(jsonPath("$.titulo", is("Duna")));
    }
    
    @Test
    @DisplayName("GET /api/livros/{id} - Deve retornar 404 para livro inexistente")
    void testObterLivroInexistente() throws Exception {
        mockMvc.perform(get("/api/livros/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("POST /api/livros - Deve validar título mínimo (3 caracteres)")
    void testCriarLivroComTituloInvalido() throws Exception {
        request.setTitulo("AB");
        String json = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("POST /api/livros - Deve validar ano entre 1900 e 2100")
    void testCriarLivroComAnoInvalido() throws Exception {
        request.setAno(1800);
        String json = objectMapper.writeValueAsString(request);
        
        mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("POST /api/livros - RN03: Novo livro deve ter status DISPONIVEL")
    void testNovoLivroTemStatusDisponivel() throws Exception {
        String json = objectMapper.writeValueAsString(request);
        
        MvcResult result = mockMvc.perform(post("/api/livros")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andReturn();
        
        String responseBody = result.getResponse().getContentAsString();
        LivroDTO livroDTO = objectMapper.readValue(responseBody, LivroDTO.class);
        
        assert livroDTO.getStatus() == LivroStatus.DISPONIVEL;
    }
}
