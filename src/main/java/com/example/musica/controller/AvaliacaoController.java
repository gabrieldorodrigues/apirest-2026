package com.example.musica.controller;

import java.math.BigDecimal;

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

import com.example.musica.dto.AvaliacaoRequestDto;
import com.example.musica.dto.AvaliacaoResponseDto;
import com.example.musica.service.AvaliacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public Page<AvaliacaoResponseDto> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long albumId,
            @RequestParam(required = false) Long musicaId,
            @RequestParam(required = false) BigDecimal notaMinima,
            @PageableDefault(size = 10, sort = "criadaEm") Pageable pageable) {
        return avaliacaoService.listar(usuarioId, albumId, musicaId, notaMinima, pageable);
    }

    @GetMapping("/{id}")
    public AvaliacaoResponseDto buscar(@PathVariable Long id) {
        return avaliacaoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponseDto criar(@RequestBody @Valid AvaliacaoRequestDto dto) {
        return avaliacaoService.criar(dto);
    }

    @PutMapping("/{id}")
    public AvaliacaoResponseDto atualizar(@PathVariable Long id, @RequestBody @Valid AvaliacaoRequestDto dto) {
        return avaliacaoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        avaliacaoService.remover(id);
    }
}