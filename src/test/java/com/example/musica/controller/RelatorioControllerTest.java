package com.example.musica.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import com.example.musica.ApiTestBase;
import com.example.musica.model.Avaliacao;
import com.example.musica.model.Usuario;
import com.example.musica.repository.AlbumRepository;
import com.example.musica.repository.AvaliacaoRepository;
import com.example.musica.repository.MusicaRepository;
import com.example.musica.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Relatório de avaliações por período, com as datas do seed:
 * setembro tem 2 avaliações de álbum, outubro 3 de álbum e novembro 4 de música (9 no total).
 */
@DisplayName("Relatório de avaliações por período")
// as estatísticas do Hibernate contam os comandos SQL de cada relatório (teste do N+1)
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "logging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=WARN"
})
class RelatorioControllerTest extends ApiTestBase {

    private static final String RELATORIO = "/relatorios/avaliacoes";

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private MusicaRepository musicaRepository;

    @Test
    @DisplayName("Período com todo o seed: totais, média, usuários, distribuição, gêneros e mais avaliados")
    void relatorioCompleto() throws Exception {
        mockMvc.perform(get(RELATORIO).param("inicio", "2026-09-01").param("fim", "2026-11-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inicio").value("2026-09-01"))
                .andExpect(jsonPath("$.fim").value("2026-11-30"))
                .andExpect(jsonPath("$.totalAvaliacoes").value(9))
                .andExpect(jsonPath("$.avaliacoesDeAlbuns").value(5))
                .andExpect(jsonPath("$.avaliacoesDeMusicas").value(4))
                .andExpect(jsonPath("$.notaMedia").value(4.44))
                .andExpect(jsonPath("$.usuariosQueAvaliaram").value(3))
                // 10 notas possíveis, de 0,5 a 5, mesmo as que ninguém deu
                .andExpect(jsonPath("$.distribuicaoNotas", hasSize(10)))
                .andExpect(jsonPath("$.distribuicaoNotas[0].nota").value(0.5))
                .andExpect(jsonPath("$.distribuicaoNotas[0].quantidade").value(0))
                .andExpect(jsonPath("$.distribuicaoNotas[6].nota").value(3.5))
                .andExpect(jsonPath("$.distribuicaoNotas[6].quantidade").value(1))
                .andExpect(jsonPath("$.distribuicaoNotas[7].quantidade").value(2))
                .andExpect(jsonPath("$.distribuicaoNotas[8].quantidade").value(3))
                .andExpect(jsonPath("$.distribuicaoNotas[9].nota").value(5.0))
                .andExpect(jsonPath("$.distribuicaoNotas[9].quantidade").value(3))
                // avaliações de música contam para os gêneros do álbum; empate na quantidade vai por nome
                .andExpect(jsonPath("$.porGenero", hasSize(5)))
                .andExpect(jsonPath("$.porGenero[0].nome").value("Alternativo"))
                .andExpect(jsonPath("$.porGenero[0].avaliacoes").value(5))
                .andExpect(jsonPath("$.porGenero[0].notaMedia").value(4.6))
                .andExpect(jsonPath("$.porGenero[1].nome").value("Rock"))
                .andExpect(jsonPath("$.porGenero[2].nome").value("Jazz"))
                .andExpect(jsonPath("$.porGenero[2].avaliacoes").value(2))
                .andExpect(jsonPath("$.porGenero[2].notaMedia").value(4.0))
                // mais avaliações primeiro; empate vai pela maior nota média
                .andExpect(jsonPath("$.albunsMaisAvaliados", hasSize(4)))
                .andExpect(jsonPath("$.albunsMaisAvaliados[0].titulo").value("OK Computer"))
                .andExpect(jsonPath("$.albunsMaisAvaliados[0].artista").value("Radiohead"))
                .andExpect(jsonPath("$.albunsMaisAvaliados[0].avaliacoes").value(2))
                .andExpect(jsonPath("$.albunsMaisAvaliados[0].notaMedia").value(4.75))
                .andExpect(jsonPath("$.albunsMaisAvaliados[1].titulo").value("Caetano Veloso"))
                .andExpect(jsonPath("$.albunsMaisAvaliados[3].titulo").value("In Rainbows"))
                .andExpect(jsonPath("$.musicasMaisAvaliadas", hasSize(4)))
                .andExpect(jsonPath("$.musicasMaisAvaliadas[0].titulo").value("Paranoid Android"))
                .andExpect(jsonPath("$.musicasMaisAvaliadas[0].album").value("OK Computer"))
                .andExpect(jsonPath("$.musicasMaisAvaliadas[3].titulo").value("So What"))
                .andExpect(jsonPath("$.musicasMaisAvaliadas[3].notaMedia").value(3.5));
    }

    @Test
    @DisplayName("Períodos diferentes dão relatórios diferentes (outubro só álbuns, novembro só músicas)")
    void periodosDiferentes() throws Exception {
        mockMvc.perform(get(RELATORIO).param("inicio", "2026-10-01").param("fim", "2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvaliacoes").value(3))
                .andExpect(jsonPath("$.avaliacoesDeAlbuns").value(3))
                .andExpect(jsonPath("$.avaliacoesDeMusicas").value(0))
                .andExpect(jsonPath("$.notaMedia").value(4.5))
                .andExpect(jsonPath("$.musicasMaisAvaliadas", hasSize(0)));

        mockMvc.perform(get(RELATORIO).param("inicio", "2026-11-01").param("fim", "2026-11-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvaliacoes").value(4))
                .andExpect(jsonPath("$.avaliacoesDeAlbuns").value(0))
                .andExpect(jsonPath("$.avaliacoesDeMusicas").value(4))
                .andExpect(jsonPath("$.notaMedia").value(4.25))
                .andExpect(jsonPath("$.albunsMaisAvaliados", hasSize(0)));
    }

