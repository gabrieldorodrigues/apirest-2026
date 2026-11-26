# Como executar

Há três jeitos de rodar a API. Para avaliar o projeto, o **Docker** é o mais simples: não precisa de Java nem de banco instalados.

<div class="grid cards" markdown>

-   :material-docker: **Docker Compose**

    ---

    PostgreSQL + 3 instâncias da API + load balancer Nginx, com um comando.

    [:octicons-arrow-right-24: Docker](docker.md)

-   :material-language-java: **Localmente com H2**

    ---

    JDK 17 e nada mais. Banco H2 em arquivo, criado na primeira execução.

    [:octicons-arrow-right-24: Local](local.md)

-   :material-elephant: **Localmente com PostgreSQL**

    ---

    A API rodando no IntelliJ ou no terminal, apontando para um PostgreSQL.

    [:octicons-arrow-right-24: PostgreSQL](postgresql.md)

</div>

Em todos os casos a API responde em `http://localhost:8080`:

| Endereço | O que é |
| --- | --- |
| `http://localhost:8080/swagger-ui.html` | Swagger UI para testar os endpoints pelo navegador |
| [Swagger UI do site](/apirest-2026/swagger/) | A mesma Swagger UI publicada no GitHub Pages; o **Try it out** chama a API local |
| `http://localhost:8080/actuator/health` | Situação da API e do banco |
| `http://localhost:8080/albuns` | Exemplo de listagem |

A collection do Postman está em [`postman/collections/apirest-2026`](https://github.com/gabrieldorodrigues/apirest-2026/tree/main/postman/collections/apirest-2026), no formato v3 (YAML). Para usar, abra a pasta raiz do repositório no Postman (Local Mode).

## Variáveis de ambiente

Todas são opcionais.

| Variável | Padrão (H2) | Padrão com o profile `postgres` | Descrição |
| --- | --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | (vazio) | `postgres` | Ativa o PostgreSQL |
| `DB_URL` | `jdbc:h2:file:./data/musica` | `jdbc:postgresql://localhost:5432/musica` | URL JDBC do banco |
| `DB_USER` | `sa` | `musica` | Usuário do banco |
| `DB_PASSWORD` | (vazio) | `musica` | Senha do banco |

No Docker Compose, as variáveis ficam no `.env` (veja o [`.env.example`](https://github.com/gabrieldorodrigues/apirest-2026/blob/main/.env.example)).
