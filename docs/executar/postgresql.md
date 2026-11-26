# Executar com PostgreSQL

A API troca o H2 pelo PostgreSQL com o profile `postgres`. Esta página mostra como rodar a API **fora do Docker** (no IntelliJ ou no terminal) apontando para um PostgreSQL. Para subir tudo junto, use o [Docker Compose](docker.md).

## 1. Suba um PostgreSQL

=== "Só o banco do Compose"

    ```bash
    docker compose up -d db
    ```

=== "Container avulso"

    ```bash
    docker run -d --name musica-postgres \
      -e POSTGRES_DB=musica -e POSTGRES_USER=musica -e POSTGRES_PASSWORD=musica \
      -p 5432:5432 postgres:17-alpine
    ```

## 2. Rode a API com o profile `postgres`

=== "IntelliJ"

    Em **Run → Edit Configurations → MusicaApplication**, preencha **Active profiles** com `postgres` (ou adicione a variável de ambiente `SPRING_PROFILES_ACTIVE=postgres`) e rode.

=== "Terminal"

    ```bash
    SPRING_PROFILES_ACTIVE=postgres ./mvnw spring-boot:run
    ```

=== "PowerShell"

    ```powershell
    $env:SPRING_PROFILES_ACTIVE = "postgres"; ./mvnw spring-boot:run
    ```

Na primeira execução o Hibernate cria as tabelas e o seed grava os dados de exemplo. O health check confirma o banco:

```bash
curl http://localhost:8080/actuator/health
```

```json
{
  "status": "UP",
  "components": {
    "db": { "status": "UP", "details": { "database": "PostgreSQL", "validationQuery": "isValid()" } }
  }
}
```

## 3. Consulte os dados no banco

```bash
docker exec -it musica-db psql -U musica -d musica -c "\dt"
docker exec -it musica-db psql -U musica -d musica -c "select titulo, ano_lancamento from albuns;"
```

(Com o container avulso, troque `musica-db` por `musica-postgres`.)

Para usar outro servidor PostgreSQL, informe `DB_URL`, `DB_USER` e `DB_PASSWORD`. O que muda entre H2 e PostgreSQL e como isso é testado está em [PostgreSQL](../extras/postgresql.md).
