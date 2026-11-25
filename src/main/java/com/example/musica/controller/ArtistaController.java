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

import com.example.musica.dto.ArtistaRequestDto;
import com.example.musica.dto.ArtistaResponseDto;
import com.example.musica.service.ArtistaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @GetMapping
    public Page<ArtistaResponseDto> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String pais,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return artistaService.listar(nome, pais, pageable);
    }

    @GetMapping("/{id}")
    public ArtistaResponseDto buscar(@PathVariable Long id) {
        return artistaService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistaResponseDto criar(@RequestBody @Valid ArtistaRequestDto dto) {
        return artistaService.criar(dto);
    }

    @PutMapping("/{id}")
    public ArtistaResponseDto atualizar(@PathVariable Long id, @RequestBody @Valid ArtistaRequestDto dto) {
        return artistaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        artistaService.remover(id);
    }
}