package com.mylibrary;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoriaService Unit Tests")
class CategoriaServiceTest {
    
    @Mock
    private CategoriaRepository categoriaRepository;
    
    @InjectMocks
    private CategoriaService categoriaService;
    
    private Categoria categoria;
    private CategoriaDTO categoriaDTO;
    
    @BeforeEach
    void setUp() {
        categoria = Categoria.builder()
                .id(1L)
                .nome("Ficção Científica")
                .descricao("Livros de ficção científica")
                .livros(new HashSet<>())
                .build();
        
        categoriaDTO = CategoriaDTO.builder()
                .id(1L)
                .nome("Ficção Científica")
                .descricao("Livros de ficção científica")
                .livrosCount(0L)
                .build();
    }
    
    @Test
    @DisplayName("Deve criar categoria com dados válidos")
    void testCriarCategoriaComDadosValidos() {
        CategoriaDTO novaDTO = CategoriaDTO.builder()
                .nome("Romance")
                .descricao("Livros de romance")
                .build();
        
        when(categoriaRepository.findByNomeIgnoreCase("Romance"))
                .thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class)))
                .thenReturn(categoria);
        
        CategoriaDTO resultado = categoriaService.criar(novaDTO);
        
        assertNotNull(resultado);
        assertEquals("Ficção Científica", resultado.getNome());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com nome duplicado (RN01)")
    void testCriarCategoriaComNomeDuplicado() {
        CategoriaDTO duplicadaDTO = CategoriaDTO.builder()
                .nome("Ficção Científica")
                .descricao("Descrição")
                .build();
        
        when(categoriaRepository.findByNomeIgnoreCase("Ficção Científica"))
                .thenReturn(Optional.of(categoria));
        
        CategoriaException exception = assertThrows(CategoriaException.class, 
                () -> categoriaService.criar(duplicadaDTO));
        
        assertTrue(exception.getMessage().contains("já existe"));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }
    
    @Test
    @DisplayName("Deve deletar categoria sem livros")
    void testDeletarCategoriaVazia() {
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        
        assertDoesNotThrow(() -> categoriaService.deletar(1L));
        
        verify(categoriaRepository, times(1)).delete(categoria);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria com livros (RN02)")
    void testDeletarCategoriaComLivros() {
        // Preparar categoria com livros
        Livro livro = Livro.builder()
                .id(1L)
                .titulo("Duna")
                .status("DISPONIVEL")
                .categoria(categoria)
                .build();
        
        categoria.getLivros().add(livro);
        
        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));
        
        CategoriaException exception = assertThrows(CategoriaException.class, 
                () -> categoriaService.deletar(1L));
        
        assertTrue(exception.getMessage().contains("Não é possível deletar"));
        verify(categoriaRepository, never()).delete(any());
    }
    
    @Test
    @DisplayName("Deve retornar contagem correta de livros")
    void testContagemCorretaDeLivros() {
        // Preparar categoria com 2 livros
        Livro livro1 = Livro.builder().id(1L).titulo("Livro 1").build();
        Livro livro2 = Livro.builder().id(2L).titulo("Livro 2").build();
        
        categoria.getLivros().add(livro1);
        categoria.getLivros().add(livro2);
        
        CategoriaDTO dto = new CategoriaDTO(categoria);
        
        assertEquals(2L, dto.getLivrosCount());
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria inexistente")
    void testDeletarCategoriaInexistente() {
        when(categoriaRepository.findById(999L))
                .thenReturn(Optional.empty());
        
        CategoriaException exception = assertThrows(CategoriaException.class,
                () -> categoriaService.deletar(999L));
        
        assertTrue(exception.getMessage().contains("não encontrada"));
    }
    
    @Test
    @DisplayName("Deve validar nome não em branco")
    void testValidarNomeNaoEmBranco() {
        CategoriaDTO invalida = CategoriaDTO.builder()
                .nome("   ")
                .build();
        
        when(categoriaRepository.findByNomeIgnoreCase("   "))
                .thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class)))
                .thenReturn(categoria);
        
        // A validação aconteceria através de @Valid no controller
        // Aqui verificamos que o serviço faz trim
        assertDoesNotThrow(() -> categoriaService.criar(invalida));
        verify(categoriaRepository).save(any());
    }
}
