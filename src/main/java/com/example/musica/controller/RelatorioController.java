package com.example.musica.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.musica.dto.RelatorioAvaliacoesDto;
import com.example.musica.service.RelatorioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Relatórios")
@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @Operation(summary = "Relatório das avaliações de um período",
            description = """
                    Totais e contagens das avaliações criadas entre `inicio` e `fim` (as duas datas entram no período): \
                    total, avaliações de álbuns e de músicas, nota média, usuários que avaliaram, distribuição das notas, \
                    avaliações por gênero e os 5 álbuns e as 5 músicas mais avaliados. \
                    O período pode ter no máximo 1 ano. Sem avaliações no período, as contagens vêm zeradas.""")
    @ApiResponse(responseCode = "200", description = "Relatório do período")
    @ApiResponse(responseCode = "400", description = "`inicio` ou `fim` ausente, ou data fora do formato AAAA-MM-DD")
    @ApiResponse(responseCode = "422", description = "Data inicial depois da final, ou período maior que 1 ano")
    @GetMapping("/avaliacoes")
    public RelatorioAvaliacoesDto avaliacoes(
            @Parameter(description = "Data inicial, inclusiva (AAAA-MM-DD)", example = "2026-09-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @Parameter(description = "Data final, inclusiva (AAAA-MM-DD)", example = "2026-11-30")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return relatorioService.avaliacoes(inicio, fim);
    }
}
