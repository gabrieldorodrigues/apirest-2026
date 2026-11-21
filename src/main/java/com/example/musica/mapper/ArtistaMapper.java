package com.example.musica.mapper;

import org.springframework.stereotype.Component;

import com.example.musica.dto.ArtistaRequestDto;
import com.example.musica.dto.ArtistaResponseDto;
import com.example.musica.model.Artista;

@Component
public class ArtistaMapper {

    public void copiar(ArtistaRequestDto dto, Artista artista) {
        artista.setNome(dto.nome());
        artista.setPais(dto.pais());
        artista.setAnoInicio(dto.anoInicio());
    }

    public ArtistaResponseDto toDto(Artista artista) {
        return new ArtistaResponseDto(
                artista.getId(),
                artista.getNome(),
                artista.getPais(),
                artista.getAnoInicio()
        );
    }
}