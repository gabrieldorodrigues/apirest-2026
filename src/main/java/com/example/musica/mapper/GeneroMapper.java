package com.example.musica.mapper;

import org.springframework.stereotype.Component;

import com.example.musica.dto.GeneroResponseDto;
import com.example.musica.model.Genero;

@Component
public class GeneroMapper {

    public GeneroResponseDto toDto(Genero genero) {
        return new GeneroResponseDto(genero.getId(), genero.getNome());
    }
}