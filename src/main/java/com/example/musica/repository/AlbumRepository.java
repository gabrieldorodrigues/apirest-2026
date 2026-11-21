package com.example.musica.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.musica.model.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    // filtros opcionais: quando o parâmetro vem null a condição vira sempre verdadeira.
    // o gênero usa exists em vez de join pra não duplicar o álbum que tem vários gêneros
    @Query("""
            select a from Album a
            where lower(a.titulo) like :titulo
              and (:ano is null or a.anoLancamento = :ano)
              and (:artistaId is null or a.artista.id = :artistaId)
              and (:generoId is null or exists (select 1 from a.generos g where g.id = :generoId))
            """)
    Page<Album> buscar(String titulo, Integer ano, Long artistaId, Long generoId, Pageable pageable);

    boolean existsByArtistaId(Long artistaId);

    boolean existsByGenerosId(Long generoId);
}