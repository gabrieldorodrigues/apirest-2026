# PostgreSQL

!!! abstract "Requisito extra · 5%"
    Issue [#3](https://github.com/gabrieldorodrigues/apirest-2026/issues/3) · PR [#9](https://github.com/gabrieldorodrigues/apirest-2026/pull/9)

A API roda em **PostgreSQL 17** além do H2. A troca é feita pelo **profile `postgres`** do Spring, **sem mudar nenhuma linha do código de domínio**: as mesmas entidades, consultas JPQL e regras funcionam nos dois bancos. O H2 continua como padrão para desenvolvimento e para os testes.

## Como foi feito

- Driver `org.postgresql:postgresql` no `pom.xml`;
- `application-postgres.properties`, ativado com `SPRING_PROFILES_ACTIVE=postgres`:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/musica}
spring.datasource.username=${DB_USER:musica}
spring.datasource.password=${DB_PASSWORD:musica}
# o console do H2 não faz sentido com PostgreSQL
spring.h2.console.enabled=false
```

O Hibernate detecta o dialeto pela conexão e cria as tabelas na primeira execução (`ddl-auto=update`).

## O que muda entre H2 e PostgreSQL (e foi testado)

O PostgreSQL é mais rígido que o H2 em alguns pontos. Todos foram cobertos pelo `PostgresIntegracaoTest`:

| Ponto de atenção | Onde aparece | Resultado |
| --- | --- | --- |
| Parâmetros nulos em `:param is null` | Filtros opcionais das listagens | Funciona sem mudanças |
| `lower(...) like` | Filtros de texto | Funciona sem mudanças |
| `avg` sobre `numeric(2,1)` | Nota média | Funciona sem mudanças |
| Constraints únicas | E-mail, gênero, faixa por álbum | 409 como no H2 |
| Colunas de identidade | Ids gerados | Funciona sem mudanças |
| Seed com várias instâncias | `SeedConfig` | Lock `pg_advisory_xact_lock` (veja [Load balancer](load-balancer.md#seed-seguro)) |

## Testes com Testcontainers

O `PostgresIntegracaoTest` sobe um **PostgreSQL 17 num container** durante o `./mvnw test` e roda a API com o profile `postgres` contra ele:

```java
@SpringBootTest(properties = "logging.file.name=")
@AutoConfigureMockMvc
@ActiveProfiles("postgres")
@Testcontainers(disabledWithoutDocker = true)
@Transactional
class PostgresIntegracaoTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");
    // ...
}
```

O `@ServiceConnection` liga a API ao container automaticamente. Sem Docker, a classe é pulada.

## Como rodar

Veja [Executar com PostgreSQL](../executar/postgresql.md) para rodar a API no IntelliJ ou no terminal, ou [Docker](../executar/docker.md) para subir tudo junto.

## Evidências

![API com o profile postgres: /actuator/health com database PostgreSQL](../evidencias/20-postgres-health.png){ .evidencia }

![Tabelas criadas pelo Hibernate e álbuns do seed no PostgreSQL](../evidencias/21-postgres-dados.png){ .evidencia }

![PostgresIntegracaoTest passando](../evidencias/22-testes-postgres.png){ .evidencia }
