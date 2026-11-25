package com.example.musica.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.musica.dto.AlbumRequestDto;
import com.example.musica.dto.AlbumResponseDto;
import com.example.musica.service.AlbumService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/albuns")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public Page<AlbumResponseDto> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Long artistaId,
            @RequestParam(required = false) Long generoId,
            @PageableDefault(size = 10, sort = "titulo") Pageable pageable) {
        return albumService.listar(titulo, ano, artistaId, generoId, pageable);
    }

    @GetMapping("/{id}")
    public AlbumResponseDto buscar(@PathVariable Long id) {
        return albumService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponseDto criar(@RequestBody @Valid AlbumRequestDto dto) {
        return albumService.criar(dto);
    }

    @PutMapping("/{id}")
    public AlbumResponseDto atualizar(@PathVariable Long id, @RequestBody @Valid AlbumRequestDto dto) {
        return albumService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        albumService.remover(id);
    }
}