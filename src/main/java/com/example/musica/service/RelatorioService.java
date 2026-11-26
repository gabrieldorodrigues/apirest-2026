package com.example.musica.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.musica.dto.NotaQuantidadeDto;
import com.example.musica.dto.RelatorioAvaliacoesDto;
import com.example.musica.exception.RegraNegocioException;
import com.example.musica.repository.AvaliacaoRepository;
import com.example.musica.repository.ResumoPeriodo;

@Service
public class RelatorioService {

    private static final Logger log = LoggerFactory.getLogger(RelatorioService.class);
    private static final Pageable TOP_5 = PageRequest.of(0, 5);
    private static final BigDecimal MEIO = new BigDecimal("0.5");
    private static final BigDecimal NOTA_MAXIMA = new BigDecimal("5.0");

    private final AvaliacaoRepository avaliacaoRepository;

    public RelatorioService(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    // 5 consultas agregadas, qualquer que seja a quantidade de avaliações no período
    @Transactional(readOnly = true)
    public RelatorioAvaliacoesDto avaliacoes(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        // as datas são inclusivas: vai do início do dia inicial até o início do dia seguinte ao final
        LocalDateTime de = inicio.atStartOfDay();
        LocalDateTime ate = fim.plusDays(1).atStartOfDay();

        ResumoPeriodo resumo = avaliacaoRepository.resumoDoPeriodo(de, ate);
        RelatorioAvaliacoesDto relatorio = new RelatorioAvaliacoesDto(
                inicio,
                fim,
                resumo.total().longValue(),
                resumo.deAlbuns().longValue(),
                resumo.deMusicas().longValue(),
                resumo.notaMedia(),
                resumo.usuarios().longValue(),
                distribuicao(avaliacaoRepository.notasDoPeriodo(de, ate)),
                avaliacaoRepository.generosDoPeriodo(de, ate),
                avaliacaoRepository.albunsMaisAvaliados(de, ate, TOP_5),
                avaliacaoRepository.musicasMaisAvaliadas(de, ate, TOP_5));
        log.info("Relatório de avaliações de {} a {}: {} avaliações", inicio, fim, relatorio.totalAvaliacoes());
        return relatorio;
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new RegraNegocioException("A data inicial (" + inicio + ") é depois da data final (" + fim + ")");
        }
        if (fim.isAfter(inicio.plusYears(1))) {
            throw new RegraNegocioException("O período do relatório pode ter no máximo 1 ano");
        }
    }

    // o banco só devolve as notas que apareceram; as outras entram com quantidade 0, de 0,5 a 5
    private List<NotaQuantidadeDto> distribuicao(List<NotaQuantidadeDto> doBanco) {
        Map<BigDecimal, Long> porNota = doBanco.stream()
                .collect(Collectors.toMap(n -> n.nota().stripTrailingZeros(), NotaQuantidadeDto::quantidade));
        List<NotaQuantidadeDto> notas = new ArrayList<>();
        for (BigDecimal nota = MEIO; nota.compareTo(NOTA_MAXIMA) <= 0; nota = nota.add(MEIO)) {
            notas.add(new NotaQuantidadeDto(nota, porNota.getOrDefault(nota.stripTrailingZeros(), 0L)));
        }
        return notas;
    }
}
