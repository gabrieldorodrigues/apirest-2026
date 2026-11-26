package com.example.musica.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AlbumRelatorioDto(
        @Schema(example = "1") Long id,
        @Schema(example = "OK Computer") String titulo,
        @Schema(example = "Radiohead") String artista,
        @Schema(example = "2") Long avaliacoes,
        @Schema(example = "4.75") Double notaMedia
) {

    public AlbumRelatorioDto {
        notaMedia = RelatorioAvaliacoesDto.arredondar(notaMedia);
    }
}
