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

import com.example.musica.dto.GeneroRequestDto;
import com.example.musica.dto.GeneroResponseDto;
import com.example.musica.service.GeneroService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @GetMapping
    public Page<GeneroResponseDto> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return generoService.listar(nome, pageable);
    }

    @GetMapping("/{id}")
    public GeneroResponseDto buscar(@PathVariable Long id) {
        return generoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeneroResponseDto criar(@RequestBody @Valid GeneroRequestDto dto) {
        return generoService.criar(dto);
    }

    @PutMapping("/{id}")
    public GeneroResponseDto atualizar(@PathVariable Long id, @RequestBody @Valid GeneroRequestDto dto) {
        return generoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        generoService.remover(id);
    }
}