    @Test
    @DisplayName("As duas datas entram no período: inicio = fim pega as avaliações do dia todo")
    void datasInclusivas() throws Exception {
        // So What foi avaliada em 20/11 às 16:35
        mockMvc.perform(get(RELATORIO).param("inicio", "2026-11-20").param("fim", "2026-11-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvaliacoes").value(1))
                .andExpect(jsonPath("$.musicasMaisAvaliadas[0].titulo").value("So What"));
    }

    @Test
    @DisplayName("Período sem avaliações: 200 com contagens zeradas, listas vazias e notaMedia nula")
    void periodoVazio() throws Exception {
        mockMvc.perform(get(RELATORIO).param("inicio", "2020-01-01").param("fim", "2020-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvaliacoes").value(0))
                .andExpect(jsonPath("$.avaliacoesDeAlbuns").value(0))
                .andExpect(jsonPath("$.avaliacoesDeMusicas").value(0))
                .andExpect(jsonPath("$.notaMedia").value(nullValue()))
                .andExpect(jsonPath("$.usuariosQueAvaliaram").value(0))
                .andExpect(jsonPath("$.distribuicaoNotas", hasSize(10)))
                .andExpect(jsonPath("$.distribuicaoNotas[*].quantidade").value(everyItem(is(0))))
                .andExpect(jsonPath("$.porGenero", hasSize(0)))
                .andExpect(jsonPath("$.albunsMaisAvaliados", hasSize(0)))
                .andExpect(jsonPath("$.musicasMaisAvaliadas", hasSize(0)));
    }

    @Test
    @DisplayName("Uma avaliação criada pela API entra no relatório do dia")
    void novaAvaliacaoEntraNoRelatorio() throws Exception {
        String hoje = LocalDate.now().toString();
        long antes = totalDoDia(hoje);

        mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 3, "albumId": 1, "nota": 4.0}
                """))
                .andExpect(status().isCreated());

        assertEquals(antes + 1, totalDoDia(hoje));
    }

    @Test
    @DisplayName("Data inicial depois da final: 422")
    void inicioDepoisDoFim() throws Exception {
        mockMvc.perform(get(RELATORIO).param("inicio", "2026-12-31").param("fim", "2026-01-01"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"))
                .andExpect(jsonPath("$.detail").value(containsString("depois da data final")));
    }

    @Test
    @DisplayName("Período maior que 1 ano: 422; exatamente 1 ano: 200")
    void periodoMaximo() throws Exception {
        mockMvc.perform(get(RELATORIO).param("inicio", "2025-01-01").param("fim", "2026-06-01"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("O período do relatório pode ter no máximo 1 ano"));

        mockMvc.perform(get(RELATORIO).param("inicio", "2025-11-30").param("fim", "2026-11-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvaliacoes").value(9));
    }

    @Test
    @DisplayName("Data ausente ou fora do formato AAAA-MM-DD: 400 em Problem Details")
    void datasInvalidas() throws Exception {
        mockMvc.perform(get(RELATORIO).param("inicio", "2026-09-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro obrigatório"))
                .andExpect(jsonPath("$.detail").value("O parâmetro fim é obrigatório"));

        mockMvc.perform(get(RELATORIO).param("inicio", "2026-13-01").param("fim", "2026-12-31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro inválido"))
                .andExpect(jsonPath("$.detail").value(containsString("inicio")));

        mockMvc.perform(get(RELATORIO).param("inicio", "01/09/2026").param("fim", "2026-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Sem N+1: o relatório faz 5 consultas, com 9 ou com 29 avaliações no período")
    void numeroFixoDeConsultas() throws Exception {
        assertEquals(5, consultasDoRelatorio());

        // mais 20 avaliações no período: 10 usuários novos avaliando um álbum e uma música cada
        for (int i = 1; i <= 10; i++) {
            Usuario usuario = usuarioRepository.save(new Usuario("Ouvinte " + i, "ouvinte" + i + "@email.com"));
            avaliacaoRepository.save(avaliacao(usuario, true));
            avaliacaoRepository.save(avaliacao(usuario, false));
        }
        entityManager.flush();

        assertEquals(5, consultasDoRelatorio());
        mockMvc.perform(get(RELATORIO).param("inicio", "2026-09-01").param("fim", "2026-11-30"))
                .andExpect(jsonPath("$.totalAvaliacoes").value(29))
                .andExpect(jsonPath("$.usuariosQueAvaliaram").value(13));
    }

    private long consultasDoRelatorio() throws Exception {
        Statistics estatisticas = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        estatisticas.clear();
        mockMvc.perform(get(RELATORIO).param("inicio", "2026-09-01").param("fim", "2026-11-30"))
                .andExpect(status().isOk());
        return estatisticas.getPrepareStatementCount();
    }

    private long totalDoDia(String dia) throws Exception {
        String corpo = mockMvc.perform(get(RELATORIO).param("inicio", dia).param("fim", dia))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(corpo.replaceAll(".*\"totalAvaliacoes\":(\\d+).*", "$1"));
    }

    private Avaliacao avaliacao(Usuario usuario, boolean deAlbum) {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setUsuario(usuario);
        if (deAlbum) {
            avaliacao.setAlbum(albumRepository.getReferenceById(2L));
        } else {
            avaliacao.setMusica(musicaRepository.getReferenceById(4L));
        }
        avaliacao.setNota(new BigDecimal("4.0"));
        avaliacao.setCriadaEm(LocalDateTime.of(2026, 10, 15, 12, 0));
        return avaliacao;
    }
}
