package com.example.musica.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.musica.dto.AlbumRelatorioDto;
import com.example.musica.dto.GeneroRelatorioDto;
import com.example.musica.dto.MusicaRelatorioDto;
import com.example.musica.dto.NotaQuantidadeDto;
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

    // relatório por período: cada consulta agrega no banco, então o número de consultas não cresce com os dados.
    // O período vai de inicio (inclusivo) até fim (exclusivo).

    @Query("""
            select new com.example.musica.repository.ResumoPeriodo(
                count(a),
                coalesce(sum(case when a.album is not null then 1 else 0 end), 0),
                coalesce(sum(case when a.musica is not null then 1 else 0 end), 0),
                avg(a.nota),
                count(distinct a.usuario.id))
            from Avaliacao a
            where a.criadaEm >= :inicio and a.criadaEm < :fim
            """)
    ResumoPeriodo resumoDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            select new com.example.musica.dto.NotaQuantidadeDto(a.nota, count(a))
            from Avaliacao a
            where a.criadaEm >= :inicio and a.criadaEm < :fim
            group by a.nota
            """)
    List<NotaQuantidadeDto> notasDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    // a avaliação de uma música conta para os gêneros do álbum da música
    @Query("""
            select new com.example.musica.dto.GeneroRelatorioDto(g.id, g.nome, count(a), avg(a.nota))
            from Avaliacao a
            left join a.musica m
            join Album al on al.id = coalesce(a.album.id, m.album.id)
            join al.generos g
            where a.criadaEm >= :inicio and a.criadaEm < :fim
            group by g.id, g.nome
            order by count(a) desc, g.nome
            """)
    List<GeneroRelatorioDto> generosDoPeriodo(LocalDateTime inicio, LocalDateTime fim);

    @Query("""
            select new com.example.musica.dto.AlbumRelatorioDto(al.id, al.titulo, ar.nome, count(a), avg(a.nota))
            from Avaliacao a
            join a.album al
            join al.artista ar
            where a.criadaEm >= :inicio and a.criadaEm < :fim
            group by al.id, al.titulo, ar.nome
            order by count(a) desc, avg(a.nota) desc, al.titulo
            """)
    List<AlbumRelatorioDto> albunsMaisAvaliados(LocalDateTime inicio, LocalDateTime fim, Pageable limite);

    @Query("""
            select new com.example.musica.dto.MusicaRelatorioDto(m.id, m.titulo, al.titulo, ar.nome, count(a), avg(a.nota))
            from Avaliacao a
            join a.musica m
            join m.album al
            join al.artista ar
            where a.criadaEm >= :inicio and a.criadaEm < :fim
            group by m.id, m.titulo, al.titulo, ar.nome
            order by count(a) desc, avg(a.nota) desc, m.titulo
            """)
    List<MusicaRelatorioDto> musicasMaisAvaliadas(LocalDateTime inicio, LocalDateTime fim, Pageable limite);
}