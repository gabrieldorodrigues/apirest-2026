package com.example.musica.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.musica.model.Avaliacao;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("""
            select a from Avaliacao a
            where (:usuarioId is null or a.usuario.id = :usuarioId)
              and (:albumId is null or a.album.id = :albumId)
              and (:musicaId is null or a.musica.id = :musicaId)
              and (:notaMinima is null or a.nota >= :notaMinima)
            """)
    Page<Avaliacao> buscar(Long usuarioId, Long albumId, Long musicaId, BigDecimal notaMinima, Pageable pageable);

    @Query("select avg(a.nota) from Avaliacao a where a.album.id = :albumId")
    Double mediaDoAlbum(Long albumId);

    @Query("select avg(a.nota) from Avaliacao a where a.musica.id = :musicaId")
    Double mediaDaMusica(Long musicaId);

    boolean existsByUsuarioIdAndAlbumId(Long usuarioId, Long albumId);

    boolean existsByUsuarioIdAndAlbumIdAndIdNot(Long usuarioId, Long albumId, Long id);

    boolean existsByUsuarioIdAndMusicaId(Long usuarioId, Long musicaId);

    boolean existsByUsuarioIdAndMusicaIdAndIdNot(Long usuarioId, Long musicaId, Long id);

    boolean existsByUsuarioId(Long usuarioId);

    boolean existsByAlbumId(Long albumId);

    boolean existsByMusicaId(Long musicaId);
}