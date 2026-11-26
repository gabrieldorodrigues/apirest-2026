package com.example.musica.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.musica.ApiTestBase;

@DisplayName("Documentação OpenAPI")
class OpenApiDocsTest extends ApiTestBase {

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
}
