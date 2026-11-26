# Observabilidade

!!! abstract "Requisito extra · 5%"
    Issue [#2](https://github.com/gabrieldorodrigues/apirest-2026/issues/2) · PR [#8](https://github.com/gabrieldorodrigues/apirest-2026/pull/8)

A API usa o **Spring Boot Actuator** com **Micrometer** para health check, informações e métricas, e **SLF4J/Logback** para os logs.

## Endpoints do Actuator

| Endpoint | O que mostra |
| --- | --- |
| `GET /actuator/health` | Situação da API (`UP`/`DOWN`) com os detalhes de cada componente: banco (`db`), disco (`diskSpace`), `liveness` e `readiness` |
| `GET /actuator/info` | Nome, descrição e repositório da API; versão e data do build; Java; sistema operacional; instância que respondeu |
| `GET /actuator/metrics` | Lista de todas as métricas disponíveis |
| `GET /actuator/metrics/{nome}` | Valor de uma métrica. Aceita filtro por tag, ex.: `?tag=tipo:album` |

Só esses endpoints ficam expostos:

```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

O health check também é usado pelo Docker: o `HEALTHCHECK` do `Dockerfile` consulta `/actuator/health`, e o load balancer só manda tráfego para instâncias `healthy`.

## Métricas

Além das métricas automáticas, como `http.server.requests` (requisições por rota, status e tempo), `jvm.memory.used` e `hikaricp.connections.active`, a API registra duas **métricas do domínio** no `AvaliacaoService`:

| Métrica | Tipo | O que mede |
| --- | --- | --- |
| `musica.avaliacoes.criadas` | Counter, com a tag `tipo` (`album` ou `musica`) | Avaliações criadas desde que a API subiu. Avaliações recusadas (400, 409, 422) não contam |
| `musica.avaliacoes.total` | Gauge | Total de avaliações no banco no momento da consulta |

```java
Counter.builder("musica.avaliacoes.criadas")
        .description("Avaliações criadas desde que a API subiu")
        .tag("tipo", tipo)
        .register(meterRegistry)
        .increment();
```

Exemplos:

| Requisição | O que mostra |
| --- | --- |
| `GET /actuator/metrics/musica.avaliacoes.criadas?tag=tipo:album` | Avaliações de álbum criadas |
| `GET /actuator/metrics/http.server.requests?tag=uri:/avaliacoes&tag=status:409` | Quantas vezes `/avaliacoes` respondeu 409 |

!!! note "Com várias instâncias"
    `musica.avaliacoes.criadas` é contada por instância, porque cada réplica tem o próprio Micrometer. `musica.avaliacoes.total` lê do banco e é igual em todas.

## Logs

Os logs saem no console e em arquivo (`logs/musica.log`, com rotação automática e fora do Git). No Docker, cada instância grava o próprio arquivo (`logs/musica-<hostname>.log`).

| Origem | Nível | Exemplo |
| --- | --- | --- |
| `RequestLoggingFilter` (toda requisição da API) | INFO; WARN para 4xx | `POST /avaliacoes -> 201 (18 ms)` |
| `AvaliacaoService` (criar, atualizar, remover) | INFO | `Avaliação 10 criada: usuário 3 avaliou album 1 com nota 4.5` |
| `AvaliacaoService` (regras violadas) | WARN | `Avaliação recusada (409): usuário 1 já avaliou o álbum 1` |
| `ApiExceptionHandler` (erro inesperado) | ERROR, com stack trace | O cliente recebe só o 500 em Problem Details, sem detalhes internos |

As requisições do Actuator, do Swagger e do console do H2 não entram no log de requisições. Os testes automatizados logam só no console.

## Evidências

![/actuator/health com status UP e detalhes do banco e do disco](../evidencias/17-actuator-health.png){ .evidencia }

![Métrica musica.avaliacoes.criadas com 1 avaliação de álbum](../evidencias/18-actuator-metricas.png){ .evidencia }

![logs/musica.log com a avaliação criada e o log da requisição](../evidencias/19-logs.png){ .evidencia }
