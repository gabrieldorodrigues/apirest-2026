package com.example.musica.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.musica.model.Album;
import com.example.musica.model.Artista;
import com.example.musica.model.Avaliacao;
import com.example.musica.model.Genero;
import com.example.musica.model.Musica;
import com.example.musica.model.Usuario;
import com.example.musica.repository.AlbumRepository;
import com.example.musica.repository.ArtistaRepository;
import com.example.musica.repository.AvaliacaoRepository;
import com.example.musica.repository.GeneroRepository;
import com.example.musica.repository.MusicaRepository;
import com.example.musica.repository.UsuarioRepository;

@Configuration
public class SeedConfig {

    private static final Logger log = LoggerFactory.getLogger(SeedConfig.class);

    // número qualquer, só precisa ser o mesmo em todas as instâncias
    private static final long LOCK_SEED = 2026_11_19L;

    @Bean
    CommandLineRunner seed(ArtistaRepository artistaRepository, GeneroRepository generoRepository,
            AlbumRepository albumRepository, MusicaRepository musicaRepository,
            UsuarioRepository usuarioRepository, AvaliacaoRepository avaliacaoRepository,
            PlatformTransactionManager transactionManager, JdbcTemplate jdbcTemplate) {
        return args -> new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
            // com várias instâncias subindo juntas, só uma popula o banco; as outras esperam o lock e
            // depois encontram os dados já gravados
            travarSeedNoPostgres(jdbcTemplate);

            // o banco é persistente, então só popula na primeira vez
            if (artistaRepository.count() > 0) {
                return;
            }
            log.info("Banco vazio: gravando os dados de exemplo");

            Genero rock = generoRepository.save(new Genero("Rock"));
            Genero alternativo = generoRepository.save(new Genero("Alternativo"));
            Genero mpb = generoRepository.save(new Genero("MPB"));
            Genero jazz = generoRepository.save(new Genero("Jazz"));
            Genero tropicalia = generoRepository.save(new Genero("Tropicália"));

            Artista radiohead = artistaRepository.save(new Artista("Radiohead", "Reino Unido", 1985));
            Artista caetano = artistaRepository.save(new Artista("Caetano Veloso", "Brasil", 1965));
            Artista miles = artistaRepository.save(new Artista("Miles Davis", "Estados Unidos", 1944));
            artistaRepository.save(new Artista("Os Mutantes", "Brasil", 1966));

            Album okComputer = albumRepository.save(album("OK Computer", 1997, radiohead, Set.of(rock, alternativo)));
            Album inRainbows = albumRepository.save(album("In Rainbows", 2007, radiohead, Set.of(rock, alternativo)));
            Album caetano1968 = albumRepository.save(album("Caetano Veloso", 1968, caetano, Set.of(mpb, tropicalia)));
            Album kindOfBlue = albumRepository.save(album("Kind of Blue", 1959, miles, Set.of(jazz)));

            Musica paranoid = musicaRepository.save(musica("Paranoid Android", 2, 383, okComputer));
            musicaRepository.save(musica("Airbag", 1, 284, okComputer));
            musicaRepository.save(musica("Karma Police", 6, 264, okComputer));
            Musica reckoner = musicaRepository.save(musica("Reckoner", 7, 290, inRainbows));
            musicaRepository.save(musica("15 Step", 1, 237, inRainbows));
            Musica tropicaliaMusica = musicaRepository.save(musica("Tropicália", 1, 220, caetano1968));
            musicaRepository.save(musica("Alegria, Alegria", 7, 172, caetano1968));
            Musica soWhat = musicaRepository.save(musica("So What", 1, 562, kindOfBlue));
            musicaRepository.save(musica("Blue in Green", 3, 337, kindOfBlue));

            Usuario gabriel = usuarioRepository.save(new Usuario("Gabriel", "gabriel@email.com"));
            Usuario ana = usuarioRepository.save(new Usuario("Ana", "ana@email.com"));
            Usuario joao = usuarioRepository.save(new Usuario("João", "joao@email.com"));

            // datas espalhadas entre setembro e novembro de 2026, em ordem, para o relatório por período
            avaliacaoRepository.save(avaliacao(gabriel, okComputer, null, "5.0", "Melhor álbum dos anos 90.", "2026-09-05T20:15"));
            avaliacaoRepository.save(avaliacao(ana, okComputer, null, "4.5", "Clássico, mas um pouco frio.", "2026-09-18T21:40"));
            avaliacaoRepository.save(avaliacao(joao, inRainbows, null, "4.0", null, "2026-10-02T19:05"));
            avaliacaoRepository.save(avaliacao(gabriel, kindOfBlue, null, "4.5", "Perfeito pra estudar.", "2026-10-11T14:30"));
            avaliacaoRepository.save(avaliacao(ana, caetano1968, null, "5.0", "Tropicália raiz.", "2026-10-24T22:10"));
            avaliacaoRepository.save(avaliacao(gabriel, null, paranoid, "5.0", "Três músicas em uma.", "2026-11-03T18:45"));
            avaliacaoRepository.save(avaliacao(joao, null, reckoner, "4.5", null, "2026-11-09T23:20"));
            avaliacaoRepository.save(avaliacao(ana, null, tropicaliaMusica, "4.0", null, "2026-11-15T10:00"));
            avaliacaoRepository.save(avaliacao(joao, null, soWhat, "3.5", "Bom, mas longo.", "2026-11-20T16:35"));
        });
    }

    // lock de transação do PostgreSQL: é liberado sozinho no commit; o H2 roda numa instância só e não precisa
    private void travarSeedNoPostgres(JdbcTemplate jdbcTemplate) {
        String banco = jdbcTemplate.execute(
                (ConnectionCallback<String>) conexao -> conexao.getMetaData().getDatabaseProductName());
        if ("PostgreSQL".equals(banco)) {
            jdbcTemplate.queryForObject("select pg_advisory_xact_lock(?)::text", String.class, LOCK_SEED);
        }
    }

    private Album album(String titulo, int ano, Artista artista, Set<Genero> generos) {
        Album album = new Album();
        album.setTitulo(titulo);
        album.setAnoLancamento(ano);
        album.setArtista(artista);
        album.setGeneros(new HashSet<>(generos));
        return album;
    }

    private Musica musica(String titulo, int faixa, int duracao, Album album) {
        Musica musica = new Musica();
        musica.setTitulo(titulo);
        musica.setNumeroFaixa(faixa);
        musica.setDuracaoSegundos(duracao);
        musica.setAlbum(album);
        return musica;
    }

    private Avaliacao avaliacao(Usuario usuario, Album album, Musica musica, String nota, String resenha,
            String criadaEm) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setAlbum(album);
        avaliacao.setMusica(musica);
        avaliacao.setNota(new BigDecimal(nota));
        avaliacao.setResenha(resenha);
        avaliacao.setCriadaEm(LocalDateTime.parse(criadaEm));
        return avaliacao;
    }
}