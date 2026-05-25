package com.mylibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LivroService Unit Tests")
class LivroServiceTest {
    
    @Mock
    private LivroRepository livroRepository;
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    @InjectMocks
    private LivroService livroService;
    
    private Categoria categoria;
    private Livro livro;
    private CreateLivroRequest request;
    
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
        
        request = CreateLivroRequest.builder()
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .categoriaId(1L)
                .build();
    }
    
    @Test
    @DisplayName("Deve criar livro com dados válidos")
    void testCriarLivroComDadosValidos() {
        when(livroRepository.findByIsbn("978-0-553-29438-0"))
                .thenReturn(Optional.empty());
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(livroRepository.save(any(Livro.class)))
                .thenReturn(livro);
        
        LivroDTO resultado = livroService.criar(request);
        
        assertNotNull(resultado);
        assertEquals("Duna", resultado.getTitulo());
        assertEquals("Frank Herbert", resultado.getAutor());
        assertEquals(LivroStatus.DISPONIVEL, resultado.getStatus());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar livro com ISBN duplicado")
    void testCriarLivroComIsbnDuplicado() {
        when(livroRepository.findByIsbn("978-0-553-29438-0"))
                .thenReturn(Optional.of(livro));
        
        LivroException exception = assertThrows(LivroException.class, 
                () -> livroService.criar(request));
        
        assertTrue(exception.getMessage().contains("já existe"));
        verify(livroRepository, never()).save(any(Livro.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar livro com categoria inexistente")
    void testCriarLivroComCategoriaInexistente() {
        when(livroRepository.findByIsbn("978-0-553-29438-0"))
                .thenReturn(Optional.empty());
        when(categoriaRepository.findById(999L))
                .thenReturn(Optional.empty());
        
        request.setCategoriaId(999L);
        
        LivroException exception = assertThrows(LivroException.class,
                () -> livroService.criar(request));
        
        assertTrue(exception.getMessage().contains("Categoria não encontrada"));
        verify(livroRepository, never()).save(any(Livro.class));
    }
    
    @Test
    @DisplayName("RN03: Status inicial deve ser DISPONIVEL")
    void testStatusInicialDisponivel() {
        when(livroRepository.findByIsbn("978-0-553-29438-0"))
                .thenReturn(Optional.empty());
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        when(livroRepository.save(any(Livro.class)))
                .thenReturn(livro);
        
        LivroDTO resultado = livroService.criar(request);
        
        assertEquals(LivroStatus.DISPONIVEL, resultado.getStatus());
    }
    
    @Test
    @DisplayName("Deve listar todos os livros")
    void testListarTodosOsLivros() {
        List<Livro> livros = List.of(livro);
        when(livroRepository.findAll())
                .thenReturn(livros);
        
        List<LivroDTO> resultado = livroService.listar();
        
        assertEquals(1, resultado.size());
        assertEquals("Duna", resultado.get(0).getTitulo());
    }
    
    @Test
    @DisplayName("Deve filtrar livros por categoria")
    void testFiltrarPorCategoria() {
        List<Livro> livros = List.of(livro);
        when(livroRepository.findByCategoriaId(1L))
                .thenReturn(livros);
        
        List<LivroDTO> resultado = livroService.listarComFiltros(1L, null);
        
        assertEquals(1, resultado.size());
        assertEquals("Duna", resultado.get(0).getTitulo());
    }
    
    @Test
    @DisplayName("Deve filtrar livros por status")
    void testFiltrarPorStatus() {
        List<Livro> livros = List.of(livro);
        when(livroRepository.findByStatus(LivroStatus.DISPONIVEL))
                .thenReturn(livros);
        
        List<LivroDTO> resultado = livroService.listarComFiltros(null, LivroStatus.DISPONIVEL);
        
        assertEquals(1, resultado.size());
        assertEquals(LivroStatus.DISPONIVEL, resultado.get(0).getStatus());
    }
    
    @Test
    @DisplayName("Deve filtrar livros por categoria E status")
    void testFiltrarPorCategoriaEStatus() {
        List<Livro> livros = List.of(livro);
        when(livroRepository.findByStatusAndCategoriaId(LivroStatus.DISPONIVEL, 1L))
                .thenReturn(livros);
        
        List<LivroDTO> resultado = livroService.listarComFiltros(1L, LivroStatus.DISPONIVEL);
        
        assertEquals(1, resultado.size());
    }
    
    @Test
    @DisplayName("RN04: Deve deletar livro com status DISPONIVEL")
    void testDeletarLivroDisponivel() {
        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));
        
        assertDoesNotThrow(() -> livroService.deletar(1L));
        
        verify(livroRepository, times(1)).delete(livro);
    }
    
    @Test
    @DisplayName("RN04 & RN05: Não deve deletar livro com status EMPRESTADO")
    void testNaoDeletarLivroEmprestado() {
        Livro livroEmprestado = Livro.builder()
                .id(1L)
                .titulo("Duna")
                .autor("Frank Herbert")
                .isbn("978-0-553-29438-0")
                .ano(1965)
                .status(LivroStatus.EMPRESTADO)
                .categoria(categoria)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
        
        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livroEmprestado));
        
        LivroException exception = assertThrows(LivroException.class,
                () -> livroService.deletar(1L));
        
        assertTrue(exception.getMessage().contains("Não é possível deletar"));
        assertTrue(exception.getMessage().contains("EMPRESTADO"));
        verify(livroRepository, never()).delete(any());
    }
    
    @Test
    @DisplayName("Deve buscar livro por título")
    void testBuscarPorTitulo() {
        List<Livro> livros = List.of(livro);
        when(livroRepository.findByTituloContainingIgnoreCase("Duna"))
                .thenReturn(livros);
        
        List<LivroDTO> resultado = livroService.buscar("Duna", null);
        
        assertEquals(1, resultado.size());
        assertEquals("Duna", resultado.get(0).getTitulo());
    }
    
    @Test
    @DisplayName("Deve buscar livro por autor")
    void testBuscarPorAutor() {
        List<Livro> livros = List.of(livro);
        when(livroRepository.findByAutorContainingIgnoreCase("Frank"))
                .thenReturn(livros);
        
        List<LivroDTO> resultado = livroService.buscar(null, "Frank");
        
        assertEquals(1, resultado.size());
        assertEquals("Frank Herbert", resultado.get(0).getAutor());
    }
    
    @Test
    @DisplayName("Deve obter livro por ID")
    void testObterPorId() {
        when(livroRepository.findById(1L))
                .thenReturn(Optional.of(livro));
        
        LivroDTO resultado = livroService.obterPorId(1L);
        
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Duna", resultado.getTitulo());
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao obter livro inexistente")
    void testObterLivroInexistente() {
        when(livroRepository.findById(999L))
                .thenReturn(Optional.empty());
        
        LivroException exception = assertThrows(LivroException.class,
                () -> livroService.obterPorId(999L));
        
        assertTrue(exception.getMessage().contains("não encontrado"));
    }
}
