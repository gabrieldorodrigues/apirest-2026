package com.example.musica.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MusicaRelatorioDto(
        @Schema(example = "1") Long id,
        @Schema(example = "Paranoid Android") String titulo,
        @Schema(example = "OK Computer") String album,
        @Schema(example = "Radiohead") String artista,
        @Schema(example = "1") Long avaliacoes,
        @Schema(example = "5.0") Double notaMedia
) {

    public MusicaRelatorioDto {
        notaMedia = RelatorioAvaliacoesDto.arredondar(notaMedia);
    }
}
