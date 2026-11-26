package com.example.musica.repository;

/**
 * Totais das avaliações de um período, calculados numa consulta só pelo AvaliacaoRepository.
 * As contagens são Number porque o tipo que o banco devolve para count e sum muda entre H2 e PostgreSQL.
 */
public record ResumoPeriodo(
        Number total,
        Number deAlbuns,
        Number deMusicas,
        Double notaMedia,
        Number usuarios
) {}
