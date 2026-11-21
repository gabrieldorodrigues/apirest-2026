package com.example.musica.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequestDto(
        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        Long albumId,

        Long musicaId,

        @NotNull(message = "A nota é obrigatória")
        @DecimalMin(value = "0.5", message = "A nota mínima é 0.5")
        @DecimalMax(value = "5.0", message = "A nota máxima é 5")
        BigDecimal nota,

        @Size(max = 2000, message = "A resenha deve ter no máximo 2000 caracteres")
        String resenha
) {}