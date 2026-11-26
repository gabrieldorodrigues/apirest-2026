package com.example.musica.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotaQuantidadeDto(
        @Schema(example = "4.5") BigDecimal nota,
        @Schema(example = "3") Long quantidade
) {}
