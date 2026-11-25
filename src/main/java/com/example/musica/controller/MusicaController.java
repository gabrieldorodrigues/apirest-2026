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

import com.example.musica.dto.MusicaRequestDto;
import com.example.musica.dto.MusicaResponseDto;
import com.example.musica.service.MusicaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final MusicaService musicaService;

    public MusicaController(MusicaService musicaService) {
        this.musicaService = musicaService;
    }

    @GetMapping
    public Page<MusicaResponseDto> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Long albumId,
            @PageableDefault(size = 10, sort = "numeroFaixa") Pageable pageable) {
        return musicaService.listar(titulo, albumId, pageable);
    }

    @GetMapping("/{id}")
    public MusicaResponseDto buscar(@PathVariable Long id) {
        return musicaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MusicaResponseDto criar(@RequestBody @Valid MusicaRequestDto dto) {
        return musicaService.criar(dto);
    }

    @PutMapping("/{id}")
    public MusicaResponseDto atualizar(@PathVariable Long id, @RequestBody @Valid MusicaRequestDto dto) {
        return musicaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        musicaService.remover(id);
    }
}