package com.example.musica.config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Identifica a instância da API que atendeu a requisição. Com várias réplicas atrás do load balancer,
 * o header X-Instancia mostra qual delas respondeu; o mesmo identificador aparece no /actuator/info.
 * Dentro do Docker o identificador é o hostname do container.
 */
@Component
public class InstanciaFilter extends OncePerRequestFilter implements InfoContributor {

    public static final String HEADER = "X-Instancia";

    private final String instancia = hostname();
    private final LocalDateTime iniciadaEm = LocalDateTime.now();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        response.setHeader(HEADER, instancia);
        chain.doFilter(request, response);
    }

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("id", instancia);
        dados.put("iniciadaEm", iniciadaEm.toString());
        builder.withDetail("instancia", dados);
    }

    private static String hostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "desconhecida";
        }
    }
}
