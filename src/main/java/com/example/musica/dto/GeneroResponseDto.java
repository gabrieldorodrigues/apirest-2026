package com.example.musica.dto;

import java.io.Serializable;

// Serializable: vai dentro do AlbumResponseDto para o cache no Redis
public record GeneroResponseDto(
        Long id,
        String nome
) implements Serializable {}