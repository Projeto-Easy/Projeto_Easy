package com.squad10.chatboteasy.dto.report;

import com.squad10.chatboteasy.model.MovimentoEnriquecido;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class WeeklyReportDTO {
    private LocalDate inicio;
    private LocalDate fim;
    private boolean somentePagoOuRecebido;

    // Totais principais (pedidos no PDF)
    private BigDecimal totalRecebido;  // entradas
    private BigDecimal totalPago;      // saídas

    // Buckets Easy
    private BigDecimal receitaOperacional_1_0;
    private BigDecimal custosVariaveis_2_1;
    private BigDecimal despesasFixas_3x; // 3.0 + 3.1 + 3.2
    private BigDecimal resultadoOperacional; // 1.0 - 2.1 - 3.0 - 3.1 - 3.2

    // Opcional: levar itens para detalhamento (se não quiser, pode remover)
    private List<MovimentoEnriquecido> itens;
}
