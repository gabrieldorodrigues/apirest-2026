# Docker/Compose

!!! abstract "Requisito extra · 5%"
    Issue [#4](https://github.com/gabrieldorodrigues/apirest-2026/issues/4) · PR [#10](https://github.com/gabrieldorodrigues/apirest-2026/pull/10)

A API e o PostgreSQL sobem com um único comando, sem precisar de Java nem de banco instalados:

```bash
docker compose up --build
```

## Dockerfile

O `Dockerfile` é **multi-stage**:

```dockerfile
# Etapa 1: compila e gera o .jar com Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline     # camada de dependências, refeita só quando o pom.xml muda
COPY src ./src
RUN mvn -B -q package -DskipTests

# Etapa 2: imagem final só com o JRE e o .jar
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S musica && adduser -S musica -G musica && mkdir logs && chown musica:musica logs
USER musica
COPY --from=build /build/target/musica-*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=40s --retries=5 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

| Decisão | Por quê |
| --- | --- |
| Multi-stage | A imagem final não leva Maven nem JDK, só o JRE e o `.jar` |
| Dependências numa camada separada | Mudar o código não baixa as dependências de novo |
| Usuário `musica` sem root | Segurança: o processo não roda como root no container |
| `HEALTHCHECK` no `/actuator/health` | O Docker sabe quando a API está pronta, e não só quando o processo subiu |
| `.dockerignore` | `target/`, `data/`, `logs/`, `.git/` e evidências ficam fora do build |

## docker-compose.yml

| Serviço | O que faz |
| --- | --- |
| `db` | PostgreSQL 17 com healthcheck (`pg_isready`) e dados no volume `musica-pgdata` |
| `api` | Construída pelo `Dockerfile`, com o profile `postgres` apontando para `db:5432`; logs no volume `musica-logs` |
| `lb` | Nginx na frente das réplicas da API, adicionado no [Load balancer](load-balancer.md) |

- A API só sobe depois que o PostgreSQL aceita conexões (`depends_on` com `condition: service_healthy`);
- Os dados ficam no volume e **sobrevivem** a um `docker compose down` (só o `down -v` apaga);
- Portas, credenciais e número de réplicas vêm do `.env` (veja o `.env.example`);
- Os containers usam o fuso `America/Sao_Paulo`, igual à máquina de desenvolvimento.

## O que foi verificado

| Cenário | Resultado |
| --- | --- |
| `docker compose up --build` | Serviços `healthy`; `/actuator/health` com `"database": "PostgreSQL"` |
| Criar uma avaliação, `docker compose down` e `up` de novo | A avaliação continua lá (volume persistente) |
| Usuário do processo no container | `musica` (sem root) |

Os comandos do dia a dia estão em [Executar com Docker](../executar/docker.md).

## Evidência

![docker compose ps com a API e o banco healthy (versão com uma instância)](../evidencias/23-docker-compose-ps.png){ .evidencia }
