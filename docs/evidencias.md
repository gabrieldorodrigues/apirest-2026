# Evidências

Prints da API rodando, tirados no Postman, no navegador, no IntelliJ, no terminal e no console do H2. A collection usada está em [`postman/collections/apirest-2026`](https://github.com/gabrieldorodrigues/apirest-2026/tree/main/postman/collections/apirest-2026).

!!! tip
    Clique com o botão direito numa imagem e abra em uma nova aba para ver no tamanho original.

## API base (E2)

<div class="galeria" markdown>

<figure markdown>
![01](evidencias/01-listar-paginacao-ordenacao.png){ .evidencia loading=lazy }
<figcaption>01 · <code>GET /albuns?page=0&size=2&sort=anoLancamento,desc</code> (200)</figcaption>
</figure>

<figure markdown>
![02](evidencias/02-filtro-artista-genero.png){ .evidencia loading=lazy }
<figcaption>02 · <code>GET /albuns?artistaId=1&generoId=2</code> (200)</figcaption>
</figure>

<figure markdown>
![03](evidencias/03-historico-usuario.png){ .evidencia loading=lazy }
<figcaption>03 · Histórico do usuário: <code>GET /avaliacoes?usuarioId=1</code> (200)</figcaption>
</figure>

<figure markdown>
![04](evidencias/04-criar-album.png){ .evidencia loading=lazy }
<figcaption>04 · <code>POST /albuns</code> com artista e gêneros (201)</figcaption>
</figure>

<figure markdown>
![05](evidencias/05-buscar-album.png){ .evidencia loading=lazy }
<figcaption>05 · <code>GET /albuns/5</code> com nota média (200)</figcaption>
</figure>

<figure markdown>
![06](evidencias/06-atualizar-avaliacao.png){ .evidencia loading=lazy }
<figcaption>06 · <code>PUT /avaliacoes/10</code> (200)</figcaption>
</figure>

<figure markdown>
![07](evidencias/07-remover-artista.png){ .evidencia loading=lazy }
<figcaption>07 · <code>DELETE /artistas/5</code> (204)</figcaption>
</figure>

<figure markdown>
![08](evidencias/08-validacao-400.png){ .evidencia loading=lazy }
<figcaption>08 · Campos inválidos no <code>POST /usuarios</code> (400)</figcaption>
</figure>

<figure markdown>
![09](evidencias/09-nao-encontrado-404.png){ .evidencia loading=lazy }
<figcaption>09 · <code>GET /albuns/999</code> (404)</figcaption>
</figure>

<figure markdown>
![10](evidencias/10-conflito-409.png){ .evidencia loading=lazy }
<figcaption>10 · Usuário avaliando o mesmo álbum duas vezes (409)</figcaption>
</figure>

<figure markdown>
![11](evidencias/11-regra-negocio-422.png){ .evidencia loading=lazy }
<figcaption>11 · Avaliação de álbum e música ao mesmo tempo (422)</figcaption>
</figure>

<figure markdown>
![12](evidencias/12-banco-h2.png){ .evidencia loading=lazy }
<figcaption>12 · Dados salvos no H2, com álbuns, artistas e gêneros</figcaption>
</figure>

<figure markdown>
![13](evidencias/13-run-collection.png){ .evidencia loading=lazy }
<figcaption>13 · Todas as requisições da collection rodadas em sequência</figcaption>
</figure>

</div>

## Testes automatizados

<div class="galeria" markdown>

<figure markdown>
![14](evidencias/14-testes.png){ .evidencia loading=lazy }
<figcaption>14 · Testes automatizados passando no IntelliJ</figcaption>
</figure>

<figure markdown>
![22](evidencias/22-testes-postgres.png){ .evidencia loading=lazy }
<figcaption>22 · <code>PostgresIntegracaoTest</code> passando contra o PostgreSQL do Testcontainers</figcaption>
</figure>

</div>

## Swagger/OpenAPI

<div class="galeria" markdown>

<figure markdown>
![15](evidencias/15-swagger-ui.png){ .evidencia loading=lazy }
<figcaption>15 · Swagger UI com os endpoints agrupados por recurso</figcaption>
</figure>

<figure markdown>
![16](evidencias/16-swagger-try-it-out.png){ .evidencia loading=lazy }
<figcaption>16 · <code>GET /artistas/1</code> pelo Try it out (200)</figcaption>
</figure>

</div>

## Observabilidade

<div class="galeria" markdown>

<figure markdown>
![17](evidencias/17-actuator-health.png){ .evidencia loading=lazy }
<figcaption>17 · <code>/actuator/health</code> com status UP</figcaption>
</figure>

<figure markdown>
![18](evidencias/18-actuator-metricas.png){ .evidencia loading=lazy }
<figcaption>18 · Métrica <code>musica.avaliacoes.criadas</code></figcaption>
</figure>

<figure markdown>
![19](evidencias/19-logs.png){ .evidencia loading=lazy }
<figcaption>19 · <code>logs/musica.log</code> com a API rodando</figcaption>
</figure>

</div>

## PostgreSQL

<div class="galeria" markdown>

<figure markdown>
![20](evidencias/20-postgres-health.png){ .evidencia loading=lazy }
<figcaption>20 · Health check com <code>"database": "PostgreSQL"</code></figcaption>
</figure>

<figure markdown>
![21](evidencias/21-postgres-dados.png){ .evidencia loading=lazy }
<figcaption>21 · Tabelas e álbuns do seed no PostgreSQL</figcaption>
</figure>

</div>

## Docker e load balancer

<div class="galeria" markdown>

<figure markdown>
![23](evidencias/23-docker-compose-ps.png){ .evidencia loading=lazy }
<figcaption>23 · <code>docker compose ps</code> com API e banco (uma instância)</figcaption>
</figure>

<figure markdown>
![24](evidencias/24-lb-compose-ps.png){ .evidencia loading=lazy }
<figcaption>24 · Nginx, PostgreSQL e 3 instâncias da API healthy</figcaption>
</figure>

<figure markdown>
![25](evidencias/25-lb-balanceamento.png){ .evidencia loading=lazy }
<figcaption>25 · Header <code>X-Instancia</code> alternando entre as 3 instâncias</figcaption>
</figure>

<figure markdown>
![26](evidencias/26-lb-failover.png){ .evidencia loading=lazy }
<figcaption>26 · Failover: com a api-2 parada, tudo responde 200</figcaption>
</figure>

</div>
