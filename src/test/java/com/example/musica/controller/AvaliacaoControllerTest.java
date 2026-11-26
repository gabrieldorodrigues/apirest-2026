package com.example.musica.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import com.example.musica.ApiTestBase;

@DisplayName("Avaliações")
class AvaliacaoControllerTest extends ApiTestBase {

    private ResultActions postAvaliacao(String json) throws Exception {
        return mockMvc.perform(post("/avaliacoes").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    @DisplayName("POST cria avaliação de álbum e atualiza a nota média do álbum")
    void criaAvaliacaoDeAlbumEAtualizaNotaMedia() throws Exception {
        postAvaliacao("""
                {"usuarioId": 3, "albumId": 1, "nota": 2.5, "resenha": "Não é pra mim"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.usuarioNome").value("João"))
                .andExpect(jsonPath("$.albumTitulo").value("OK Computer"))
                .andExpect(jsonPath("$.musicaId").doesNotExist())
                .andExpect(jsonPath("$.nota").value(2.5))
                .andExpect(jsonPath("$.criadaEm").isNotEmpty());

        // seed: 5.0 e 4.5; com a nova 2.5 a média fica (5 + 4.5 + 2.5) / 3 = 4.0
        mockMvc.perform(get("/albuns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notaMedia").value(4.0));
    }

    @Test
    @DisplayName("POST cria avaliação de música")
    void criaAvaliacaoDeMusica() throws Exception {
        postAvaliacao("""
                {"usuarioId": 2, "musicaId": 1, "nota": 4.0}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.musicaTitulo").value("Paranoid Android"))
                .andExpect(jsonPath("$.albumId").doesNotExist());
    }

    @Test
    @DisplayName("PUT atualiza a nota e a resenha")
    void atualizaAvaliacao() throws Exception {
        mockMvc.perform(put("/avaliacoes/1").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 1, "albumId": 1, "nota": 4.5, "resenha": "Revisitei e baixei meio ponto"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(4.5))
                .andExpect(jsonPath("$.resenha").value("Revisitei e baixei meio ponto"))
                .andExpect(jsonPath("$.atualizadaEm").isNotEmpty());
    }

    @Test
    @DisplayName("DELETE remove a avaliação e depois o GET retorna 404")
    void removeAvaliacao() throws Exception {
        mockMvc.perform(delete("/avaliacoes/9")).andExpect(status().isNoContent());
        mockMvc.perform(get("/avaliacoes/9")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("409 quando o usuário avalia o mesmo álbum duas vezes")
    void conflitoAvaliacaoRepetidaDeAlbum() throws Exception {
        postAvaliacao("""
                {"usuarioId": 1, "albumId": 1, "nota": 3.0}
                """)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflito"))
                .andExpect(jsonPath("$.detail").value("Este usuário já avaliou este álbum"));
    }

    @Test
    @DisplayName("409 quando o usuário avalia a mesma música duas vezes")
    void conflitoAvaliacaoRepetidaDeMusica() throws Exception {
        postAvaliacao("""
                {"usuarioId": 1, "musicaId": 1, "nota": 3.0}
                """)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Este usuário já avaliou esta música"));
    }

    @Test
    @DisplayName("PUT da própria avaliação não conta como avaliação repetida")
    void atualizarPropriaAvaliacaoNaoEhConflito() throws Exception {
        mockMvc.perform(put("/avaliacoes/6").contentType(MediaType.APPLICATION_JSON).content("""
                {"usuarioId": 1, "musicaId": 1, "nota": 4.5}
                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("422 quando a avaliação aponta para álbum e música ao mesmo tempo")
    void regraAlbumEMusicaAoMesmoTempo() throws Exception {
        postAvaliacao("""
                {"usuarioId": 3, "albumId": 1, "musicaId": 1, "nota": 4.0}
                """)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.title").value("Regra de negócio violada"))
                .andExpect(jsonPath("$.detail").value(containsString("nunca dos dois")));
    }

    @Test
    @DisplayName("422 quando a avaliação não aponta para álbum nem música")
    void regraSemAlbumESemMusica() throws Exception {
        postAvaliacao("""
                {"usuarioId": 3, "nota": 4.0}
                """)
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @DisplayName("422 quando a nota não está em passos de 0,5")
    void regraNotaForaDoPasso() throws Exception {
        postAvaliacao("""
                {"usuarioId": 3, "albumId": 1, "nota": 3.3}
                """)
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value(containsString("passos de 0.5")));
    }

    @Test
    @DisplayName("400 quando a nota está fora do intervalo de 0,5 a 5")
    void validacaoNotaForaDoIntervalo() throws Exception {
        postAvaliacao("""
                {"usuarioId": 3, "albumId": 1, "nota": 5.5}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nota").value("A nota máxima é 5"));

        postAvaliacao("""
                {"usuarioId": 3, "albumId": 1, "nota": 0}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.nota").value("A nota mínima é 0.5"));
    }

    @Test
    @DisplayName("400 quando faltam usuário e nota")
    void validacaoCamposObrigatorios() throws Exception {
        postAvaliacao("""
                {"albumId": 1}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.usuarioId").value("O usuário é obrigatório"))
                .andExpect(jsonPath("$.errors.nota").value("A nota é obrigatória"));
    }

    @Test
    @DisplayName("404 quando o usuário informado não existe")
    void usuarioInexistente() throws Exception {
        postAvaliacao("""
                {"usuarioId": 999, "albumId": 1, "nota": 4.0}
                """)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET com usuarioId mostra o histórico de avaliações do usuário")
    void historicoDoUsuario() throws Exception {
        mockMvc.perform(get("/avaliacoes").param("usuarioId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(3))
                .andExpect(jsonPath("$.content[*].usuarioNome").value(everyItem(is("Gabriel"))));
    }

    @Test
    @DisplayName("GET com notaMinima filtra e ordena por nota decrescente")
    void filtroNotaMinimaOrdenado() throws Exception {
        mockMvc.perform(get("/avaliacoes").param("notaMinima", "4.5").param("sort", "nota,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(6))
                .andExpect(jsonPath("$.content[0].nota").value(5.0))
                .andExpect(jsonPath("$.content[5].nota").value(4.5));
    }
}
