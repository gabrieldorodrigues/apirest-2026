package com.example.musica.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record GeneroRelatorioDto(
        @Schema(example = "1") Long id,
        @Schema(example = "Rock") String nome,
        @Schema(example = "5") Long avaliacoes,
        @Schema(example = "4.6") Double notaMedia
) {

    public GeneroRelatorioDto {
        notaMedia = RelatorioAvaliacoesDto.arredondar(notaMedia);
    }
}
