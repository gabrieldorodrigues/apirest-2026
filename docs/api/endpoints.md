# Endpoints e DTOs

Cada entidade tem um DTO de entrada (`RequestDto`) e um de saída (`ResponseDto`). **As entidades JPA nunca são expostas diretamente.** Os campos `id`, `criadoEm`, `criadaEm`, `atualizadaEm` e `notaMedia` são gerados pela API.

## Resumo dos DTOs

| Recurso | Entrada | Saída |
| --- | --- | --- |
| Artista | `nome`, `pais`, `anoInicio` | `id`, `nome`, `pais`, `anoInicio` |
| Gênero | `nome` | `id`, `nome` |
| Álbum | `titulo`, `anoLancamento`, `capaUrl`, `artistaId`, `generoIds` | `id`, `titulo`, `anoLancamento`, `capaUrl`, `artista` (objeto), `generos` (lista), `notaMedia` |
| Música | `titulo`, `numeroFaixa`, `duracaoSegundos`, `albumId` | `id`, `titulo`, `numeroFaixa`, `duracaoSegundos`, `albumId`, `albumTitulo`, `notaMedia` |
| Usuário | `nome`, `email` | `id`, `nome`, `email`, `criadoEm` |
| Avaliação | `usuarioId`, `albumId` **ou** `musicaId`, `nota`, `resenha` | `id`, `usuarioId`, `usuarioNome`, `albumId`/`albumTitulo` **ou** `musicaId`/`musicaTitulo`, `nota`, `resenha`, `criadaEm`, `atualizadaEm` |

## Artistas

| Campo | Tipo | Regras |
| --- | --- | --- |
| `nome` | texto | obrigatório, até 100 caracteres |
| `pais` | texto | opcional, até 60 caracteres |
| `anoInicio` | inteiro | opcional, de 1900 a 2100 |

=== "POST /artistas"

    ```json
    { "nome": "Gilberto Gil", "pais": "Brasil", "anoInicio": 1962 }
    ```

=== "Resposta (201)"

    ```json
    { "id": 5, "nome": "Gilberto Gil", "pais": "Brasil", "anoInicio": 1962 }
    ```

Remover um artista que tem álbuns retorna **409**.

## Gêneros

| Campo | Tipo | Regras |
| --- | --- | --- |
| `nome` | texto | obrigatório, até 50 caracteres, único sem diferenciar maiúsculas |

Remover um gênero usado por algum álbum retorna **409**.

## Álbuns

| Campo | Tipo | Regras |
| --- | --- | --- |
| `titulo` | texto | obrigatório, até 150 caracteres |
| `anoLancamento` | inteiro | obrigatório, de 1900 a 2100 |
| `capaUrl` | texto | opcional, até 300 caracteres |
| `artistaId` | id | obrigatório; o artista precisa existir (senão 404) |
| `generoIds` | lista de ids | pelo menos um; cada gênero precisa existir (senão 404) |

=== "POST /albuns"

    ```json
    {
      "titulo": "Racional Vol. 1",
      "anoLancamento": 1975,
      "artistaId": 5,
      "generoIds": [3, 6]
    }
    ```

=== "Resposta (201)"

    ```json
    {
      "id": 5,
      "titulo": "Racional Vol. 1",
      "anoLancamento": 1975,
      "capaUrl": null,
      "artista": { "id": 5, "nome": "Tim Maia", "pais": "Brasil", "anoInicio": 1959 },
      "generos": [ { "id": 3, "nome": "MPB" }, { "id": 6, "nome": "Soul" } ],
      "notaMedia": null
    }
    ```

=== "GET /albuns/1"

    ```json
    {
      "id": 1,
      "titulo": "OK Computer",
      "anoLancamento": 1997,
      "capaUrl": null,
      "artista": { "id": 1, "nome": "Radiohead", "pais": "Reino Unido", "anoInicio": 1985 },
      "generos": [ { "id": 2, "nome": "Alternativo" }, { "id": 1, "nome": "Rock" } ],
      "notaMedia": 4.75
    }
    ```

A `notaMedia` é a média das avaliações do álbum (5,0 e 4,5 no seed) e fica `null` sem avaliações. Remover um álbum com músicas ou avaliações retorna **409**.

## Músicas

| Campo | Tipo | Regras |
| --- | --- | --- |
| `titulo` | texto | obrigatório, até 150 caracteres |
| `numeroFaixa` | inteiro | obrigatório, maior que zero, **único dentro do álbum** (senão 409) |
| `duracaoSegundos` | inteiro | obrigatório, maior que zero |
| `albumId` | id | obrigatório; o álbum precisa existir (senão 404) |

=== "POST /musicas"

    ```json
    { "titulo": "Exit Music (For a Film)", "numeroFaixa": 4, "duracaoSegundos": 264, "albumId": 1 }
    ```

=== "Resposta (201)"

    ```json
    {
      "id": 10,
      "titulo": "Exit Music (For a Film)",
      "numeroFaixa": 4,
      "duracaoSegundos": 264,
      "albumId": 1,
      "albumTitulo": "OK Computer",
      "notaMedia": null
    }
    ```

Remover uma música com avaliações retorna **409**.

## Usuários

| Campo | Tipo | Regras |
| --- | --- | --- |
| `nome` | texto | obrigatório, até 100 caracteres |
| `email` | texto | obrigatório, formato de e-mail, **único** sem diferenciar maiúsculas; é salvo em minúsculo |

=== "POST /usuarios"

    ```json
    { "nome": "Maria", "email": "Maria@Email.com" }
    ```

=== "Resposta (201)"

    ```json
    { "id": 4, "nome": "Maria", "email": "maria@email.com", "criadoEm": "2026-11-26T03:10:00.123456" }
    ```

Remover um usuário com avaliações retorna **409**.

## Avaliações

| Campo | Tipo | Regras |
| --- | --- | --- |
| `usuarioId` | id | obrigatório; o usuário precisa existir (senão 404) |
| `albumId` | id | informe este **ou** `musicaId` (os dois ou nenhum: 422) |
| `musicaId` | id | informe este **ou** `albumId` |
| `nota` | decimal | obrigatória, de 0,5 a 5 (senão 400), em passos de 0,5 (senão 422) |
| `resenha` | texto | opcional, até 2000 caracteres |

=== "Avaliar um álbum"

    ```json
    { "usuarioId": 3, "albumId": 1, "nota": 4.5, "resenha": "Atmosférico do começo ao fim." }
    ```

=== "Avaliar uma música"

    ```json
    { "usuarioId": 2, "musicaId": 1, "nota": 4.0 }
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

A resposta traz só os campos do item avaliado: `albumId`/`albumTitulo` para álbum ou `musicaId`/`musicaTitulo` para música. O mesmo usuário avaliar o mesmo álbum (ou música) de novo retorna **409**; para mudar a nota, use `PUT /avaliacoes/{id}`. O `atualizadaEm` é atualizado a cada `PUT`.
