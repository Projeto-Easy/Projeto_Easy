package com.squad10.chatboteasy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarNumeroRequest(
        @NotNull Long empresaId,
        @NotBlank String numero,
        String nome
) {}