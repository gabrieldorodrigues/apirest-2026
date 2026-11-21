package com.example.musica.dto;

import java.time.LocalDateTime;

public record UsuarioResponseDto(
        Long id,
        String nome,
        String email,
        LocalDateTime criadoEm
) {}