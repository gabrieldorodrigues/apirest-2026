package com.example.musica.controller;

import java.math.BigDecimal;

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

import com.example.musica.dto.AvaliacaoRequestDto;
import com.example.musica.dto.AvaliacaoResponseDto;
import com.example.musica.service.AvaliacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Avaliações")
@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private static final String REGRAS = """
            Regras: a avaliação aponta para um álbum **ou** para uma música (nunca os dois); \
            a nota vai de 0,5 a 5 em passos de 0,5; cada usuário avalia o mesmo álbum ou a mesma música uma vez só.""";

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @Operation(summary = "Lista avaliações",
            description = "Filtros opcionais e combináveis. Com `usuarioId` mostra o histórico do usuário. Ordenação padrão: `criadaEm`.")
    @ApiResponse(responseCode = "200", description = "Página de avaliações")
    @ApiResponse(responseCode = "400", description = "Campo de `sort` inexistente")
    @GetMapping
    public Page<AvaliacaoResponseDto> listar(
            @Parameter(description = "Id do usuário (histórico de avaliações)", example = "1")
            @RequestParam(required = false) Long usuarioId,
            @Parameter(description = "Id do álbum avaliado", example = "1")
            @RequestParam(required = false) Long albumId,
            @Parameter(description = "Id da música avaliada", example = "1")
            @RequestParam(required = false) Long musicaId,
            @Parameter(description = "Nota mínima (inclusiva)", example = "4.5")
            @RequestParam(required = false) BigDecimal notaMinima,
            @ParameterObject @PageableDefault(size = 10, sort = "criadaEm") Pageable pageable) {
        return avaliacaoService.listar(usuarioId, albumId, musicaId, notaMinima, pageable);
    }

    @Operation(summary = "Busca uma avaliação pelo id")
    @ApiResponse(responseCode = "200", description = "Avaliação encontrada")
    @ApiResponse(responseCode = "400", description = "Id inválido na rota")
    @ApiResponse(responseCode = "404", description = "Avaliação não existe")
    @GetMapping("/{id}")
    public AvaliacaoResponseDto buscar(@Parameter(description = "Id da avaliação", example = "1") @PathVariable Long id) {
        return avaliacaoService.buscar(id);
    }

    @Operation(summary = "Avalia um álbum ou uma música", description = REGRAS)
    @ApiResponse(responseCode = "201", description = "Avaliação criada")
    @ApiResponse(responseCode = "400", description = "Campos inválidos, nota fora de 0,5 a 5 ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Usuário, álbum ou música não existe")
    @ApiResponse(responseCode = "409", description = "Usuário já avaliou este álbum ou esta música")
    @ApiResponse(responseCode = "422", description = "Álbum e música ao mesmo tempo (ou nenhum dos dois), ou nota fora do passo de 0,5")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponseDto criar(@RequestBody @Valid AvaliacaoRequestDto dto) {
        return avaliacaoService.criar(dto);
    }

    @Operation(summary = "Atualiza uma avaliação", description = REGRAS)
    @ApiResponse(responseCode = "200", description = "Avaliação atualizada")
    @ApiResponse(responseCode = "400", description = "Campos inválidos, nota fora de 0,5 a 5 ou JSON mal formatado")
    @ApiResponse(responseCode = "404", description = "Avaliação, usuário, álbum ou música não existe")
    @ApiResponse(responseCode = "409", description = "Usuário já avaliou este álbum ou esta música em outra avaliação")
    @ApiResponse(responseCode = "422", description = "Álbum e música ao mesmo tempo (ou nenhum dos dois), ou nota fora do passo de 0,5")
    @PutMapping("/{id}")
    public AvaliacaoResponseDto atualizar(@Parameter(description = "Id da avaliação", example = "1") @PathVariable Long id,
            @RequestBody @Valid AvaliacaoRequestDto dto) {
        return avaliacaoService.atualizar(id, dto);
    }

    @Operation(summary = "Remove uma avaliação")
    @ApiResponse(responseCode = "204", description = "Avaliação removida")
    @ApiResponse(responseCode = "404", description = "Avaliação não existe")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@Parameter(description = "Id da avaliação", example = "9") @PathVariable Long id) {
        avaliacaoService.remover(id);
    }
}
