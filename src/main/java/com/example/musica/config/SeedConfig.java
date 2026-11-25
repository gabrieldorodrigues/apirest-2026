package com.example.musica.config;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    CommandLineRunner seed(ArtistaRepository artistaRepository, GeneroRepository generoRepository,
            AlbumRepository albumRepository, MusicaRepository musicaRepository,
            UsuarioRepository usuarioRepository, AvaliacaoRepository avaliacaoRepository) {
        return args -> {
            // o banco é em arquivo, então só popula na primeira vez
            if (artistaRepository.count() > 0) {
                return;
            }

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

            avaliacaoRepository.save(avaliacao(gabriel, okComputer, null, "5.0", "Melhor álbum dos anos 90."));
            avaliacaoRepository.save(avaliacao(ana, okComputer, null, "4.5", "Clássico, mas um pouco frio."));
            avaliacaoRepository.save(avaliacao(joao, inRainbows, null, "4.0", null));
            avaliacaoRepository.save(avaliacao(gabriel, kindOfBlue, null, "4.5", "Perfeito pra estudar."));
            avaliacaoRepository.save(avaliacao(ana, caetano1968, null, "5.0", "Tropicália raiz."));
            avaliacaoRepository.save(avaliacao(gabriel, null, paranoid, "5.0", "Três músicas em uma."));
            avaliacaoRepository.save(avaliacao(joao, null, reckoner, "4.5", null));
            avaliacaoRepository.save(avaliacao(ana, null, tropicaliaMusica, "4.0", null));
            avaliacaoRepository.save(avaliacao(joao, null, soWhat, "3.5", "Bom, mas longo."));
        };
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

    private Avaliacao avaliacao(Usuario usuario, Album album, Musica musica, String nota, String resenha) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        avaliacao.setAlbum(album);
        avaliacao.setMusica(musica);
        avaliacao.setNota(new BigDecimal(nota));
        avaliacao.setResenha(resenha);
        return avaliacao;
    }
}