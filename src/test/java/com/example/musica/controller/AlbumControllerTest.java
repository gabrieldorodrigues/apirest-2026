package com.example.musica.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.example.musica.ApiTestBase;

@DisplayName("Álbuns")
class AlbumControllerTest extends ApiTestBase {

    @Test
    @DisplayName("POST cria álbum com artista e gêneros, sem nota média")
    void criaAlbum() throws Exception {
        mockMvc.perform(post("/albuns").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "Kid A", "anoLancamento": 2000, "artistaId": 1, "generoIds": [1, 2]}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.artista.nome").value("Radiohead"))
                .andExpect(jsonPath("$.generos.length()").value(2))
                .andExpect(jsonPath("$.notaMedia").isEmpty());
    }

    @Test
    @DisplayName("GET por id traz a nota média calculada das avaliações")
    void buscaAlbumComNotaMedia() throws Exception {
        // seed: OK Computer tem notas 5.0 e 4.5
        mockMvc.perform(get("/albuns/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("OK Computer"))
                .andExpect(jsonPath("$.notaMedia").value(4.75));
    }

    @Test
    @DisplayName("PUT atualiza o álbum")
    void atualizaAlbum() throws Exception {
        mockMvc.perform(put("/albuns/2").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "In Rainbows (Disk 1)", "anoLancamento": 2007, "artistaId": 1, "generoIds": [2]}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("In Rainbows (Disk 1)"))
                .andExpect(jsonPath("$.generos.length()").value(1));
    }

    @Test
    @DisplayName("DELETE remove álbum sem músicas nem avaliações")
    void removeAlbum() throws Exception {
        String json = mockMvc.perform(post("/albuns").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "Amnesiac", "anoLancamento": 2001, "artistaId": 1, "generoIds": [2]}
                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String id = json.replaceAll("^\\{\"id\":(\\d+).*$", "$1");

        mockMvc.perform(delete("/albuns/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get("/albuns/" + id)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("409 ao remover álbum que tem músicas")
    void naoRemoveAlbumComMusicas() throws Exception {
        mockMvc.perform(delete("/albuns/4"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Não é possível remover um álbum que possui músicas cadastradas"));
    }

    @Test
    @DisplayName("404 no formato Problem Details quando o álbum não existe")
    void albumInexistente() throws Exception {
        mockMvc.perform(get("/albuns/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.detail").value("Álbum com id 999 não existe"))
                .andExpect(jsonPath("$.instance").value("/albuns/999"));
    }

    @Test
    @DisplayName("404 quando um gênero informado no corpo não existe")
    void generoInexistenteAoCriar() throws Exception {
        mockMvc.perform(post("/albuns").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "Kid A", "anoLancamento": 2000, "artistaId": 1, "generoIds": [999]}
                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("400 com a lista de campos inválidos")
    void validacaoCampos() throws Exception {
        mockMvc.perform(post("/albuns").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "", "anoLancamento": 1800, "generoIds": []}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.titulo").value("O título é obrigatório"))
                .andExpect(jsonPath("$.errors.anoLancamento").value("O ano de lançamento deve ser a partir de 1900"))
                .andExpect(jsonPath("$.errors.artistaId").value("O artista é obrigatório"))
                .andExpect(jsonPath("$.errors.generoIds").value("Informe pelo menos um gênero"));
    }

    @Test
    @DisplayName("GET filtra por artista e gênero com paginação e ordenação")
    void filtraPorArtistaEGenero() throws Exception {
        mockMvc.perform(get("/albuns").param("artistaId", "1").param("generoId", "2")
                        .param("sort", "anoLancamento,desc").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("In Rainbows"));
    }

    @Test
    @DisplayName("400 quando o sort usa um campo que não existe")
    void ordenacaoInvalida() throws Exception {
        mockMvc.perform(get("/albuns").param("sort", "naoExiste"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Ordenação inválida"));
    }
}
