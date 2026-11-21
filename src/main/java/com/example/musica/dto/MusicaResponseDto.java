package com.example.musica.dto;

public record MusicaResponseDto(
        Long id,
        String titulo,
        Integer numeroFaixa,
        Integer duracaoSegundos,
        Long albumId,
        String albumTitulo,
        Double notaMedia
) {}