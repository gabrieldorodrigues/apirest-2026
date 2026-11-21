package com.example.musica.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

public record AvaliacaoResponseDto(
        Long id,
        Long usuarioId,
        String usuarioNome,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long albumId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String albumTitulo,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long musicaId,
        @JsonInclude(JsonInclude.Include.NON_NULL) String musicaTitulo,
        BigDecimal nota,
        String resenha,
        LocalDateTime criadaEm,
        LocalDateTime atualizadaEm
) {}