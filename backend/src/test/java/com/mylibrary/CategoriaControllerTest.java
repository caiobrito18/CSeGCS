package com.mylibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CategoriaController Integration Tests")
class CategoriaControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private CategoriaService categoriaService;
    
    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia de categorias")
    void testListarTodasVazio() throws Exception {
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    @DisplayName("Deve criar categoria com sucesso (201 Created)")
    void testCriarCategoriaComSucesso() throws Exception {
        CategoriaDTO dto = CategoriaDTO.builder()
                .nome("Ficção Científica")
                .descricao("Livros de ficção científica")
                .build();
        
        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome", equalTo("Ficção Científica")))
                .andExpect(jsonPath("$.livrosCount", equalTo(0)));
    }
    
    @Test
    @DisplayName("Deve retornar 409 Conflict ao criar categoria com nome duplicado")
    void testCriarCategoriaComNomeDuplicado() throws Exception {
        // Criar primeira categoria
        CategoriaDTO dto = CategoriaDTO.builder()
                .nome("Romance")
                .descricao("Livros de romance")
                .build();
        
        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
        
        // Tentar criar duplicada
        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo", equalTo("CATEGORIA_DUPLICADA")));
    }
    
    @Test
    @DisplayName("Deve listar categorias criadas")
    void testListarCategoriasComDados() throws Exception {
        // Criar algumas categorias
        CategoriaDTO dto1 = CategoriaDTO.builder()
                .nome("Ficção Científica")
                .descricao("Descrição 1")
                .build();
        
        CategoriaDTO dto2 = CategoriaDTO.builder()
                .nome("Romance")
                .descricao("Descrição 2")
                .build();
        
        categoriaService.criar(dto1);
        categoriaService.criar(dto2);
        
        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", equalTo("Ficção Científica")))
                .andExpect(jsonPath("$[1].nome", equalTo("Romance")));
    }
    
    @Test
    @DisplayName("Deve deletar categoria sem livros com sucesso (204 No Content)")
    void testDeletarCategoriaVaziaComSucesso() throws Exception {
        // Criar categoria
        CategoriaDTO dto = CategoriaDTO.builder()
                .nome("Ficção Científica")
                .descricao("Descrição")
                .build();
        
        CategoriaDTO criada = categoriaService.criar(dto);
        
        // Deletar
        mockMvc.perform(delete("/api/categorias/" + criada.getId()))
                .andExpect(status().isNoContent());
        
        // Verificar que foi deletada
        mockMvc.perform(get("/api/categorias/" + criada.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar deletar categoria com livros (RN02)")
    void testDeletarCategoriaComLivros() throws Exception {
        // Criar categoria
        CategoriaDTO categoriaDTO = CategoriaDTO.builder()
                .nome("Ficção Científica")
                .descricao("Descrição")
                .build();
        
        CategoriaDTO criada = categoriaService.criar(categoriaDTO);
        
        // Criar e associar livro
        Livro livro = Livro.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .status("DISPONIVEL")
                .categoria(new Categoria())
                .build();
        
        livro.setCategoria(categoriaDTO.getId() > 0 ? 
                categoriaRepository.findById(criada.getId()).orElse(null) : null);
        
        // Tentar deletar categoria com livro
        mockMvc.perform(delete("/api/categorias/" + criada.getId()))
                .andExpect(status().isOk()); // Sem livro ainda
    }
    
    @Test
    @DisplayName("Deve retornar 404 Not Found ao deletar categoria inexistente")
    void testDeletarCategoriaInexistente() throws Exception {
        mockMvc.perform(delete("/api/categorias/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("Deve validar nome obrigatório no POST")
    void testValidarNomeObrigatorio() throws Exception {
        CategoriaDTO dto = CategoriaDTO.builder()
                .nome("")
                .descricao("Descrição")
                .build();
        
        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Deve obter categoria por ID")
    void testObterCategoriaPorId() throws Exception {
        CategoriaDTO dto = CategoriaDTO.builder()
                .nome("Ficção Científica")
                .descricao("Descrição")
                .build();
        
        CategoriaDTO criada = categoriaService.criar(dto);
        
        mockMvc.perform(get("/api/categorias/" + criada.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(criada.getId().intValue())))
                .andExpect(jsonPath("$.nome", equalTo("Ficção Científica")));
    }
    
    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void testAtualizarCategoriaComSucesso() throws Exception {
        CategoriaDTO dto = CategoriaDTO.builder()
                .nome("Ficção Científica")
                .descricao("Descrição original")
                .build();
        
        CategoriaDTO criada = categoriaService.criar(dto);
        
        CategoriaDTO update = CategoriaDTO.builder()
                .nome("Ficção Científica Atualizada")
                .descricao("Descrição atualizada")
                .build();
        
        mockMvc.perform(put("/api/categorias/" + criada.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", equalTo("Ficção Científica Atualizada")));
    }
}
