package com.example.musica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManagerFactory;

/**
 * Cache dos álbuns num Redis de verdade, criado num container pelo Testcontainers: confere as chaves, o TTL,
 * a serialização das respostas, a invalidação depois do commit e as métricas de hit.
 * Sem Docker rodando, a classe é pulada em vez de falhar.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cache-redis-teste",
        "logging.file.name=",
        "spring.cache.type=redis",
        "spring.jpa.properties.hibernate.generate_statistics=true",
        "logging.level.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=WARN"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
@DisplayName("Cache dos álbuns no Redis (Testcontainers)")
class CacheRedisTest {

    @Container
    @ServiceConnection(name = "redis")
    static GenericContainer<?> redis = new GenericContainer<>("redis:8-alpine").withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void limparRedis() {
        redisTemplate.execute(conexao -> {
            conexao.serverCommands().flushAll();
            return null;
        }, true);
    }

    @Test
    @DisplayName("GET /albuns/1 grava a chave album::1 com TTL de até 10 minutos; a segunda consulta não faz SQL")
    void chaveETtl() throws Exception {
        long primeira = consultas(get("/albuns/1"));
        long segunda = consultas(get("/albuns/1"));

        assertThat(primeira).isPositive();
        assertThat(segunda).isZero();
        assertThat(redisTemplate.hasKey("album::1")).isTrue();
        assertThat(redisTemplate.getExpire("album::1")).isBetween(1L, 600L);
    }

    @Test
    @DisplayName("A listagem fica no Redis com a chave dos filtros e da página e volta inteira do cache")
    void listagemNoRedis() throws Exception {
        consultas(get("/albuns").param("artistaId", "1").param("size", "2"));
        long segunda = consultas(get("/albuns").param("artistaId", "1").param("size", "2"));

        assertThat(segunda).isZero();
        assertThat(redisTemplate.keys("albuns::*"))
                .containsExactly("albuns::titulo=null;ano=null;artistaId=1;generoId=null;page=0;size=2;sort=titulo: ASC");
        mockMvc.perform(get("/albuns").param("artistaId", "1").param("size", "2"))
                .andExpect(jsonPath("$.content[0].titulo").value("In Rainbows"))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    @DisplayName("Avaliação nova apaga album::1 e as listagens no Redis, e a próxima consulta traz a média nova")
    void avaliacaoInvalidaNoRedis() throws Exception {
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.notaMedia").value(4.75));
        mockMvc.perform(get("/albuns"));
        assertThat(redisTemplate.hasKey("album::1")).isTrue();

        String criada = mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 3, "albumId": 1, "nota": 2.0}
                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(redisTemplate.hasKey("album::1")).isFalse();
        assertThat(redisTemplate.keys("albuns::*")).isEmpty();
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.notaMedia").value(3.83));

        mockMvc.perform(delete("/avaliacoes/" + criada.replaceAll(".*\"id\":(\\d+).*", "$1")))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.notaMedia").value(4.75));
    }

    @Test
    @DisplayName("Acertos do cache aparecem em /actuator/metrics/cache.gets")
    void metricasDeHit() throws Exception {
        mockMvc.perform(get("/albuns/2"));
        mockMvc.perform(get("/albuns/2"));

        mockMvc.perform(get("/actuator/metrics/cache.gets").param("tag", "cache:album").param("tag", "result:hit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[0].value").value(greaterThanOrEqualTo(1.0)));
    }

    private long consultas(MockHttpServletRequestBuilder requisicao) throws Exception {
        Statistics estatisticas = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        estatisticas.clear();
        mockMvc.perform(requisicao).andExpect(status().isOk());
        return estatisticas.getPrepareStatementCount();
    }
}
