# Entregas

## Cronograma

| Entrega | Data | Conteúdo | Situação |
| --- | --- | --- | --- |
| E1 | 19/11 | Repositório, tema, integrantes, problema, domínio e UML inicial | :material-check-circle:{ style="color: #4caf50" } Entregue |
| E2 | 26/11 | API base com CRUD e requisitos obrigatórios | :material-check-circle:{ style="color: #4caf50" } Entregue |
| E3 | 03/12 | Carta-desafio, requisitos extras, slides e evidências | :material-progress-clock: Em andamento |

## Avaliação

| Bloco | Peso | Onde está |
| --- | --- | --- |
| API base | 40% | [Domínio](dominio.md), [API](api/index.md) e [Evidências](evidencias.md) |
| Carta-desafio | 40% | Carta 8, [relatório por período](carta-desafio.md). Veja também o [catálogo de cartas](cartas-desafio.md) |
| Requisitos extras | 20% | [Requisitos extras](extras/index.md) |

## Mudanças no modelo

O modelo inicial da E1 foi implementado como está no [diagrama](dominio.md#diagrama-de-classes). A única adição na E2 é o campo `notaMedia` nas respostas de álbum e música. Ele é calculado a partir das avaliações e não fica salvo no banco. Por isso a ordenação por nota média não está disponível no `sort`; a listagem de avaliações pode ser ordenada por `nota`.

O enunciado completo está em [Enunciado](enunciado-projeto-final.md).
