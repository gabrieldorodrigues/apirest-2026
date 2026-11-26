# Testes automatizados

!!! abstract "Requisito extra · 5%"
    Issue [#5](https://github.com/gabrieldorodrigues/apirest-2026/issues/5) · PR [#6](https://github.com/gabrieldorodrigues/apirest-2026/pull/6)

São **69 testes de integração** com JUnit 5, `@SpringBootTest` e `MockMvc`. Cada teste faz uma requisição HTTP para a API e confere o status, o corpo e o formato Problem Details dos erros. Os testes **não usam mocks**: passam pelo controller, pelo service, pelo repository e pelo banco.

## Como funciona

- O banco é um H2 **em memória** (`jdbc:h2:mem:api-teste`), separado do `./data` usado no dia a dia;
- O `SeedConfig` carrega os mesmos dados de exemplo, então os testes usam os álbuns, músicas e usuários do seed;
- A classe base `ApiTestBase` é `@Transactional`: cada teste é desfeito no final, e um teste não afeta o outro;
- Os testes logam só no console, sem escrever no `logs/musica.log`;
- O `PostgresIntegracaoTest` usa o **Testcontainers** para rodar a API contra um PostgreSQL 17 de verdade.

```java
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:api-teste",
        "logging.file.name="
})
@AutoConfigureMockMvc
@Transactional
public abstract class ApiTestBase {

    @Autowired
    protected MockMvc mockMvc;
}
```

Exemplo de teste de regra de negócio:

```java
@Test
@DisplayName("409 quando o usuário avalia o mesmo álbum duas vezes")
void conflitoAvaliacaoRepetidaDeAlbum() throws Exception {
    postAvaliacao("""
            {"usuarioId": 1, "albumId": 1, "nota": 3.0}
            """)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title").value("Conflito"))
            .andExpect(jsonPath("$.detail").value("Este usuário já avaliou este álbum"));
}
```

## O que é coberto

| Classe | Testes | O que cobre |
| --- | --- | --- |
| `AvaliacaoControllerTest` | 15 | CRUD de avaliação; recálculo da `notaMedia`; 409 por avaliação repetida de álbum e de música; editar a própria avaliação não conta como repetida; 422 para álbum e música ao mesmo tempo, sem nenhum dos dois e nota fora do passo de 0,5; 400 para nota fora de 0,5 a 5 e campos obrigatórios; 404 para usuário inexistente; histórico por `usuarioId`; filtro `notaMinima` com ordenação |
| `AlbumControllerTest` | 10 | CRUD de álbum; `notaMedia` calculada; 409 ao remover álbum com músicas; 404 em Problem Details; 404 para gênero inexistente no corpo; 400 com a lista de campos inválidos; filtro por artista e gênero com paginação e ordenação; 400 para `sort` inválido |
| `CatalogoRegrasTest` | 14 | E-mail salvo em minúsculo e 409 por e-mail repetido; 400 de validação do usuário; faixa repetida no mesmo álbum (409) e permitida em outro álbum; músicas ordenadas pela faixa; 409 ao remover usuário, música, artista e gênero com dependentes; filtro de artistas por país; 400 para id inválido na rota e JSON com tipo errado |
| `ObservabilidadeTest` | 7 | Health, info, header `X-Instancia`, métricas HTTP, da JVM e do domínio; contador de avaliações aumenta ao criar e não muda no 409 |
| `RelatorioControllerTest` | 9 | Relatório por período da [carta-desafio](../carta-desafio.md#testes): totais do seed, períodos diferentes, datas inclusivas, período vazio, 400 e 422, número fixo de consultas |
| `OpenApiDocsTest` | 5 | `/v3/api-docs` descreve a API e os erros; `/swagger-ui.html` abre a Swagger UI; grava a especificação para o site; CORS liberado para o GitHub Pages e recusado para outra origem |
| `PostgresIntegracaoTest` | 8 | Os mesmos cenários principais contra o PostgreSQL 17 do Testcontainers, incluindo o seed sem duplicar e o relatório por período |
| `MusicaApplicationTests` | 1 | A aplicação sobe sem erros |

!!! warning "Docker para os testes do PostgreSQL"
    O `PostgresIntegracaoTest` precisa do Docker rodando. Sem Docker, os 8 testes são **pulados** (`@Testcontainers(disabledWithoutDocker = true)`) em vez de falhar, e o restante da suíte roda normalmente.

## Como rodar

=== "IntelliJ"

    Botão direito em `src/test/java` → **Run 'All Tests'**.

=== "Terminal"

    ```bash
    ./mvnw test
    ```

## Evidências

![Os testes passando no IntelliJ](../evidencias/14-testes.png){ .evidencia }

![PostgresIntegracaoTest passando contra o PostgreSQL do Testcontainers](../evidencias/22-testes-postgres.png){ .evidencia }
