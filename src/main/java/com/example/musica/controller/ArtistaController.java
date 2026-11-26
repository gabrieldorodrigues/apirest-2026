package com.example.musica.controller;

import org.springdoc.core.annotations.ParameterObject;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Artistas")
@RestController
@RequestMapping("/artistas")
public class ArtistaController {

    private final ArtistaService artistaService;

    public ArtistaController(ArtistaService artistaService) {
        this.artistaService = artistaService;
    }

    @Operation(summary = "Lista artistas", description = "Filtros opcionais e combináveis. Ordenação padrão: `nome`.")
    @ApiResponse(responseCode = "200", description = "Página de artistas")
    @ApiResponse(responseCode = "400", description = "Campo de `sort` inexistente")
    @GetMapping
    public Page<ArtistaResponseDto> listar(
            @Parameter(description = "Parte do nome, sem diferenciar maiúsculas", example = "radio")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Parte do país, sem diferenciar maiúsculas", example = "brasil")
            @RequestParam(required = false) String pais,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return artistaService.listar(nome, pais, pageable);
    }

    @Operation(summary = "Busca um artista pelo id")
    @ApiResponse(responseCode = "200", description = "Artista encontrado")
    @ApiResponse(responseCode = "400", description = "Id inválido na rota")
    @ApiResponse(responseCode = "404", description = "Artista não existe")
    @GetMapping("/{id}")
    public ArtistaResponseDto buscar(@Parameter(description = "Id do artista", example = "1") @PathVariable Long id) {
        return artistaService.buscar(id);
    }

    @Operation(summary = "Cadastra um artista")
    @ApiResponse(responseCode = "201", description = "Artista criado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistaResponseDto criar(@RequestBody @Valid ArtistaRequestDto dto) {
        return artistaService.criar(dto);
    }

    @Operation(summary = "Atualiza um artista")
    @ApiResponse(responseCode = "200", description = "Artista atualizado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Artista não existe")
    @PutMapping("/{id}")
    public ArtistaResponseDto atualizar(@Parameter(description = "Id do artista", example = "1") @PathVariable Long id,
            @RequestBody @Valid ArtistaRequestDto dto) {
        return artistaService.atualizar(id, dto);
    }

    @Operation(summary = "Remove um artista", description = "Só é possível remover artistas sem álbuns cadastrados.")
    @ApiResponse(responseCode = "204", description = "Artista removido")
    @ApiResponse(responseCode = "404", description = "Artista não existe")
    @ApiResponse(responseCode = "409", description = "Artista possui álbuns")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@Parameter(description = "Id do artista", example = "4") @PathVariable Long id) {
        artistaService.remover(id);
    }
}
