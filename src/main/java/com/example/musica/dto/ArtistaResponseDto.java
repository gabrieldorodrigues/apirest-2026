package com.example.musica.dto;

import java.io.Serializable;

// Serializable: vai dentro do AlbumResponseDto para o cache no Redis
public record ArtistaResponseDto(
        Long id,
        String nome,
        String pais,
        Integer anoInicio
) implements Serializable {}