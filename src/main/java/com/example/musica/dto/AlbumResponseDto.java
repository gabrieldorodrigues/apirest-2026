package com.example.musica.dto;

import java.util.List;

public record AlbumResponseDto(
        Long id,
        String titulo,
        Integer anoLancamento,
        String capaUrl,
        ArtistaResponseDto artista,
        List<GeneroResponseDto> generos,
        Double notaMedia
) {}