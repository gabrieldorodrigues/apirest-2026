package com.example.musica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MusicaRequestDto(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        @NotNull(message = "O número da faixa é obrigatório")
        @Positive(message = "O número da faixa deve ser maior que zero")
        Integer numeroFaixa,

        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        Integer duracaoSegundos,

        @NotNull(message = "O álbum é obrigatório")
        Long albumId
) {}