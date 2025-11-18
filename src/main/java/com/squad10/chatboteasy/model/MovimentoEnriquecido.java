package com.squad10.chatboteasy.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class MovimentoEnriquecido {

    private LocalDate dataPagamento;
    private LocalDate dataEmissao;   // fallback
    private LocalDate dataPrevisao;  // fallback

    private String grupo;
    private String status;
    private BigDecimal valor;
    private String codCategoria;
    private String descCategoria;
    private String easyCode;
}
