package com.mylibrary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private long totalLivros;

    private long totalDisponivel;

    private long totalEmprestado;

    private long emprestimosAtivos;

    private long emprestimosAtrasados;

    private List<EmprestimoDTO> emprestimosList;
}
