package com.example.musica.mapper;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.example.musica.dto.AlbumResponseDto;
import com.example.musica.model.Album;

@Component
public class AlbumMapper {

    private final ArtistaMapper artistaMapper;
    private final GeneroMapper generoMapper;

    public AlbumMapper(ArtistaMapper artistaMapper, GeneroMapper generoMapper) {
        this.artistaMapper = artistaMapper;
        this.generoMapper = generoMapper;
    }

    public AlbumResponseDto toDto(Album album, Double notaMedia) {
        return new AlbumResponseDto(
                album.getId(),
                album.getTitulo(),
                album.getAnoLancamento(),
                album.getCapaUrl(),
                artistaMapper.toDto(album.getArtista()),
                album.getGeneros().stream()
                        .map(generoMapper::toDto)
                        .sorted(Comparator.comparing(g -> g.nome()))
                        .toList(),
                arredondar(notaMedia)
        );
    }

    static Double arredondar(Double valor) {
        return valor == null ? null : Math.round(valor * 100) / 100.0;
    }
}