package com.squad10.chatboteasy.dto;

import com.squad10.chatboteasy.enums.StatusNumero;

public record AtualizarNumeroRequest(
        String nome,
        StatusNumero status,
        String numero
) {}