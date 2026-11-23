package com.example.musica.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.musica.dto.ArtistaRequestDto;
import com.example.musica.dto.ArtistaResponseDto;
import com.example.musica.exception.ConflitoException;
import com.example.musica.exception.RecursoNaoEncontradoException;
import com.example.musica.mapper.ArtistaMapper;
import com.example.musica.model.Artista;
import com.example.musica.repository.AlbumRepository;
import com.example.musica.repository.ArtistaRepository;

@Service
public class ArtistaService {

    private final ArtistaRepository artistaRepository;
    private final AlbumRepository albumRepository;
    private final ArtistaMapper artistaMapper;

    public ArtistaService(ArtistaRepository artistaRepository, AlbumRepository albumRepository,
            ArtistaMapper artistaMapper) {
        this.artistaRepository = artistaRepository;
        this.albumRepository = albumRepository;
        this.artistaMapper = artistaMapper;
    }

    @Transactional(readOnly = true)
    public Page<ArtistaResponseDto> listar(String nome, String pais, Pageable pageable) {
        return artistaRepository.buscar(Filtro.like(nome), Filtro.like(pais), pageable)
                .map(artistaMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ArtistaResponseDto buscar(Long id) {
        return artistaMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public ArtistaResponseDto criar(ArtistaRequestDto dto) {
        Artista artista = new Artista();
        artistaMapper.copiar(dto, artista);
        return artistaMapper.toDto(artistaRepository.save(artista));
    }

    @Transactional
    public ArtistaResponseDto atualizar(Long id, ArtistaRequestDto dto) {
        Artista artista = buscarEntidade(id);
        artistaMapper.copiar(dto, artista);
        return artistaMapper.toDto(artistaRepository.save(artista));
    }

    @Transactional
    public void remover(Long id) {
        Artista artista = buscarEntidade(id);
        // checa antes pra devolver 409 com mensagem clara em vez de estourar a FK no banco
        if (albumRepository.existsByArtistaId(id)) {
            throw new ConflitoException("Não é possível remover um artista que possui álbuns cadastrados");
        }
        artistaRepository.delete(artista);
    }

    Artista buscarEntidade(Long id) {
        return artistaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Artista", id));
    }
}