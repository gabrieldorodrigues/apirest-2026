package com.example.musica.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.musica.dto.MusicaRequestDto;
import com.example.musica.dto.MusicaResponseDto;
import com.example.musica.exception.ConflitoException;
import com.example.musica.exception.RecursoNaoEncontradoException;
import com.example.musica.mapper.MusicaMapper;
import com.example.musica.model.Musica;
import com.example.musica.repository.AvaliacaoRepository;
import com.example.musica.repository.MusicaRepository;

@Service
public class MusicaService {

    private final MusicaRepository musicaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AlbumService albumService;
    private final MusicaMapper musicaMapper;

    public MusicaService(MusicaRepository musicaRepository, AvaliacaoRepository avaliacaoRepository,
            AlbumService albumService, MusicaMapper musicaMapper) {
        this.musicaRepository = musicaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.albumService = albumService;
        this.musicaMapper = musicaMapper;
    }

    @Transactional(readOnly = true)
    public Page<MusicaResponseDto> listar(String titulo, Long albumId, Pageable pageable) {
        return musicaRepository.buscar(Filtro.like(titulo), albumId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public MusicaResponseDto buscar(Long id) {
        return toDto(buscarEntidade(id));
    }

    @Transactional
    public MusicaResponseDto criar(MusicaRequestDto dto) {
        if (musicaRepository.existsByAlbumIdAndNumeroFaixa(dto.albumId(), dto.numeroFaixa())) {
            throw new ConflitoException("Já existe a faixa " + dto.numeroFaixa() + " neste álbum");
        }
        Musica musica = new Musica();
        copiar(dto, musica);
        return toDto(musicaRepository.save(musica));
    }

    @Transactional
    public MusicaResponseDto atualizar(Long id, MusicaRequestDto dto) {
        Musica musica = buscarEntidade(id);
        if (musicaRepository.existsByAlbumIdAndNumeroFaixaAndIdNot(dto.albumId(), dto.numeroFaixa(), id)) {
            throw new ConflitoException("Já existe a faixa " + dto.numeroFaixa() + " neste álbum");
        }
        copiar(dto, musica);
        return toDto(musicaRepository.save(musica));
    }

    @Transactional
    public void remover(Long id) {
        Musica musica = buscarEntidade(id);
        if (avaliacaoRepository.existsByMusicaId(id)) {
            throw new ConflitoException("Não é possível remover uma música que possui avaliações");
        }
        musicaRepository.delete(musica);
    }

    Musica buscarEntidade(Long id) {
        return musicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Música", id));
    }

    private void copiar(MusicaRequestDto dto, Musica musica) {
        musica.setTitulo(dto.titulo());
        musica.setNumeroFaixa(dto.numeroFaixa());
        musica.setDuracaoSegundos(dto.duracaoSegundos());
        musica.setAlbum(albumService.buscarEntidade(dto.albumId()));
    }

    private MusicaResponseDto toDto(Musica musica) {
        return musicaMapper.toDto(musica, avaliacaoRepository.mediaDaMusica(musica.getId()));
    }
}