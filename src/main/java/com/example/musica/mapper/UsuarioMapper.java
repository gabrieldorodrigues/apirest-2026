package com.example.musica.mapper;

import org.springframework.stereotype.Component;

import com.example.musica.dto.UsuarioResponseDto;
import com.example.musica.model.Usuario;

@Component
public class UsuarioMapper {

    public UsuarioResponseDto toDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCriadoEm()
        );
    }
}