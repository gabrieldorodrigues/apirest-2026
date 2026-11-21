package com.example.musica.mapper;

import org.springframework.stereotype.Component;

import com.example.musica.dto.MusicaResponseDto;
import com.example.musica.model.Musica;

@Component
public class MusicaMapper {

    public MusicaResponseDto toDto(Musica musica, Double notaMedia) {
        return new MusicaResponseDto(
                musica.getId(),
                musica.getTitulo(),
                musica.getNumeroFaixa(),
                musica.getDuracaoSegundos(),
                musica.getAlbum().getId(),
                musica.getAlbum().getTitulo(),
                AlbumMapper.arredondar(notaMedia)
        );
    }
}