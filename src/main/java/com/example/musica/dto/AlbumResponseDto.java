package com.example.musica.dto;

import java.io.Serializable;
import java.util.List;

// Serializable: o Redis guarda a resposta pronta (cache "album" e "albuns")
public record AlbumResponseDto(
        Long id,
        String titulo,
        Integer anoLancamento,
        String capaUrl,
        ArtistaResponseDto artista,
        List<GeneroResponseDto> generos,
        Double notaMedia
) implements Serializable {}