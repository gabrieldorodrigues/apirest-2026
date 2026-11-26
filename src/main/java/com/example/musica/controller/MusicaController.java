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

import com.example.musica.dto.MusicaRequestDto;
import com.example.musica.dto.MusicaResponseDto;
import com.example.musica.service.MusicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Músicas")
@RestController
@RequestMapping("/musicas")
public class MusicaController {

    private final MusicaService musicaService;

    public MusicaController(MusicaService musicaService) {
        this.musicaService = musicaService;
    }

    @Operation(summary = "Lista músicas",
            description = "Filtros opcionais e combináveis. Cada música traz a `notaMedia` das avaliações. Ordenação padrão: `numeroFaixa`.")
    @ApiResponse(responseCode = "200", description = "Página de músicas")
    @ApiResponse(responseCode = "400", description = "Campo de `sort` inexistente")
    @GetMapping
    public Page<MusicaResponseDto> listar(
            @Parameter(description = "Parte do título, sem diferenciar maiúsculas", example = "police")
            @RequestParam(required = false) String titulo,
            @Parameter(description = "Id do álbum, para listar as faixas de um álbum", example = "1")
            @RequestParam(required = false) Long albumId,
            @ParameterObject @PageableDefault(size = 10, sort = "numeroFaixa") Pageable pageable) {
        return musicaService.listar(titulo, albumId, pageable);
    }

    @Operation(summary = "Busca uma música pelo id")
    @ApiResponse(responseCode = "200", description = "Música encontrada")
    @ApiResponse(responseCode = "400", description = "Id inválido na rota")
    @ApiResponse(responseCode = "404", description = "Música não existe")
    @GetMapping("/{id}")
    public MusicaResponseDto buscar(@Parameter(description = "Id da música", example = "1") @PathVariable Long id) {
        return musicaService.buscar(id);
    }

    @Operation(summary = "Cadastra uma música", description = "O número da faixa é único dentro do álbum.")
    @ApiResponse(responseCode = "201", description = "Música criada")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Álbum informado não existe")
    @ApiResponse(responseCode = "409", description = "Já existe essa faixa no álbum")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MusicaResponseDto criar(@RequestBody @Valid MusicaRequestDto dto) {
        return musicaService.criar(dto);
    }

    @Operation(summary = "Atualiza uma música")
    @ApiResponse(responseCode = "200", description = "Música atualizada")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Música ou álbum não existe")
    @ApiResponse(responseCode = "409", description = "Já existe essa faixa no álbum")
    @PutMapping("/{id}")
    public MusicaResponseDto atualizar(@Parameter(description = "Id da música", example = "1") @PathVariable Long id,
            @RequestBody @Valid MusicaRequestDto dto) {
        return musicaService.atualizar(id, dto);
    }

    @Operation(summary = "Remove uma música", description = "Só é possível remover músicas sem avaliações.")
    @ApiResponse(responseCode = "204", description = "Música removida")
    @ApiResponse(responseCode = "404", description = "Música não existe")
    @ApiResponse(responseCode = "409", description = "Música possui avaliações")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@Parameter(description = "Id da música", example = "2") @PathVariable Long id) {
        musicaService.remover(id);
    }
}
