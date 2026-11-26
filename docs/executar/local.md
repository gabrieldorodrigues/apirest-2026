# Executar localmente (H2)

!!! note "Pré-requisito"
    JDK 17 ou superior instalado.

=== "IntelliJ"

    1. Abra a pasta do projeto.
    2. Espere o Maven baixar as dependências. Se o projeto já estava aberto e o `pom.xml` mudou, use **Maven → Reload All Maven Projects**.
    3. Rode a classe `MusicaApplication`.

=== "Terminal (Linux/macOS)"

    ```bash
    ./mvnw spring-boot:run
    ```

A API sobe em `http://localhost:8080`. Na primeira execução o banco H2 é criado em `./data/`, e um seed cadastra gêneros, artistas, álbuns, músicas, usuários e avaliações de exemplo (veja [Dados de exemplo](../dominio.md#dados-de-exemplo)).

!!! tip "Voltar ao estado inicial"
    Pare a API e apague a pasta `data/`. Na próxima execução o seed roda de novo.

## Console do H2

Disponível em `http://localhost:8080/h2-console`:

| Campo | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:file:./data/musica` |
| Usuário | `sa` |
| Senha | (vazia) |

## Logs

Os logs saem no console e no arquivo `logs/musica.log` (veja [Observabilidade](../extras/observabilidade.md#logs)).

## Testes

=== "IntelliJ"

    Botão direito em `src/test/java` → **Run 'All Tests'**.

=== "Terminal"

    ```bash
    ./mvnw test
    ```

Os testes do PostgreSQL usam o Testcontainers e precisam do Docker rodando; sem Docker, eles são pulados. Veja [Testes automatizados](../extras/testes.md).
