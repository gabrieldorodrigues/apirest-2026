package com.example.musica.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.musica.dto.AlbumRequestDto;
import com.example.musica.dto.AlbumResponseDto;
import com.example.musica.exception.ConflitoException;
import com.example.musica.exception.RecursoNaoEncontradoException;
import com.example.musica.mapper.AlbumMapper;
import com.example.musica.model.Album;
import com.example.musica.model.Genero;
import com.example.musica.repository.AlbumRepository;
import com.example.musica.repository.AvaliacaoRepository;
import com.example.musica.repository.MusicaRepository;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final MusicaRepository musicaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ArtistaService artistaService;
    private final GeneroService generoService;
    private final AlbumMapper albumMapper;

    public AlbumService(AlbumRepository albumRepository, MusicaRepository musicaRepository,
            AvaliacaoRepository avaliacaoRepository, ArtistaService artistaService,
            GeneroService generoService, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.musicaRepository = musicaRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.artistaService = artistaService;
        this.generoService = generoService;
        this.albumMapper = albumMapper;
    }

    @Transactional(readOnly = true)
    public Page<AlbumResponseDto> listar(String titulo, Integer ano, Long artistaId, Long generoId,
            Pageable pageable) {
        return albumRepository.buscar(Filtro.like(titulo), ano, artistaId, generoId, pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public AlbumResponseDto buscar(Long id) {
        return toDto(buscarEntidade(id));
    }

    @Transactional
    public AlbumResponseDto criar(AlbumRequestDto dto) {
        Album album = new Album();
        copiar(dto, album);
        return toDto(albumRepository.save(album));
    }

    @Transactional
    public AlbumResponseDto atualizar(Long id, AlbumRequestDto dto) {
        Album album = buscarEntidade(id);
        copiar(dto, album);
        return toDto(albumRepository.save(album));
    }

    @Transactional
    public void remover(Long id) {
        Album album = buscarEntidade(id);
        if (musicaRepository.existsByAlbumId(id)) {
            throw new ConflitoException("Não é possível remover um álbum que possui músicas cadastradas");
        }
        if (avaliacaoRepository.existsByAlbumId(id)) {
            throw new ConflitoException("Não é possível remover um álbum que possui avaliações");
        }
        albumRepository.delete(album);
    }

    Album buscarEntidade(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Álbum", id));
    }

    private void copiar(AlbumRequestDto dto, Album album) {
        album.setTitulo(dto.titulo());
        album.setAnoLancamento(dto.anoLancamento());
        album.setCapaUrl(dto.capaUrl());
        album.setArtista(artistaService.buscarEntidade(dto.artistaId()));

        Set<Genero> generos = new HashSet<>();
        for (Long generoId : dto.generoIds()) {
            generos.add(generoService.buscarEntidade(generoId));
        }
        album.setGeneros(generos);
    }

    // a nota média não fica salva, é calculada a partir das avaliações a cada consulta
    private AlbumResponseDto toDto(Album album) {
        return albumMapper.toDto(album, avaliacaoRepository.mediaDoAlbum(album.getId()));
    }
}