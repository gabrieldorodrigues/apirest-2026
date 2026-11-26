package com.example.musica.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.musica.dto.AvaliacaoRequestDto;
import com.example.musica.dto.AvaliacaoResponseDto;
import com.example.musica.exception.ConflitoException;
import com.example.musica.exception.RecursoNaoEncontradoException;
import com.example.musica.exception.RegraNegocioException;
import com.example.musica.mapper.AvaliacaoMapper;
import com.example.musica.model.Avaliacao;
import com.example.musica.repository.AvaliacaoRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class AvaliacaoService {

    private static final Logger log = LoggerFactory.getLogger(AvaliacaoService.class);
    private static final BigDecimal MEIO = new BigDecimal("0.5");

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioService usuarioService;
    private final AlbumService albumService;
    private final MusicaService musicaService;
    private final AvaliacaoMapper avaliacaoMapper;
    private final MeterRegistry meterRegistry;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, UsuarioService usuarioService,
            AlbumService albumService, MusicaService musicaService, AvaliacaoMapper avaliacaoMapper,
            MeterRegistry meterRegistry) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioService = usuarioService;
        this.albumService = albumService;
        this.musicaService = musicaService;
        this.avaliacaoMapper = avaliacaoMapper;
        this.meterRegistry = meterRegistry;

        // lido do banco a cada consulta em /actuator/metrics/musica.avaliacoes.total
        Gauge.builder("musica.avaliacoes.total", avaliacaoRepository, AvaliacaoRepository::count)
                .description("Total de avaliações cadastradas")
                .register(meterRegistry);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponseDto> listar(Long usuarioId, Long albumId, Long musicaId, BigDecimal notaMinima,
            Pageable pageable) {
        return avaliacaoRepository.buscar(usuarioId, albumId, musicaId, notaMinima, pageable)
                .map(avaliacaoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponseDto buscar(Long id) {
        return avaliacaoMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public AvaliacaoResponseDto criar(AvaliacaoRequestDto dto) {
        validarRegras(dto, null);
        Avaliacao avaliacao = new Avaliacao();
        copiar(dto, avaliacao);
        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        invalidarCacheDoAlbum(salva);

        String tipo = salva.getAlbum() != null ? "album" : "musica";
        Counter.builder("musica.avaliacoes.criadas")
                .description("Avaliações criadas desde que a API subiu")
                .tag("tipo", tipo)
                .register(meterRegistry)
                .increment();
        log.info("Avaliação {} criada: usuário {} avaliou {} {} com nota {}", salva.getId(), dto.usuarioId(), tipo,
                tipo.equals("album") ? dto.albumId() : dto.musicaId(), dto.nota());
        return avaliacaoMapper.toDto(salva);
    }

    @Transactional
    public AvaliacaoResponseDto atualizar(Long id, AvaliacaoRequestDto dto) {
        Avaliacao avaliacao = buscarEntidade(id);
        validarRegras(dto, id);
        // a avaliação pode ter trocado de álbum (ou de álbum para música): a média do álbum antigo também muda
        invalidarCacheDoAlbum(avaliacao);
        copiar(dto, avaliacao);
        invalidarCacheDoAlbum(avaliacao);
        // flush força o @PreUpdate a rodar agora, senão a resposta voltaria com o atualizadaEm antigo
        AvaliacaoResponseDto resposta = avaliacaoMapper.toDto(avaliacaoRepository.saveAndFlush(avaliacao));
        log.info("Avaliação {} atualizada: nota {}", id, dto.nota());
        return resposta;
    }

    @Transactional
    public void remover(Long id) {
        Avaliacao avaliacao = buscarEntidade(id);
        avaliacaoRepository.delete(avaliacao);
        invalidarCacheDoAlbum(avaliacao);
        log.info("Avaliação {} removida", id);
    }

    // a nota média do álbum muda; avaliação de música não entra na média do álbum
    private void invalidarCacheDoAlbum(Avaliacao avaliacao) {
        if (avaliacao.getAlbum() != null) {
            albumService.invalidarCache(avaliacao.getAlbum().getId());
        }
    }

    private Avaliacao buscarEntidade(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliação", id));
    }

    // id null = criação; no update ignora a própria avaliação na checagem de duplicidade
    private void validarRegras(AvaliacaoRequestDto dto, Long id) {
        boolean temAlbum = dto.albumId() != null;
        boolean temMusica = dto.musicaId() != null;
        // iguais = vieram os dois ou nenhum
        if (temAlbum == temMusica) {
            log.warn("Avaliação recusada (422): usuário {} informou álbum {} e música {}", dto.usuarioId(),
                    dto.albumId(), dto.musicaId());
            throw new RegraNegocioException("A avaliação deve ser de um álbum ou de uma música, nunca dos dois");
        }
        if (dto.nota().remainder(MEIO).compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Avaliação recusada (422): nota {} fora do passo de 0.5", dto.nota());
            throw new RegraNegocioException("A nota deve ser em passos de 0.5 (ex.: 3.0, 3.5, 4.0)");
        }

        boolean jaAvaliou;
        if (temAlbum) {
            jaAvaliou = id == null
                    ? avaliacaoRepository.existsByUsuarioIdAndAlbumId(dto.usuarioId(), dto.albumId())
                    : avaliacaoRepository.existsByUsuarioIdAndAlbumIdAndIdNot(dto.usuarioId(), dto.albumId(), id);
        } else {
            jaAvaliou = id == null
                    ? avaliacaoRepository.existsByUsuarioIdAndMusicaId(dto.usuarioId(), dto.musicaId())
                    : avaliacaoRepository.existsByUsuarioIdAndMusicaIdAndIdNot(dto.usuarioId(), dto.musicaId(), id);
        }
        if (jaAvaliou) {
            log.warn("Avaliação recusada (409): usuário {} já avaliou {} {}", dto.usuarioId(),
                    temAlbum ? "o álbum" : "a música", temAlbum ? dto.albumId() : dto.musicaId());
            throw new ConflitoException("Este usuário já avaliou " + (temAlbum ? "este álbum" : "esta música"));
        }
    }

    private void copiar(AvaliacaoRequestDto dto, Avaliacao avaliacao) {
        avaliacao.setUsuario(usuarioService.buscarEntidade(dto.usuarioId()));
        avaliacao.setAlbum(dto.albumId() != null ? albumService.buscarEntidade(dto.albumId()) : null);
        avaliacao.setMusica(dto.musicaId() != null ? musicaService.buscarEntidade(dto.musicaId()) : null);
        avaliacao.setNota(dto.nota());
        avaliacao.setResenha(dto.resenha());
    }
}