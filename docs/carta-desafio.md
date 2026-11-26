# Carta-desafio: relatório por período

!!! abstract "Carta 8 · Relatório agregado por período · 40%"
    Issue [#19](https://github.com/gabrieldorodrigues/apirest-2026/issues/19) · PR [#20](https://github.com/gabrieldorodrigues/apirest-2026/pull/20)

## História de usuário

Como curador da plataforma de avaliação musical, quero obter totais e contagens das avaliações feitas em um período. Assim acompanho o que os usuários estão ouvindo e avaliando: quantas avaliações foram feitas, qual a nota média e quais gêneros, álbuns e músicas mais movimentaram a plataforma.

## Endpoint

```http
GET /relatorios/avaliacoes?inicio=2026-09-01&fim=2026-11-30
```

| Parâmetro | Obrigatório | Formato | O que é |
| --- | --- | --- | --- |
| `inicio` | Sim | `AAAA-MM-DD` | Primeiro dia do período. Entra no período |
| `fim` | Sim | `AAAA-MM-DD` | Último dia do período. Entra no período, até 23:59:59 |

O período usa a data de criação da avaliação (`criadaEm`). Uma avaliação editada depois continua no período em que foi criada.

## O que o relatório traz

| Campo | O que é |
| --- | --- |
| `inicio`, `fim` | O período consultado |
| `totalAvaliacoes` | Avaliações criadas no período |
| `avaliacoesDeAlbuns`, `avaliacoesDeMusicas` | O total separado por tipo de item avaliado |
| `notaMedia` | Média das notas do período, com 2 casas decimais. Fica `null` quando não há avaliações |
| `usuariosQueAvaliaram` | Usuários diferentes que avaliaram no período |
| `distribuicaoNotas` | Quantidade de avaliações para cada nota possível, de 0,5 a 5. Sempre traz as 10 notas, mesmo as que ninguém deu |
| `porGenero` | Avaliações e nota média por gênero, da maior quantidade para a menor. Empate vai pelo nome |
| `albunsMaisAvaliados` | Até 5 álbuns, com artista, quantidade e nota média do período |
| `musicasMaisAvaliadas` | Até 5 músicas, com álbum, artista, quantidade e nota média do período |

Nos dois rankings, o item com mais avaliações vem primeiro. Empate vai pela maior nota média e depois pelo título.

Regras de contagem por gênero:

- A avaliação de uma música conta para os gêneros do álbum da música, porque a música não tem gênero próprio no domínio.
- Um álbum com dois gêneros conta nos dois. Por isso a soma de `porGenero` pode passar do `totalAvaliacoes`.

## Exemplo

O seed grava as 9 avaliações entre setembro e novembro de 2026: 2 de álbuns em setembro, 3 de álbuns em outubro e 4 de músicas em novembro. Assim, períodos diferentes dão relatórios diferentes.

=== "Setembro a novembro"

    `GET /relatorios/avaliacoes?inicio=2026-09-01&fim=2026-11-30` → **200**

    ```json
    {
      "inicio": "2026-09-01",
      "fim": "2026-11-30",
      "totalAvaliacoes": 9,
      "avaliacoesDeAlbuns": 5,
      "avaliacoesDeMusicas": 4,
      "notaMedia": 4.44,
      "usuariosQueAvaliaram": 3,
      "distribuicaoNotas": [
        { "nota": 0.5, "quantidade": 0 },
        { "nota": 1.0, "quantidade": 0 },
        { "nota": 1.5, "quantidade": 0 },
        { "nota": 2.0, "quantidade": 0 },
        { "nota": 2.5, "quantidade": 0 },
        { "nota": 3.0, "quantidade": 0 },
        { "nota": 3.5, "quantidade": 1 },
        { "nota": 4.0, "quantidade": 2 },
        { "nota": 4.5, "quantidade": 3 },
        { "nota": 5.0, "quantidade": 3 }
      ],
      "porGenero": [
        { "id": 2, "nome": "Alternativo", "avaliacoes": 5, "notaMedia": 4.6 },
        { "id": 1, "nome": "Rock", "avaliacoes": 5, "notaMedia": 4.6 },
        { "id": 4, "nome": "Jazz", "avaliacoes": 2, "notaMedia": 4.0 },
        { "id": 3, "nome": "MPB", "avaliacoes": 2, "notaMedia": 4.5 },
        { "id": 5, "nome": "Tropicália", "avaliacoes": 2, "notaMedia": 4.5 }
      ],
      "albunsMaisAvaliados": [
        { "id": 1, "titulo": "OK Computer", "artista": "Radiohead", "avaliacoes": 2, "notaMedia": 4.75 },
        { "id": 3, "titulo": "Caetano Veloso", "artista": "Caetano Veloso", "avaliacoes": 1, "notaMedia": 5.0 },
        { "id": 4, "titulo": "Kind of Blue", "artista": "Miles Davis", "avaliacoes": 1, "notaMedia": 4.5 },
        { "id": 2, "titulo": "In Rainbows", "artista": "Radiohead", "avaliacoes": 1, "notaMedia": 4.0 }
      ],
      "musicasMaisAvaliadas": [
        { "id": 1, "titulo": "Paranoid Android", "album": "OK Computer", "artista": "Radiohead", "avaliacoes": 1, "notaMedia": 5.0 },
        { "id": 4, "titulo": "Reckoner", "album": "In Rainbows", "artista": "Radiohead", "avaliacoes": 1, "notaMedia": 4.5 },
        { "id": 6, "titulo": "Tropicália", "album": "Caetano Veloso", "artista": "Caetano Veloso", "avaliacoes": 1, "notaMedia": 4.0 },
        { "id": 8, "titulo": "So What", "album": "Kind of Blue", "artista": "Miles Davis", "avaliacoes": 1, "notaMedia": 3.5 }
      ]
    }
    ```

=== "Só outubro"

    `GET /relatorios/avaliacoes?inicio=2026-10-01&fim=2026-10-31` → **200**

    ```json
    {
      "inicio": "2026-10-01",
      "fim": "2026-10-31",
      "totalAvaliacoes": 3,
      "avaliacoesDeAlbuns": 3,
      "avaliacoesDeMusicas": 0,
      "notaMedia": 4.5,
      "usuariosQueAvaliaram": 3,
      "distribuicaoNotas": [
        { "nota": 0.5, "quantidade": 0 },
        { "nota": 1.0, "quantidade": 0 },
        { "nota": 1.5, "quantidade": 0 },
        { "nota": 2.0, "quantidade": 0 },
        { "nota": 2.5, "quantidade": 0 },
        { "nota": 3.0, "quantidade": 0 },
        { "nota": 3.5, "quantidade": 0 },
        { "nota": 4.0, "quantidade": 1 },
        { "nota": 4.5, "quantidade": 1 },
        { "nota": 5.0, "quantidade": 1 }
      ],
      "porGenero": [
        { "id": 2, "nome": "Alternativo", "avaliacoes": 1, "notaMedia": 4.0 },
        { "id": 4, "nome": "Jazz", "avaliacoes": 1, "notaMedia": 4.5 },
        { "id": 3, "nome": "MPB", "avaliacoes": 1, "notaMedia": 5.0 },
        { "id": 1, "nome": "Rock", "avaliacoes": 1, "notaMedia": 4.0 },
        { "id": 5, "nome": "Tropicália", "avaliacoes": 1, "notaMedia": 5.0 }
      ],
      "albunsMaisAvaliados": [
        { "id": 3, "titulo": "Caetano Veloso", "artista": "Caetano Veloso", "avaliacoes": 1, "notaMedia": 5.0 },
        { "id": 4, "titulo": "Kind of Blue", "artista": "Miles Davis", "avaliacoes": 1, "notaMedia": 4.5 },
        { "id": 2, "titulo": "In Rainbows", "artista": "Radiohead", "avaliacoes": 1, "notaMedia": 4.0 }
      ],
      "musicasMaisAvaliadas": []
    }
    ```

=== "Período sem avaliações"

    `GET /relatorios/avaliacoes?inicio=2020-01-01&fim=2020-01-31` → **200**

    As contagens vêm zeradas, a `notaMedia` vem `null`, a `distribuicaoNotas` traz as 10 notas com quantidade 0 e as outras listas vêm vazias. Um período sem avaliações é uma resposta válida, não um erro.

## Erros

Os erros seguem o formato Problem Details, como no resto da API (veja [Validação e erros](api/erros.md)).

| Código | Título | Quando acontece | `detail` |
| --- | --- | --- | --- |
| **400** | Parâmetro obrigatório | Falta `inicio` ou `fim` | `O parâmetro fim é obrigatório` |
| **400** | Parâmetro inválido | Data fora do formato `AAAA-MM-DD` ou inexistente, como `2026-13-01` | `O valor '2026-13-01' não é válido para o parâmetro inicio` |
| **422** | Regra de negócio violada | `inicio` depois de `fim` | `A data inicial (2026-12-31) é depois da data final (2026-01-01)` |
| **422** | Regra de negócio violada | Período maior que 1 ano | `O período do relatório pode ter no máximo 1 ano` |

O limite de 1 ano mantém a consulta rápida e o relatório legível. Um período de exatamente 1 ano, como de `2025-11-30` a `2026-11-30`, é aceito.

## Implementação

O relatório só lê as entidades que já existem: `Avaliacao`, `Album`, `Musica`, `Genero`, `Artista` e `Usuario`. Nenhuma entidade nova foi criada para ele.

| Arquivo | Papel |
| --- | --- |
| `controller/RelatorioController.java` | Recebe `inicio` e `fim` como datas ISO e documenta o endpoint no Swagger, na seção **Relatórios** |
| `service/RelatorioService.java` | Valida o período, transforma as datas no intervalo de horários e monta o relatório. Completa a distribuição com as notas que não apareceram |
| `repository/AvaliacaoRepository.java` | As 5 consultas agregadas do relatório |
| `repository/ResumoPeriodo.java` | Os totais do período, que vêm de uma consulta só |
| `dto/RelatorioAvaliacoesDto.java` e os DTOs `NotaQuantidadeDto`, `GeneroRelatorioDto`, `AlbumRelatorioDto` e `MusicaRelatorioDto` | O formato da resposta. Arredondam a nota média para 2 casas, como a `notaMedia` de álbuns e músicas |

### Consulta sem N+1

O banco faz todas as contas, com `count`, `sum`, `avg` e `group by`. As consultas devolvem os números prontos, sem carregar as avaliações para a memória. Cada relatório faz sempre 5 consultas:

| Consulta | O que calcula |
| --- | --- |
| Resumo | Total, avaliações de álbuns e de músicas, nota média e usuários, numa consulta só |
| Distribuição | Quantidade de avaliações por nota |
| Por gênero | Avaliações e média por gênero, ligando a avaliação de música ao álbum da música |
| Álbuns mais avaliados | Top 5 álbuns, com o artista na mesma consulta |
| Músicas mais avaliadas | Top 5 músicas, com o álbum e o artista na mesma consulta |

O teste `numeroFixoDeConsultas` conta os comandos SQL com as estatísticas do Hibernate. O relatório faz 5 consultas com as 9 avaliações do seed e continua fazendo 5 depois de mais 20 avaliações no período.

### Mudanças no código que já existia

| Mudança | Por quê |
| --- | --- |
| A `Avaliacao` só preenche `criadaEm` na hora de salvar quando a data ainda está vazia | O seed precisa gravar datas no passado. Pela API, a data continua sendo a do momento da criação, porque o DTO de entrada não tem esse campo |
| O seed grava cada avaliação com uma data entre setembro e novembro de 2026, em ordem | O relatório fica demonstrável com períodos diferentes. A ordem das datas é a mesma da criação, então a ordenação padrão das listagens não muda |
| O `ApiExceptionHandler` trata parâmetro obrigatório ausente | Antes, um `@RequestParam` obrigatório ausente caía no erro interno (500). Agora volta 400 com o nome do parâmetro |

> **Nota:** o seed só roda com o banco vazio. Um banco criado antes desta mudança mantém as avaliações com a data antiga. Para gravar as datas novas, apague o banco: `docker compose down -v` no Docker, ou a pasta `data/` com o H2 local.

## Como testar

1. Suba a API com o banco vazio (veja [Como executar](executar/index.md)).
2. Abra `GET /relatorios/avaliacoes?inicio=2026-09-01&fim=2026-11-30` e confira o total de 9 avaliações.
3. Troque o período para outubro (`2026-10-01` a `2026-10-31`) e confira o total de 3 avaliações, todas de álbuns.
4. Troque as datas de lugar (`inicio=2026-12-31&fim=2026-01-01`) e confira o 422.
5. Use `inicio=2026-13-01` e confira o 400.
6. Crie uma avaliação com `POST /avaliacoes` e consulte o relatório do dia de hoje: o total aumenta em 1.

Os mesmos passos funcionam pela [Swagger UI](/apirest-2026/swagger/), na seção **Relatórios**, e pela collection do Postman, na pasta **05 - Relatório por período**.

## Testes

| Classe | Testes | O que cobre |
| --- | --- | --- |
| `RelatorioControllerTest` | 9 | Relatório completo do seed; outubro e novembro com resultados diferentes; datas inclusivas; período vazio; avaliação nova entrando no relatório; 422 para início depois do fim e para período maior que 1 ano; 400 para data ausente e inválida; número fixo de consultas |
| `PostgresIntegracaoTest` | 1 | O mesmo relatório contra o PostgreSQL 17, para conferir as consultas agregadas no outro banco |
