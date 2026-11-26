package com.example.musica.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Registra no log cada requisição da API com método, rota, status e tempo de resposta,
 * por exemplo: {@code POST /avaliacoes -> 201 (18 ms)}. Erros do cliente (4xx) saem como WARN.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // não polui o log com Actuator, Swagger e console do H2
        String uri = request.getRequestURI();
        return uri.startsWith("/actuator") || uri.startsWith("/swagger-ui") || uri.startsWith("/v3/api-docs")
                || uri.startsWith("/h2-console");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long inicio = System.currentTimeMillis();
        try {
            chain.doFilter(request, response);
        } finally {
            long duracao = System.currentTimeMillis() - inicio;
            String rota = request.getQueryString() == null
                    ? request.getRequestURI()
                    : request.getRequestURI() + "?" + request.getQueryString();
            int status = response.getStatus();
            if (status >= 400) {
                log.warn("{} {} -> {} ({} ms)", request.getMethod(), rota, status, duracao);
            } else {
                log.info("{} {} -> {} ({} ms)", request.getMethod(), rota, status, duracao);
            }
        }
    }
}
