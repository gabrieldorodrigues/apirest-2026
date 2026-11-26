# API de Avaliação Musical

**API REST para avaliar álbuns e músicas.** O usuário registra o que ouviu, dá uma nota de 0,5 a 5 e escreve uma resenha. A ideia segue o modelo do [Letterboxd](https://letterboxd.com/), aplicado à música.

Projeto final da disciplina de API REST · Tema **Entretenimento e Cultura**.

<div class="grid cards" markdown>

-   :material-rocket-launch: **Rodar em 1 comando**

    ---

    `docker compose up --build` sobe o PostgreSQL, 3 instâncias da API e o load balancer.

    [:octicons-arrow-right-24: Como executar](executar/index.md)

-   :material-api: **Explorar a API**

    ---

    31 endpoints REST com paginação, filtros, ordenação e erros em Problem Details.

    [:octicons-arrow-right-24: Endpoints](api/endpoints.md)

-   :material-sitemap: **Entender o domínio**

    ---

    Artistas, álbuns, músicas, gêneros, usuários e avaliações, com regras e diagrama UML.

    [:octicons-arrow-right-24: Domínio](dominio.md)

-   :material-chart-bar: **Carta-desafio**

    ---

    Relatório das avaliações de um período: totais, nota média, gêneros, álbuns e músicas mais avaliados.

    [:octicons-arrow-right-24: Relatório por período](carta-desafio.md)

-   :material-star-plus: **Requisitos extras**

    ---

    Testes, Swagger, observabilidade, PostgreSQL, Docker e load balancer.

    [:octicons-arrow-right-24: Extras](extras/index.md)

</div>

## Problema

Quem ouve música não tem um lugar central para registrar e comparar opiniões sobre álbuns e faixas. As plataformas de streaming tocam a música, mas guardam pouca opinião do ouvinte. A avaliação de um álbum inteiro também é diferente da avaliação de cada faixa.

A API resolve isso com estas funções:

- registrar artistas, álbuns, músicas e gêneros;
- permitir que cada usuário avalie álbuns **e** músicas com nota e resenha;
- listar e ordenar álbuns e músicas por ano, gênero e artista, sempre com a **nota média** calculada das avaliações;
- consultar o **histórico de avaliações** de cada usuário.

## Exemplo rápido

=== "Avaliar um álbum"

    ```http
    POST /avaliacoes
    Content-Type: application/json

    {
      "usuarioId": 3,
      "albumId": 1,
      "nota": 4.5,
      "resenha": "Atmosférico do começo ao fim."
    }
    ```

=== "Resposta (201)"

    ```json
    {
      "id": 10,
      "usuarioId": 3,
      "usuarioNome": "João",
      "albumId": 1,
      "albumTitulo": "OK Computer",
      "nota": 4.5,
      "resenha": "Atmosférico do começo ao fim.",
      "criadaEm": "2026-11-26T02:34:56.294784",
      "atualizadaEm": "2026-11-26T02:34:56.294784"
    }
    ```

=== "Avaliar de novo (409)"

    ```json
    {
      "title": "Conflito",
      "status": 409,
      "detail": "Este usuário já avaliou este álbum",
      "instance": "/avaliacoes"
    }
    ```

## Tecnologias

| Camada | Tecnologia |
| --- | --- |
| Linguagem e framework | Java 17 · Spring Boot 4.1.1 (Web MVC, Data JPA, Validation) |
| Banco de dados | H2 em arquivo (padrão) · PostgreSQL 17 (profile `postgres`) |
| Documentação da API | springdoc-openapi 3.1.1 (Swagger UI e OpenAPI 3) |
| Observabilidade | Spring Boot Actuator · Micrometer · SLF4J/Logback |
| Testes | JUnit 5 · MockMvc · Testcontainers |
| Infraestrutura | Docker · Docker Compose · Nginx (load balancer) |
| Build | Maven |

## Integrantes

| Integrante | Papel |
| --- | --- |
| Gabriel de Oliveira Rodrigues | Modelagem, implementação, testes e documentação |
