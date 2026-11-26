# Executar com Docker

!!! note "Pré-requisito"
    [Docker Desktop](https://www.docker.com/products/docker-desktop/) aberto, com a engine rodando.

```bash
docker compose up --build
```

Na primeira vez o build baixa as imagens e as dependências do Maven, o que leva alguns minutos. Depois disso a API responde em `http://localhost:8080`.

## O que sobe

```bash
docker compose ps
```

```text
NAME                 IMAGE                 SERVICE   STATUS                    PORTS
apirest-2026-api-1   apirest-2026-musica   api       Up (healthy)              8080/tcp
apirest-2026-api-2   apirest-2026-musica   api       Up (healthy)              8080/tcp
apirest-2026-api-3   apirest-2026-musica   api       Up (healthy)              8080/tcp
musica-db            postgres:17-alpine    db        Up (healthy)              0.0.0.0:5432->5432/tcp
musica-lb            nginx:1.28-alpine     lb        Up (healthy)              0.0.0.0:8080->80/tcp
```

| Serviço | O que faz |
| --- | --- |
| `lb` (`musica-lb`) | Nginx na porta 8080: distribui as requisições entre as instâncias da API |
| `api` (3 réplicas) | A API com o profile `postgres`. Não publica porta; só o `lb` fica exposto |
| `db` (`musica-db`) | PostgreSQL 17 com os dados no volume `musica-pgdata` |
| `redis` (`musica-redis`) | Redis 8, o cache dos álbuns, compartilhado pelas réplicas (veja [Carta-desafio](../carta-desafio.md)) |

Como isso funciona por dentro está em [Docker/Compose](../extras/docker.md) e [Load balancer](../extras/load-balancer.md).

## Comandos úteis

```bash
docker compose ps                     # situação dos serviços
docker compose logs -f api            # logs das 3 instâncias (prefixo api-1, api-2, api-3)
docker compose down                   # para tudo e mantém os dados
docker compose down -v                # para tudo e apaga os dados (o seed roda de novo)
docker compose up -d --scale api=5    # muda o número de instâncias
docker exec -it musica-db psql -U musica -d musica   # entra no PostgreSQL
```

## Configuração

Copie o `.env.example` para `.env` para trocar portas, credenciais ou o número de réplicas:

```properties
POSTGRES_DB=musica
POSTGRES_USER=musica
POSTGRES_PASSWORD=musica
API_PORT=8080
DB_PORT=5432
API_REPLICAS=3
```

!!! warning "Porta 8080 ocupada"
    Enquanto o Compose estiver no ar, a porta 8080 é do Nginx, então a `MusicaApplication` no IntelliJ não sobe. Use `docker compose down` antes, ou troque a porta com `API_PORT=8081` no `.env`.
