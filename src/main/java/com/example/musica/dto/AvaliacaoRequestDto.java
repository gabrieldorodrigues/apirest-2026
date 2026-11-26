package com.example.musica.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Avaliação de um álbum ou de uma música. Informe albumId ou musicaId, nunca os dois.")
public record AvaliacaoRequestDto(
        @Schema(description = "Id do usuário que avalia", example = "3")
        @NotNull(message = "O usuário é obrigatório")
        Long usuarioId,

        @Schema(description = "Id do álbum avaliado (deixe vazio ao avaliar uma música)", example = "1")
        Long albumId,

        @Schema(description = "Id da música avaliada (deixe vazio ao avaliar um álbum)", example = "null")
        Long musicaId,

        @Schema(description = "Nota de 0,5 a 5, em passos de 0,5", example = "4.5")
        @NotNull(message = "A nota é obrigatória")
        @DecimalMin(value = "0.5", message = "A nota mínima é 0.5")
        @DecimalMax(value = "5.0", message = "A nota máxima é 5")
        BigDecimal nota,

        @Schema(description = "Resenha opcional", example = "Atmosférico do começo ao fim.")
        @Size(max = 2000, message = "A resenha deve ter no máximo 2000 caracteres")
        String resenha
) {}