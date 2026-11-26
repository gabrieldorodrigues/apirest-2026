package com.example.musica.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AlbumRequestDto(
        @Schema(example = "Kid A")
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        @Schema(example = "2000")
        @NotNull(message = "O ano de lançamento é obrigatório")
        @Min(value = 1900, message = "O ano de lançamento deve ser a partir de 1900")
        @Max(value = 2100, message = "O ano de lançamento deve ser até 2100")
        Integer anoLancamento,

        @Schema(description = "URL da imagem da capa (opcional)", example = "https://exemplo.com/kid-a.jpg")
        @Size(max = 300, message = "A URL da capa deve ter no máximo 300 caracteres")
        String capaUrl,

        @Schema(description = "Id do artista do álbum", example = "1")
        @NotNull(message = "O artista é obrigatório")
        Long artistaId,

        @Schema(description = "Ids dos gêneros (pelo menos um)", example = "[1, 2]")
        @NotEmpty(message = "Informe pelo menos um gênero")
        Set<Long> generoIds
) {}