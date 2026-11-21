package com.example.musica.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArtistaRequestDto(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Size(max = 60, message = "O país deve ter no máximo 60 caracteres")
        String pais,

        @Min(value = 1900, message = "O ano de início deve ser a partir de 1900")
        @Max(value = 2100, message = "O ano de início deve ser até 2100")
        Integer anoInicio
) {}