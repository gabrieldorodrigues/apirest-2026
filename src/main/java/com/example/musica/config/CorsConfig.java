package com.example.musica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o navegador a chamar a API a partir de outras origens, como a Swagger UI publicada no GitHub Pages:
 * o "Try it out" da página do Pages faz as requisições para a API rodando em localhost:8080.
 * As origens ficam em api.cors.origens (variável de ambiente API_CORS_ORIGENS).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origens;

    public CorsConfig(@Value("${api.cors.origens}") String[] origens) {
        this.origens = origens;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origens)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                // deixa o navegador ler o header que mostra qual instância respondeu
                .exposedHeaders(InstanciaFilter.HEADER);
    }
}
