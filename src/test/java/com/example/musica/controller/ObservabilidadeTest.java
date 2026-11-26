package com.example.musica.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.example.musica.ApiTestBase;

import io.micrometer.core.instrument.MeterRegistry;

@DisplayName("Observabilidade (Actuator e métricas)")
class ObservabilidadeTest extends ApiTestBase {

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    @DisplayName("/actuator/health retorna UP com os detalhes do banco e do disco")
    void healthCheck() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.db.status").value("UP"))
                .andExpect(jsonPath("$.components.db.details.database").value("H2"))
                .andExpect(jsonPath("$.components.diskSpace.status").value("UP"));
    }

    @Test
    @DisplayName("/actuator/info mostra o nome da API e a versão do Java")
    void info() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.nome").value("API de Avaliação Musical"))
                .andExpect(jsonPath("$.java.version").isNotEmpty());
    }

    @Test
    @DisplayName("Toda resposta traz o header X-Instancia, o mesmo id mostrado no /actuator/info")
    void identificaInstancia() throws Exception {
        String instancia = mockMvc.perform(get("/albuns"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Instancia"))
                .andReturn().getResponse().getHeader("X-Instancia");

        mockMvc.perform(get("/actuator/info"))
                .andExpect(jsonPath("$.instancia.id").value(instancia))
                .andExpect(jsonPath("$.instancia.iniciadaEm").isNotEmpty());
    }

    @Test
    @DisplayName("/actuator/metrics lista as métricas HTTP, da JVM e do domínio")
    void listaMetricas() throws Exception {
        mockMvc.perform(get("/albuns")).andExpect(status().isOk());

        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.names", hasItem("http.server.requests")))
                .andExpect(jsonPath("$.names", hasItem("jvm.memory.used")))
                .andExpect(jsonPath("$.names", hasItem("musica.avaliacoes.total")));
    }

    @Test
    @DisplayName("Criar uma avaliação de álbum incrementa musica.avaliacoes.criadas com tipo=album")
    void contadorDeAvaliacoesCriadas() throws Exception {
        double antes = contador("album");

        mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 3, "albumId": 1, "nota": 4.0}
                """))
                .andExpect(status().isCreated());

        assertThat(contador("album")).isEqualTo(antes + 1);
        mockMvc.perform(get("/actuator/metrics/musica.avaliacoes.criadas").param("tag", "tipo:album"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[0].statistic").value("COUNT"));
    }

    @Test
    @DisplayName("Avaliação recusada (409) não incrementa o contador")
    void avaliacaoRecusadaNaoConta() throws Exception {
        double antes = contador("album");

        mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 1, "albumId": 1, "nota": 4.0}
                """))
                .andExpect(status().isConflict());

        assertThat(contador("album")).isEqualTo(antes);
    }

    @Test
    @DisplayName("musica.avaliacoes.total mostra a quantidade de avaliações no banco")
    void totalDeAvaliacoes() throws Exception {
        // seed tem 9 avaliações
        mockMvc.perform(get("/actuator/metrics/musica.avaliacoes.total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.measurements[0].value").value(9.0));
    }

    private double contador(String tipo) {
        var counter = meterRegistry.find("musica.avaliacoes.criadas").tag("tipo", tipo).counter();
        return counter == null ? 0 : counter.count();
    }
}
