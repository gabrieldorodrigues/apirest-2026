package com.example.musica.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.musica.config.CacheConfig;
import com.example.musica.dto.GeneroRequestDto;
import com.example.musica.dto.GeneroResponseDto;
import com.example.musica.exception.ConflitoException;
import com.example.musica.exception.RecursoNaoEncontradoException;
import com.example.musica.mapper.GeneroMapper;
import com.example.musica.model.Genero;
import com.example.musica.repository.AlbumRepository;
import com.example.musica.repository.GeneroRepository;

@Service
public class GeneroService {

    private final GeneroRepository generoRepository;
    private final AlbumRepository albumRepository;
    private final GeneroMapper generoMapper;

    public GeneroService(GeneroRepository generoRepository, AlbumRepository albumRepository,
            GeneroMapper generoMapper) {
        this.generoRepository = generoRepository;
        this.albumRepository = albumRepository;
        this.generoMapper = generoMapper;
    }

    @Transactional(readOnly = true)
    public Page<GeneroResponseDto> listar(String nome, Pageable pageable) {
        return generoRepository.findByNomeContainingIgnoreCase(nome == null ? "" : nome.trim(), pageable)
                .map(generoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public GeneroResponseDto buscar(Long id) {
        return generoMapper.toDto(buscarEntidade(id));
    }

    @Transactional
    public GeneroResponseDto criar(GeneroRequestDto dto) {
        if (generoRepository.existsByNomeIgnoreCase(dto.nome().trim())) {
            throw new ConflitoException("Já existe um gênero com o nome " + dto.nome());
        }
        Genero genero = new Genero(dto.nome().trim());
        return generoMapper.toDto(generoRepository.save(genero));
    }

    // o nome do gênero vai dentro da resposta de cada álbum, então todos os álbuns em cache ficam velhos
    @CacheEvict(cacheNames = { CacheConfig.ALBUM, CacheConfig.ALBUNS }, allEntries = true)
    @Transactional
    public GeneroResponseDto atualizar(Long id, GeneroRequestDto dto) {
        Genero genero = buscarEntidade(id);
        if (generoRepository.existsByNomeIgnoreCaseAndIdNot(dto.nome().trim(), id)) {
            throw new ConflitoException("Já existe um gênero com o nome " + dto.nome());
        }
        genero.setNome(dto.nome().trim());
        return generoMapper.toDto(generoRepository.save(genero));
    }

    @Transactional
    public void remover(Long id) {
        Genero genero = buscarEntidade(id);
        if (albumRepository.existsByGenerosId(id)) {
            throw new ConflitoException("Não é possível remover um gênero usado por álbuns");
        }
        generoRepository.delete(genero);
    }

    Genero buscarEntidade(Long id) {
        return generoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Gênero", id));
    }
}