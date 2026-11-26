package com.example.musica.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache dos álbuns (carta-desafio 5). Sem Redis, o cache fica em memória; no docker compose, fica no Redis,
 * compartilhado pelas 3 réplicas, para a invalidação feita numa instância valer para todas.
 * Os nomes, o TTL e o tipo de cache ficam no application.properties (spring.cache.*).
 */
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    /** Um álbum por id: chave = id. */
    public static final String ALBUM = "album";
    /** Páginas da listagem de álbuns: chave = filtros + página + tamanho + ordenação. */
    public static final String ALBUNS = "albuns";

    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    // o evict só roda depois do commit: antes disso, outra réplica poderia ler o banco ainda sem a mudança
    // e guardar de novo o valor antigo
    @Bean
    RedisCacheManagerBuilderCustomizer cacheTransacional() {
        return builder -> builder.transactionAware();
    }

    // com o Redis fora do ar, a consulta segue direto para o banco em vez de devolver 500
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
                log.warn("Cache {} indisponível ao ler {}: {}", cache.getName(), key, ex.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
                log.warn("Cache {} indisponível ao gravar {}: {}", cache.getName(), key, ex.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
                log.warn("Cache {} indisponível ao invalidar {}: {}", cache.getName(), key, ex.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException ex, Cache cache) {
                log.warn("Cache {} indisponível ao limpar: {}", cache.getName(), ex.getMessage());
            }
        };
    }
}
