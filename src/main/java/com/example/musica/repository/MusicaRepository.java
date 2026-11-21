package com.example.musica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.musica.model.Musica;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {

    @Query("""
            select m from Musica m
            where lower(m.titulo) like :titulo
              and (:albumId is null or m.album.id = :albumId)
            """)
    Page<Musica> buscar(String titulo, Long albumId, Pageable pageable);

    boolean existsByAlbumIdAndNumeroFaixa(Long albumId, Integer numeroFaixa);

    boolean existsByAlbumIdAndNumeroFaixaAndIdNot(Long albumId, Integer numeroFaixa, Long id);

    boolean existsByAlbumId(Long albumId);
}