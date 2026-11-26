# Validação e erros

A validação dos campos usa **Bean Validation** nos DTOs de entrada (`@NotBlank`, `@NotNull`, `@Size`, `@Email`, `@Min`, `@Max`, `@Positive`, `@DecimalMin`, `@DecimalMax`). As regras de negócio ficam nos services.

Todos os erros passam pelo `ApiExceptionHandler` e voltam no formato **Problem Details** (RFC 9457), com `Content-Type: application/problem+json`.

## Códigos de erro

| Código | Título | Quando acontece |
| --- | --- | --- |
| **400** | Erro de validação · JSON inválido · Parâmetro inválido · Parâmetro obrigatório · Ordenação inválida | Campo obrigatório vazio, formato inválido, nota fora de 0,5 a 5, JSON mal formatado ou com tipo errado, id inválido na rota, data inválida no relatório, parâmetro obrigatório ausente, campo de `sort` inexistente |
| **404** | Recurso não encontrado · Rota não encontrada | O recurso não existe, inclusive ids informados no corpo (`artistaId`, `generoIds`, `usuarioId`...) |
| **409** | Conflito | E-mail repetido, gênero repetido, faixa repetida no álbum, usuário avaliando o mesmo álbum/música duas vezes, remoção de registro que tem dependentes |
| **422** | Regra de negócio violada | Avaliação com álbum e música ao mesmo tempo (ou sem nenhum dos dois), nota fora do passo de 0,5, relatório com data inicial depois da final ou período maior que 1 ano |
| **500** | Erro interno | Erro inesperado. A mensagem não expõe detalhes internos; o stack trace vai só para o log |

## Exemplos

=== "400"

    `POST /usuarios` com `{"nome": "", "email": "nao-e-email"}`:

    ```json
    {
      "title": "Erro de validação",
      "status": 400,
      "detail": "Um ou mais campos são inválidos",
      "instance": "/usuarios",
      "errors": {
        "nome": "O nome é obrigatório",
        "email": "O e-mail é inválido"
      }
    }
    ```

    O campo `errors` lista cada campo inválido com a mensagem.

=== "404"

    `GET /albuns/999`:

    ```json
    {
      "title": "Recurso não encontrado",
      "status": 404,
      "detail": "Álbum com id 999 não existe",
      "instance": "/albuns/999"
    }
    ```

=== "409"

    `POST /avaliacoes` de um usuário que já avaliou o álbum:

    ```json
    {
      "title": "Conflito",
      "status": 409,
      "detail": "Este usuário já avaliou este álbum",
      "instance": "/avaliacoes"
    }
    ```

=== "422"

    `POST /avaliacoes` com `albumId` e `musicaId` ao mesmo tempo:

    ```json
    {
      "title": "Regra de negócio violada",
      "status": 422,
      "detail": "A avaliação deve ser de um álbum ou de uma música, nunca dos dois",
      "instance": "/avaliacoes"
    }
    ```

## Regras de remoção

Não dá para remover um registro que tem dependentes. Nesses casos a API retorna **409** com uma mensagem clara, em vez de deixar a chave estrangeira estourar no banco:

| Remover | Bloqueado quando |
| --- | --- |
| Artista | tem álbuns |
| Álbum | tem músicas ou avaliações |
| Música | tem avaliações |
| Usuário | tem avaliações |
| Gênero | é usado por algum álbum |

!!! note "Rede de segurança"
    Os services checam duplicidade antes de gravar. Para e-mail, nome de gênero e número de faixa, o banco também tem constraint única: se duas requisições chegarem ao mesmo tempo (por exemplo, em instâncias diferentes atrás do load balancer) e as duas passarem pela checagem, o banco barra a segunda, e o `ApiExceptionHandler` também a transforma em **409**.
