package com.example.musica.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MusicaRequestDto(
        @Schema(example = "Exit Music (For a Film)")
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        @Schema(description = "Número da faixa, único dentro do álbum", example = "4")
        @NotNull(message = "O número da faixa é obrigatório")
        @Positive(message = "O número da faixa deve ser maior que zero")
        Integer numeroFaixa,

        @Schema(description = "Duração em segundos", example = "264")
        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        Integer duracaoSegundos,

        @Schema(description = "Id do álbum da música", example = "1")
        @NotNull(message = "O álbum é obrigatório")
        Long albumId
) {}