# API

A API segue o estilo REST: cada entidade é um recurso com CRUD completo, as respostas são JSON e os erros seguem o formato **Problem Details** (RFC 9457).

| Recurso | Descrição |
| --- | --- |
| `/artistas` | Pessoas ou bandas que lançam álbuns |
| `/generos` | Classificações musicais dos álbuns |
| `/albuns` | Lançamentos de um artista, com nota média |
| `/musicas` | Faixas de um álbum, com nota média |
| `/usuarios` | Pessoas que avaliam álbuns e músicas |
| `/avaliacoes` | Nota e resenha de um usuário sobre um álbum ou uma música |

Além dos recursos, a API tem o relatório `GET /relatorios/avaliacoes`, com os totais das avaliações de um período (veja [Carta-desafio](../carta-desafio.md)).

## Operações

Todos os recursos têm as mesmas cinco operações:

| Método | Rota | Descrição | Sucesso |
| --- | --- | --- | --- |
| `GET` | `/{recurso}` | Lista com paginação, filtros e ordenação | 200 |
| `GET` | `/{recurso}/{id}` | Busca por id | 200 |
| `POST` | `/{recurso}` | Cria | 201 |
| `PUT` | `/{recurso}/{id}` | Atualiza | 200 |
| `DELETE` | `/{recurso}/{id}` | Remove | 204 |

## Onde testar

<div class="grid cards" markdown>

-   :material-file-document-outline: **Swagger UI**

    ---

    Documentação interativa gerada do código, com **Try it out** já preenchido com dados do seed.

    [Swagger UI publicada](/apirest-2026/swagger/) · `http://localhost:8080/swagger-ui.html` · [Mais sobre o Swagger](../extras/swagger.md)

-   :material-email-fast-outline: **Postman**

    ---

    Collection com listagens, CRUD, erros e remoções, organizada em pastas.

    [`postman/collections/apirest-2026`](https://github.com/gabrieldorodrigues/apirest-2026/tree/main/postman/collections/apirest-2026)

</div>

## Nesta seção

- [Endpoints e DTOs](endpoints.md): o que cada recurso recebe e devolve, com exemplos.
- [Paginação, filtros e ordenação](listagens.md): parâmetros das listagens.
- [Validação e erros](erros.md): códigos HTTP e formato dos erros.
