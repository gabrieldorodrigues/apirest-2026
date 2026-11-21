package com.example.musica.mapper;

import org.springframework.stereotype.Component;

import com.example.musica.dto.AvaliacaoResponseDto;
import com.example.musica.model.Avaliacao;

@Component
public class AvaliacaoMapper {

    public AvaliacaoResponseDto toDto(Avaliacao avaliacao) {
        return new AvaliacaoResponseDto(
                avaliacao.getId(),
                avaliacao.getUsuario().getId(),
                avaliacao.getUsuario().getNome(),
                avaliacao.getAlbum() != null ? avaliacao.getAlbum().getId() : null,
                avaliacao.getAlbum() != null ? avaliacao.getAlbum().getTitulo() : null,
                avaliacao.getMusica() != null ? avaliacao.getMusica().getId() : null,
                avaliacao.getMusica() != null ? avaliacao.getMusica().getTitulo() : null,
                avaliacao.getNota(),
                avaliacao.getResenha(),
                avaliacao.getCriadaEm(),
                avaliacao.getAtualizadaEm()
        );
    }
}