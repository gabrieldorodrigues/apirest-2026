# Paginação, filtros e ordenação

Todos os endpoints de listagem (`GET /{recurso}`) aceitam os parâmetros do Spring Data:

| Parâmetro | Descrição | Padrão |
| --- | --- | --- |
| `page` | Número da página, começando em 0 | `0` |
| `size` | Itens por página | `10` |
| `sort` | Campo e direção, ex.: `sort=anoLancamento,desc`. Pode repetir para ordenar por mais de um campo | depende do recurso |

Um campo de `sort` que não existe retorna **400** ("Ordenação inválida").

## Filtros

Todos os filtros são opcionais e podem ser combinados. Os filtros de texto buscam por **"contém"**, sem diferenciar maiúsculas.

| Recurso | Filtros | Ordenação padrão |
| --- | --- | --- |
| `/artistas` | `nome`, `pais` | `nome` |
| `/generos` | `nome` | `nome` |
| `/albuns` | `titulo`, `ano`, `artistaId`, `generoId` | `titulo` |
| `/musicas` | `titulo`, `albumId` | `numeroFaixa` |
| `/usuarios` | `nome`, `email` | `nome` |
| `/avaliacoes` | `usuarioId`, `albumId`, `musicaId`, `notaMinima` | `criadaEm` |

!!! tip "Histórico do usuário"
    `GET /avaliacoes?usuarioId=1` mostra todas as avaliações de um usuário, ou seja, o histórico dele.

## Formato da resposta

```http
GET /albuns?page=0&size=2&sort=anoLancamento,desc
```

```json
{
  "content": [
    {
      "id": 2,
      "titulo": "In Rainbows",
      "anoLancamento": 2007,
      "capaUrl": null,
      "artista": { "id": 1, "nome": "Radiohead", "pais": "Reino Unido", "anoInicio": 1985 },
      "generos": [ { "id": 2, "nome": "Alternativo" }, { "id": 1, "nome": "Rock" } ],
      "notaMedia": 4.0
    },
    {
      "id": 1,
      "titulo": "OK Computer",
      "anoLancamento": 1997,
      "capaUrl": null,
      "artista": { "id": 1, "nome": "Radiohead", "pais": "Reino Unido", "anoInicio": 1985 },
      "generos": [ { "id": 2, "nome": "Alternativo" }, { "id": 1, "nome": "Rock" } ],
      "notaMedia": 4.75
    }
  ],
  "page": { "size": 2, "number": 0, "totalElements": 4, "totalPages": 2 }
}
```

## Exemplos

| Requisição | O que retorna |
| --- | --- |
| `GET /albuns?artistaId=1&generoId=2` | Álbuns do Radiohead no gênero Alternativo |
| `GET /albuns?ano=1997` | Álbuns lançados em 1997 |
| `GET /musicas?albumId=1&sort=numeroFaixa` | Faixas do OK Computer em ordem |
| `GET /avaliacoes?notaMinima=4.5&sort=nota,desc` | Avaliações com nota 4,5 ou mais, da maior para a menor |
| `GET /avaliacoes?usuarioId=1` | Histórico de avaliações do Gabriel |
| `GET /artistas?pais=brasil` | Artistas do Brasil |
| `GET /usuarios?email=email.com&sort=nome,desc` | Usuários com "email.com" no e-mail, de Z a A |

!!! info "Por que não dá para ordenar por nota média?"
    A `notaMedia` é calculada a partir das avaliações a cada consulta e não fica salva no banco, então não é um campo que o `sort` possa usar. A listagem de avaliações pode ser ordenada por `nota`.
