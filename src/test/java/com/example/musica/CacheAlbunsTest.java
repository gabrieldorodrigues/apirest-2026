package com.example.musica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.example.musica.config.CacheConfig;

import jakarta.persistence.EntityManagerFactory;

/**
 * Cache dos álbuns com o cache em memória (o mesmo comportamento do Redis, sem precisar de Docker).
 * Sem @Transactional: o cache não volta atrás num rollback, então cada requisição grava de verdade, num H2
 * só desta classe, e cada teste desfaz o que mudou. As estatísticas do Hibernate contam os comandos SQL.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cache-teste",
        "logging.file.name=",
        "spring.cache.type=simple",
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "logging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=WARN"
})
@AutoConfigureMockMvc
@DisplayName("Cache dos álbuns")
class CacheAlbunsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void limparCache() {
        cacheManager.getCacheNames().forEach(nome -> cacheManager.getCache(nome).clear());
    }

    @Test
    @DisplayName("GET /albuns/{id}: a primeira consulta vai ao banco, a segunda não faz nenhum SQL")
    void buscaVemDoCache() throws Exception {
        long primeira = consultas(get("/albuns/1"));
        long segunda = consultas(get("/albuns/1"));

        assertThat(primeira).isPositive();
        assertThat(segunda).isZero();
        assertThat(cacheManager.getCache(CacheConfig.ALBUM).get(1L)).isNotNull();
    }

    @Test
    @DisplayName("GET /albuns: cada combinação de filtro e página é uma entrada; repetir não faz SQL")
    void listagemVemDoCache() throws Exception {
        long primeira = consultas(get("/albuns").param("size", "2").param("sort", "anoLancamento,desc"));
        long repetida = consultas(get("/albuns").param("size", "2").param("sort", "anoLancamento,desc"));
        long outraPagina = consultas(get("/albuns").param("size", "2").param("page", "1").param("sort", "anoLancamento,desc"));

        assertThat(primeira).isPositive();
        assertThat(repetida).isZero();
        assertThat(outraPagina).isPositive();
    }

    @Test
    @DisplayName("Criar, editar e remover avaliação atualiza a notaMedia do álbum e da listagem na hora")
    void avaliacaoInvalidaOAlbum() throws Exception {
        notaMediaDoAlbum1(4.75);
        listagemComNotaMedia(4.75);

        String criada = mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 3, "albumId": 1, "nota": 2.0}
                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String id = criada.replaceAll(".*\"id\":(\\d+).*", "$1");
        // (5,0 + 4,5 + 2,0) / 3
        notaMediaDoAlbum1(3.83);
        listagemComNotaMedia(3.83);

        mockMvc.perform(put("/avaliacoes/" + id).contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 3, "albumId": 1, "nota": 3.5}
                """))
                .andExpect(status().isOk());
        notaMediaDoAlbum1(4.33);

        mockMvc.perform(delete("/avaliacoes/" + id)).andExpect(status().isNoContent());
        notaMediaDoAlbum1(4.75);
        listagemComNotaMedia(4.75);
    }

    @Test
    @DisplayName("Avaliação que troca de álbum para música também invalida o álbum antigo")
    void avaliacaoTrocaDeAlbum() throws Exception {
        String criada = mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 3, "albumId": 1, "nota": 2.0}
                """))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String id = criada.replaceAll(".*\"id\":(\\d+).*", "$1");
        notaMediaDoAlbum1(3.83);

        mockMvc.perform(put("/avaliacoes/" + id).contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 3, "musicaId": 2, "nota": 2.0}
                """))
                .andExpect(status().isOk());
        notaMediaDoAlbum1(4.75);

        mockMvc.perform(delete("/avaliacoes/" + id)).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Editar o artista invalida os álbuns, porque o nome dele vai dentro da resposta")
    void artistaInvalidaOsAlbuns() throws Exception {
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.artista.nome").value("Radiohead"));

        mockMvc.perform(put("/artistas/1").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome": "Radiohead (editado)", "pais": "Reino Unido", "anoInicio": 1985}
                """))
                .andExpect(status().isOk());
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.artista.nome").value("Radiohead (editado)"));

        mockMvc.perform(put("/artistas/1").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome": "Radiohead", "pais": "Reino Unido", "anoInicio": 1985}
                """))
                .andExpect(status().isOk());
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.artista.nome").value("Radiohead"));
    }

    @Test
    @DisplayName("Álbum inexistente (404) não fica no cache")
    void naoEncontradoNaoVaiParaOCache() throws Exception {
        mockMvc.perform(get("/albuns/999")).andExpect(status().isNotFound());

        assertThat(cacheManager.getCache(CacheConfig.ALBUM).get(999L)).isNull();
    }

    @Test
    @DisplayName("Só os álbuns têm cache: usuários (e-mail) e avaliações sempre vêm do banco")
    void soAlbunsTemCache() throws Exception {
        assertThat(cacheManager.getCacheNames()).containsExactlyInAnyOrder(CacheConfig.ALBUM, CacheConfig.ALBUNS);

        long primeira = consultas(get("/usuarios/1"));
        long segunda = consultas(get("/usuarios/1"));
        assertThat(primeira).isPositive();
        assertThat(segunda).isPositive();
    }

    private long consultas(MockHttpServletRequestBuilder requisicao) throws Exception {
        Statistics estatisticas = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        estatisticas.clear();
        mockMvc.perform(requisicao).andExpect(status().isOk());
        return estatisticas.getPrepareStatementCount();
    }

    private void notaMediaDoAlbum1(double esperada) throws Exception {
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.notaMedia").value(esperada));
    }

    private void listagemComNotaMedia(double esperada) throws Exception {
        mockMvc.perform(get("/albuns").param("titulo", "OK Computer"))
                .andExpect(jsonPath("$.content[0].notaMedia").value(esperada));
    }
}
