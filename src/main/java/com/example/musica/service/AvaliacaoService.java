package com.example.musica.service;

import java.math.BigDecimal;

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

@Service
public class AvaliacaoService {

    private static final BigDecimal MEIO = new BigDecimal("0.5");

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioService usuarioService;
    private final AlbumService albumService;
    private final MusicaService musicaService;
    private final AvaliacaoMapper avaliacaoMapper;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, UsuarioService usuarioService,
            AlbumService albumService, MusicaService musicaService, AvaliacaoMapper avaliacaoMapper) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioService = usuarioService;
        this.albumService = albumService;
        this.musicaService = musicaService;
        this.avaliacaoMapper = avaliacaoMapper;
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
        return avaliacaoMapper.toDto(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public AvaliacaoResponseDto atualizar(Long id, AvaliacaoRequestDto dto) {
        Avaliacao avaliacao = buscarEntidade(id);
        validarRegras(dto, id);
        copiar(dto, avaliacao);
        // flush força o @PreUpdate a rodar agora, senão a resposta voltaria com o atualizadaEm antigo
        return avaliacaoMapper.toDto(avaliacaoRepository.saveAndFlush(avaliacao));
    }

    @Transactional
    public void remover(Long id) {
        avaliacaoRepository.delete(buscarEntidade(id));
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
            throw new RegraNegocioException("A avaliação deve ser de um álbum ou de uma música, nunca dos dois");
        }
        if (dto.nota().remainder(MEIO).compareTo(BigDecimal.ZERO) != 0) {
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