package com.mylibrary;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaService {
    
    private final CategoriaRepository categoriaRepository;
    
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }
    
    /**
     * Listar todas as categorias com contagem de livros
     */
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaDTO::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Criar nova categoria
     * RN01: Nome deve ser único
     */
    public CategoriaDTO criar(CategoriaDTO dto) {
        // Validar nome único
        if (categoriaRepository.findByNomeIgnoreCase(dto.getNome()).isPresent()) {
            throw new CategoriaException("Categoria com nome '" + dto.getNome() + "' já existe");
        }
        
        try {
            Categoria categoria = new Categoria();
            categoria.setNome(dto.getNome().trim());
            categoria.setDescricao(dto.getDescricao());
            
            Categoria categoriasSalva = categoriaRepository.save(categoria);
            return new CategoriaDTO(categoriasSalva);
        } catch (DataIntegrityViolationException e) {
            throw new CategoriaException("Erro ao criar categoria: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obter categoria por ID
     */
    @Transactional(readOnly = true)
    public CategoriaDTO obterPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaException("Categoria não encontrada com ID: " + id));
        return new CategoriaDTO(categoria);
    }
    
    /**
     * Deletar categoria
     * RN02: Não pode deletar categoria que possui livros associados
     */
    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaException("Categoria não encontrada com ID: " + id));
        
        // Verificar se há livros associados
        if (!categoria.getLivros().isEmpty()) {
            throw new CategoriaException("Não é possível deletar categoria com " + categoria.getLivros().size() + " livro(s) associado(s)");
        }
        
        categoriaRepository.delete(categoria);
    }
    
    /**
     * Atualizar categoria
     */
    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaException("Categoria não encontrada com ID: " + id));
        
        // Se o nome foi mudado, verificar se já existe
        if (!categoria.getNome().equalsIgnoreCase(dto.getNome())) {
            if (categoriaRepository.findByNomeIgnoreCase(dto.getNome()).isPresent()) {
                throw new CategoriaException("Categoria com nome '" + dto.getNome() + "' já existe");
            }
            categoria.setNome(dto.getNome().trim());
        }
        
        categoria.setDescricao(dto.getDescricao());
        
        Categoria categoriaAtualizada = categoriaRepository.save(categoria);
        return new CategoriaDTO(categoriaAtualizada);
    }
}
