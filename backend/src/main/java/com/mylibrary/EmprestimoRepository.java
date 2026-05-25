package com.mylibrary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    /**
     * Find all loans for a specific book
     */
    List<Emprestimo> findByLivroId(Long livroId);

    /**
     * Find all active loans (not returned)
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucaoEfetiva IS NULL")
    List<Emprestimo> findAllByDataDevolucaoEfetivaNula();

    /**
     * Find all overdue loans (due date passed and not yet returned)
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucaoPrevista < :today AND e.dataDevolucaoEfetiva IS NULL")
    List<Emprestimo> findOverdueLoans(LocalDate today);

    /**
     * Find all loans by status (active/returned)
     */
    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.dataDevolucaoEfetiva IS NULL")
    long countActiveLoans();
}
