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

import com.example.musica.dto.GeneroRequestDto;
import com.example.musica.dto.GeneroResponseDto;
import com.example.musica.service.GeneroService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Gêneros")
@RestController
@RequestMapping("/generos")
public class GeneroController {

    private final GeneroService generoService;

    public GeneroController(GeneroService generoService) {
        this.generoService = generoService;
    }

    @Operation(summary = "Lista gêneros", description = "Ordenação padrão: `nome`.")
    @ApiResponse(responseCode = "200", description = "Página de gêneros")
    @ApiResponse(responseCode = "400", description = "Campo de `sort` inexistente")
    @GetMapping
    public Page<GeneroResponseDto> listar(
            @Parameter(description = "Parte do nome, sem diferenciar maiúsculas", example = "rock")
            @RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return generoService.listar(nome, pageable);
    }

    @Operation(summary = "Busca um gênero pelo id")
    @ApiResponse(responseCode = "200", description = "Gênero encontrado")
    @ApiResponse(responseCode = "400", description = "Id inválido na rota")
    @ApiResponse(responseCode = "404", description = "Gênero não existe")
    @GetMapping("/{id}")
    public GeneroResponseDto buscar(@Parameter(description = "Id do gênero", example = "1") @PathVariable Long id) {
        return generoService.buscar(id);
    }

    @Operation(summary = "Cadastra um gênero", description = "O nome é único, sem diferenciar maiúsculas.")
    @ApiResponse(responseCode = "201", description = "Gênero criado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "409", description = "Já existe um gênero com esse nome")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GeneroResponseDto criar(@RequestBody @Valid GeneroRequestDto dto) {
        return generoService.criar(dto);
    }

    @Operation(summary = "Atualiza um gênero")
    @ApiResponse(responseCode = "200", description = "Gênero atualizado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Gênero não existe")
    @ApiResponse(responseCode = "409", description = "Já existe um gênero com esse nome")
    @PutMapping("/{id}")
    public GeneroResponseDto atualizar(@Parameter(description = "Id do gênero", example = "1") @PathVariable Long id,
            @RequestBody @Valid GeneroRequestDto dto) {
        return generoService.atualizar(id, dto);
    }

    @Operation(summary = "Remove um gênero", description = "Só é possível remover gêneros que não classificam nenhum álbum.")
    @ApiResponse(responseCode = "204", description = "Gênero removido")
    @ApiResponse(responseCode = "404", description = "Gênero não existe")
    @ApiResponse(responseCode = "409", description = "Gênero usado por álbuns")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@Parameter(description = "Id do gênero", example = "1") @PathVariable Long id) {
        generoService.remover(id);
    }
}
