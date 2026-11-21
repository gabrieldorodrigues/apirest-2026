package com.example.musica.dto;

public record ArtistaResponseDto(
        Long id,
        String nome,
        String pais,
        Integer anoInicio
) {}