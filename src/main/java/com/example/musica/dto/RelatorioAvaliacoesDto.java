package com.example.musica.dto;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Totais e contagens das avaliações criadas no período")
public record RelatorioAvaliacoesDto(
        @Schema(example = "2026-09-01") LocalDate inicio,
        @Schema(example = "2026-11-30") LocalDate fim,
        @Schema(example = "9") Long totalAvaliacoes,
        @Schema(example = "5") Long avaliacoesDeAlbuns,
        @Schema(example = "4") Long avaliacoesDeMusicas,
        @Schema(description = "Média das notas do período; nula quando não há avaliações", example = "4.44")
        Double notaMedia,
        @Schema(example = "3") Long usuariosQueAvaliaram,
        @Schema(description = "Quantidade de avaliações para cada nota, de 0,5 a 5")
        List<NotaQuantidadeDto> distribuicaoNotas,
        @Schema(description = "Avaliações por gênero do álbum (a de uma música conta para o gênero do álbum dela). "
                + "Um álbum com dois gêneros conta nos dois, então a soma pode passar do total")
        List<GeneroRelatorioDto> porGenero,
        @Schema(description = "Até 5 álbuns com mais avaliações no período")
        List<AlbumRelatorioDto> albunsMaisAvaliados,
        @Schema(description = "Até 5 músicas com mais avaliações no período")
        List<MusicaRelatorioDto> musicasMaisAvaliadas
) {

    public RelatorioAvaliacoesDto {
        notaMedia = arredondar(notaMedia);
    }

    // mesmas 2 casas decimais da notaMedia de álbuns e músicas
    static Double arredondar(Double valor) {
        return valor == null ? null : Math.round(valor * 100) / 100.0;
    }
}
