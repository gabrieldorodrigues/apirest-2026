package com.example.musica;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Roda a API com o profile "postgres" contra um PostgreSQL de verdade, criado num container pelo Testcontainers.
 * Cobre o que costuma mudar entre H2 e PostgreSQL: criação do schema, filtros com parâmetros nulos,
 * LIKE sem diferenciar maiúsculas, média de numeric e constraints únicas.
 * Sem Docker rodando, a classe é pulada em vez de falhar.
 */
@SpringBootTest(properties = "logging.file.name=")
@AutoConfigureMockMvc
@ActiveProfiles("postgres")
@Testcontainers(disabledWithoutDocker = true)
@Transactional
@DisplayName("PostgreSQL (Testcontainers)")
class PostgresIntegracaoTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommandLineRunner seed;

    @Test
    @DisplayName("Seed com lock no PostgreSQL: rodar de novo (como outra instância subindo) não duplica os dados")
    void seedNaoDuplica() throws Exception {
        seed.run();

        mockMvc.perform(get("/artistas")).andExpect(jsonPath("$.page.totalElements").value(4));
        mockMvc.perform(get("/avaliacoes")).andExpect(jsonPath("$.page.totalElements").value(9));
    }

    @Test
    @DisplayName("Health check mostra o banco como PostgreSQL")
    void healthMostraPostgres() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.db.status").value("UP"))
                .andExpect(jsonPath("$.components.db.details.database").value("PostgreSQL"));
    }

    @Test
    @DisplayName("Seed carregado e listagens sem filtro (parâmetros nulos) funcionam em todos os recursos")
    void listagensSemFiltro() throws Exception {
        mockMvc.perform(get("/artistas")).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(4));
        mockMvc.perform(get("/generos")).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(5));
        mockMvc.perform(get("/albuns")).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(4));
        mockMvc.perform(get("/musicas")).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(9));
        mockMvc.perform(get("/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(3));
        mockMvc.perform(get("/avaliacoes")).andExpect(status().isOk()).andExpect(jsonPath("$.page.totalElements").value(9));
    }

    @Test
    @DisplayName("Filtros combinados, LIKE sem diferenciar maiúsculas e ordenação")
    void filtros() throws Exception {
        mockMvc.perform(get("/albuns").param("artistaId", "1").param("generoId", "2").param("ano", "2007"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("In Rainbows"));

        mockMvc.perform(get("/artistas").param("pais", "BRASIL"))
                .andExpect(jsonPath("$.page.totalElements").value(2));

        mockMvc.perform(get("/avaliacoes").param("notaMinima", "4.5").param("sort", "nota,desc"))
                .andExpect(jsonPath("$.page.totalElements").value(6))
                .andExpect(jsonPath("$.content[0].nota").value(5.0));
    }

    @Test
    @DisplayName("Nota média calculada com avg sobre numeric")
    void notaMedia() throws Exception {
        mockMvc.perform(get("/albuns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaMedia").value(4.75));
    }

    @Test
    @DisplayName("Relatório por período: count, sum com case, avg e group by no PostgreSQL")
    void relatorioPorPeriodo() throws Exception {
        mockMvc.perform(get("/relatorios/avaliacoes").param("inicio", "2026-09-01").param("fim", "2026-11-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvaliacoes").value(9))
                .andExpect(jsonPath("$.avaliacoesDeAlbuns").value(5))
                .andExpect(jsonPath("$.avaliacoesDeMusicas").value(4))
                .andExpect(jsonPath("$.notaMedia").value(4.44))
                .andExpect(jsonPath("$.usuariosQueAvaliaram").value(3))
                .andExpect(jsonPath("$.distribuicaoNotas[9].quantidade").value(3))
                .andExpect(jsonPath("$.porGenero[0].nome").value("Alternativo"))
                .andExpect(jsonPath("$.porGenero[0].avaliacoes").value(5))
                .andExpect(jsonPath("$.albunsMaisAvaliados[0].titulo").value("OK Computer"))
                .andExpect(jsonPath("$.musicasMaisAvaliadas[0].titulo").value("Paranoid Android"));

        mockMvc.perform(get("/relatorios/avaliacoes").param("inicio", "2020-01-01").param("fim", "2020-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAvaliacoes").value(0))
                .andExpect(jsonPath("$.avaliacoesDeAlbuns").value(0));
    }

    @Test
    @DisplayName("Criar avaliação (201) e repetir (409) gravando no PostgreSQL")
    void criaAvaliacaoERecusaRepetida() throws Exception {
        String corpo = """
                {"usuarioId": 3, "albumId": 1, "nota": 2.5, "resenha": "Gravada no PostgreSQL"}
                """;
        mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(2.5));
        mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isConflict());
        mockMvc.perform(get("/albuns/1")).andExpect(jsonPath("$.notaMedia").value(4.0));
    }

    @Test
    @DisplayName("Regras e constraints: e-mail repetido, faixa repetida e remoção com dependentes")
    void regrasEConstraints() throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome": "Outro", "email": "GABRIEL@email.com"}
                """))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/musicas").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "Repetida", "numeroFaixa": 2, "duracaoSegundos": 200, "albumId": 1}
                """))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/artistas/1")).andExpect(status().isConflict());
        mockMvc.perform(delete("/artistas/4")).andExpect(status().isNoContent());
    }
}
