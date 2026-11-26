package com.example.musica.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.musica.ApiTestBase;
import com.example.musica.config.InstanciaFilter;

@DisplayName("Documentação OpenAPI")
class OpenApiDocsTest extends ApiTestBase {

    private static final String PAGES = "https://gabrieldorodrigues.github.io";

    @Test
    @DisplayName("/v3/api-docs descreve a API, os 6 recursos e os erros em Problem Details")
    void especificacaoOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("API de Avaliação Musical"))
                .andExpect(jsonPath("$.paths['/artistas']").exists())
                .andExpect(jsonPath("$.paths['/generos']").exists())
                .andExpect(jsonPath("$.paths['/albuns/{id}']").exists())
                .andExpect(jsonPath("$.paths['/musicas']").exists())
                .andExpect(jsonPath("$.paths['/usuarios']").exists())
                .andExpect(jsonPath("$.paths['/avaliacoes'].post.responses['422'].content['application/problem+json']")
                        .exists())
                .andExpect(jsonPath("$.components.schemas.Problema").exists());
    }

    @Test
    @DisplayName("/swagger-ui.html redireciona para a Swagger UI")
    void swaggerUi() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    @DisplayName("Grava a especificação em target/openapi.json para a Swagger UI do GitHub Pages")
    void exportaEspecificacao() throws Exception {
        String especificacao = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // o hook mkdocs-hooks/swagger.py publica este arquivo no site
        Files.writeString(Path.of("target", "openapi.json"), especificacao);
    }

    @Test
    @DisplayName("CORS: a Swagger UI do GitHub Pages pode chamar a API pelo navegador")
    void corsLiberadoParaOPages() throws Exception {
        mockMvc.perform(options("/avaliacoes")
                        .header("Origin", PAGES)
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", PAGES));

        mockMvc.perform(get("/artistas/1").header("Origin", PAGES))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", PAGES))
                .andExpect(header().string("Access-Control-Expose-Headers", InstanciaFilter.HEADER));
    }

    @Test
    @DisplayName("CORS: outra origem é recusada (403)")
    void corsRecusaOutraOrigem() throws Exception {
        mockMvc.perform(options("/avaliacoes")
                        .header("Origin", "https://outro-site.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
