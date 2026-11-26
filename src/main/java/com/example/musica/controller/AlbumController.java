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

import com.example.musica.dto.AlbumRequestDto;
import com.example.musica.dto.AlbumResponseDto;
import com.example.musica.service.AlbumService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Álbuns")
@RestController
@RequestMapping("/albuns")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @Operation(summary = "Lista álbuns",
            description = "Filtros opcionais e combináveis. Cada álbum traz a `notaMedia` das avaliações. Ordenação padrão: `titulo`.")
    @ApiResponse(responseCode = "200", description = "Página de álbuns")
    @ApiResponse(responseCode = "400", description = "Campo de `sort` inexistente")
    @GetMapping
    public Page<AlbumResponseDto> listar(
            @Parameter(description = "Parte do título, sem diferenciar maiúsculas", example = "computer")
            @RequestParam(required = false) String titulo,
            @Parameter(description = "Ano de lançamento exato", example = "1997")
            @RequestParam(required = false) Integer ano,
            @Parameter(description = "Id do artista", example = "1")
            @RequestParam(required = false) Long artistaId,
            @Parameter(description = "Id de um dos gêneros do álbum", example = "2")
            @RequestParam(required = false) Long generoId,
            @ParameterObject @PageableDefault(size = 10, sort = "titulo") Pageable pageable) {
        return albumService.listar(titulo, ano, artistaId, generoId, pageable);
    }

    @Operation(summary = "Busca um álbum pelo id", description = "Traz o artista, os gêneros e a `notaMedia`.")
    @ApiResponse(responseCode = "200", description = "Álbum encontrado")
    @ApiResponse(responseCode = "400", description = "Id inválido na rota")
    @ApiResponse(responseCode = "404", description = "Álbum não existe")
    @GetMapping("/{id}")
    public AlbumResponseDto buscar(@Parameter(description = "Id do álbum", example = "1") @PathVariable Long id) {
        return albumService.buscar(id);
    }

    @Operation(summary = "Cadastra um álbum", description = "O álbum pertence a um artista e tem pelo menos um gênero.")
    @ApiResponse(responseCode = "201", description = "Álbum criado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Artista ou gênero informado não existe")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponseDto criar(@RequestBody @Valid AlbumRequestDto dto) {
        return albumService.criar(dto);
    }

    @Operation(summary = "Atualiza um álbum")
    @ApiResponse(responseCode = "200", description = "Álbum atualizado")
    @ApiResponse(responseCode = "400", description = "Campos inválidos ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Álbum, artista ou gênero não existe")
    @PutMapping("/{id}")
    public AlbumResponseDto atualizar(@Parameter(description = "Id do álbum", example = "1") @PathVariable Long id,
            @RequestBody @Valid AlbumRequestDto dto) {
        return albumService.atualizar(id, dto);
    }

    @Operation(summary = "Remove um álbum", description = "Só é possível remover álbuns sem músicas e sem avaliações.")
    @ApiResponse(responseCode = "204", description = "Álbum removido")
    @ApiResponse(responseCode = "404", description = "Álbum não existe")
    @ApiResponse(responseCode = "409", description = "Álbum possui músicas ou avaliações")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@Parameter(description = "Id do álbum", example = "1") @PathVariable Long id) {
        albumService.remover(id);
    }
}
