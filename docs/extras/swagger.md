# Swagger/OpenAPI

!!! abstract "Requisito extra · 5%"
    Issue [#1](https://github.com/gabrieldorodrigues/apirest-2026/issues/1) · PR [#7](https://github.com/gabrieldorodrigues/apirest-2026/pull/7)

A documentação interativa da API é gerada automaticamente pelo **springdoc-openapi 3.1.1** (compatível com Spring Boot 4.1) a partir dos controllers e DTOs.

| Endereço | O que é |
| --- | --- |
| `http://localhost:8080/swagger-ui.html` | Swagger UI: lista todos os endpoints e permite testá-los pelo navegador (**Try it out**) |
| `http://localhost:8080/v3/api-docs` | Especificação OpenAPI 3 em JSON, que pode ser importada no Postman ou em outras ferramentas |

## O que está documentado

- **Seções na ordem do domínio:** Artistas, Gêneros, Álbuns, Músicas, Usuários e Avaliações (`@Tag`);
- **Cada operação** tem resumo e descrição com as regras de negócio (`@Operation`);
- **Respostas possíveis de cada endpoint** (`@ApiResponse`): 200/201/204 e os erros 400, 404, 409 e 422 que aquele endpoint pode retornar. Os erros mostram o corpo **Problem Details** (`application/problem+json`) com um exemplo do próprio caso;
- **Filtros, paginação e ordenação** aparecem como parâmetros, com descrição e exemplo (`@Parameter` e `@ParameterObject`);
- **DTOs de entrada com exemplos do seed** (`@Schema`): o **Try it out** já abre com um corpo válido;
- **Validações** (`@NotNull`, `@Size`, `@DecimalMin`...) aparecem no schema como campos obrigatórios, mínimos e máximos.

Exemplo de um endpoint documentado:

```java
@Operation(summary = "Avalia um álbum ou uma música", description = REGRAS)
@ApiResponse(responseCode = "201", description = "Avaliação criada")
@ApiResponse(responseCode = "400", description = "Campos inválidos, nota fora de 0,5 a 5 ou JSON mal formatado")
@ApiResponse(responseCode = "404", description = "Usuário, álbum ou música não existe")
@ApiResponse(responseCode = "409", description = "Usuário já avaliou este álbum ou esta música")
@ApiResponse(responseCode = "422", description = "Álbum e música ao mesmo tempo (ou nenhum dos dois), ou nota fora do passo de 0,5")
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public AvaliacaoResponseDto criar(@RequestBody @Valid AvaliacaoRequestDto dto) {
    return avaliacaoService.criar(dto);
}
```

## Configuração

- `OpenApiConfig`: título, descrição e versão da API, a ordem das seções e um `OpenApiCustomizer` que coloca o schema de erro (`Problema`) e um exemplo em cada resposta 4xx/5xx;
- `application.properties`:

```properties
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.try-it-out-enabled=true
springdoc.default-produces-media-type=application/json
# cada endpoint mostra só os erros que retorna, sem repetir todos os do ApiExceptionHandler
springdoc.override-with-generic-response=false
```

## Como testar

1. Rode a API (qualquer forma de [Como executar](../executar/index.md)).
2. Abra `http://localhost:8080/swagger-ui.html`.
3. Abra `POST /avaliacoes` → **Try it out** → **Execute**: a primeira execução cria a avaliação (201), e a segunda retorna 409.

## Evidências

![Swagger UI com os endpoints agrupados por recurso](../evidencias/15-swagger-ui.png){ .evidencia }

![GET /artistas/1 executado pelo Try it out](../evidencias/16-swagger-try-it-out.png){ .evidencia }
