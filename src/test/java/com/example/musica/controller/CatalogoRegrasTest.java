package com.example.musica.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.example.musica.ApiTestBase;

@DisplayName("Regras do catálogo (usuários, músicas, artistas e gêneros)")
class CatalogoRegrasTest extends ApiTestBase {

    @Test
    @DisplayName("POST cria usuário com e-mail salvo em minúsculo")
    void criaUsuario() throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome": "Maria", "email": "Maria@Email.com"}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.criadoEm").isNotEmpty());
    }

    @Test
    @DisplayName("409 quando o e-mail já existe, sem diferenciar maiúsculas")
    void emailRepetido() throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome": "Outro Gabriel", "email": "GABRIEL@email.com"}
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Já existe um usuário com o e-mail gabriel@email.com"));
    }

    @Test
    @DisplayName("400 com nome vazio e e-mail inválido")
    void usuarioInvalido() throws Exception {
        mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome": "", "email": "nao-e-email"}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.errors.nome").value("O nome é obrigatório"))
                .andExpect(jsonPath("$.errors.email").value("O e-mail é inválido"));
    }

    @Test
    @DisplayName("409 ao remover usuário que tem avaliações")
    void naoRemoveUsuarioComAvaliacoes() throws Exception {
        mockMvc.perform(delete("/usuarios/1")).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("409 quando o número da faixa já existe no álbum")
    void faixaRepetidaNoAlbum() throws Exception {
        mockMvc.perform(post("/musicas").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "Outra faixa 2", "numeroFaixa": 2, "duracaoSegundos": 200, "albumId": 1}
                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Já existe a faixa 2 neste álbum"));
    }

    @Test
    @DisplayName("O mesmo número de faixa é permitido em outro álbum")
    void mesmaFaixaEmOutroAlbum() throws Exception {
        mockMvc.perform(post("/musicas").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo": "Bodysnatchers", "numeroFaixa": 2, "duracaoSegundos": 242, "albumId": 2}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.albumTitulo").value("In Rainbows"));
    }

    @Test
    @DisplayName("GET de músicas do álbum ordenadas pela faixa")
    void musicasDoAlbumOrdenadas() throws Exception {
        mockMvc.perform(get("/musicas").param("albumId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo").value("Airbag"))
                .andExpect(jsonPath("$.content[1].titulo").value("Paranoid Android"))
                .andExpect(jsonPath("$.content[2].titulo").value("Karma Police"));
    }

    @Test
    @DisplayName("409 ao remover música que tem avaliações")
    void naoRemoveMusicaComAvaliacoes() throws Exception {
        mockMvc.perform(delete("/musicas/4")).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("409 ao remover artista que tem álbuns")
    void naoRemoveArtistaComAlbuns() throws Exception {
        mockMvc.perform(delete("/artistas/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Não é possível remover um artista que possui álbuns cadastrados"));
    }

    @Test
    @DisplayName("DELETE remove artista sem álbuns")
    void removeArtistaSemAlbuns() throws Exception {
        mockMvc.perform(delete("/artistas/4")).andExpect(status().isNoContent());
        mockMvc.perform(get("/artistas/4")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("409 ao remover gênero usado por álbuns")
    void naoRemoveGeneroEmUso() throws Exception {
        mockMvc.perform(delete("/generos/1")).andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET filtra artistas por país sem diferenciar maiúsculas")
    void filtraArtistasPorPais() throws Exception {
        mockMvc.perform(get("/artistas").param("pais", "brasil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    @DisplayName("400 quando o id da rota não é número")
    void idInvalidoNaRota() throws Exception {
        mockMvc.perform(get("/artistas/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Parâmetro inválido"));
    }

    @Test
    @DisplayName("400 quando o JSON tem tipo errado")
    void jsonComTipoErrado() throws Exception {
        mockMvc.perform(post("/artistas").contentType(MediaType.APPLICATION_JSON).content("""
                {"nome": "Teste", "anoInicio": "mil novecentos"}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("JSON inválido"));
    }
}
