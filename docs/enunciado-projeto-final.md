# Projeto Final - API REST

Neste projeto, vocês deverão desenvolver uma API RESTful completa, estruturada e funcional. O projeto será feito em equipes de 3 a 4 estudantes e simula o desenvolvimento de um serviço backend que atende clientes HTTP, manipulando dados próprios.

A avaliação é dividida em três blocos: API base (40%), carta-desafio (40%) e requisitos extras (20%). A entrega do repositório é uma condição para a apresentação, mas não gera nota por si só: a nota é atribuída na apresentação e na inspeção do código, dos commits, das issues e dos pull requests.

## Objetivos

- Projetar e desenvolver uma API RESTful completa, estruturada e funcional;
- Processar requisições JSON em rotas GET, POST, PUT e DELETE;
- Aplicar arquitetura em camadas, DTOs, validação, tratamento de erros e persistência;
- Implementar paginação, filtros e ordenação nos endpoints de listagem;
- Implementar uma alteração na API a partir de uma história de usuário (carta-desafio);
- Demonstrar o funcionamento da aplicação e o domínio da implementação na apresentação;
- Documentar o projeto e as decisões técnicas no README.md.

## Tema e Aplicação

A equipe deverá escolher um tema e elaborar uma aplicação prática que se encaixe nesse tema. O tema deve ser aplicável a um domínio real e permitir a criação de entidades relacionadas.

### Temas

- Gestão Ambiental
- Logística e Transporte
- Vigilância de Saúde Pública
- Indústria e Manufatura
- Agricultura e Pecuária
- Energia e Sustentabilidade
- Educação e Aprendizado
- Entretenimento e Cultura
- Esportes e Atividades Físicas

## Avaliação

| Bloco             | Peso | O que é avaliado                                                                                             | Data de Entrega |
| ----------------- | ---- | ------------------------------------------------------------------------------------------------------------ | --------------- |
| API base          | 40%  | CRUD, modelagem, camadas, DTOs, validação, erros HTTP, paginação, filtros e ordenação                        | Até 26/11       |
| Carta-desafio     | 40%  | Entendimento da história de usuário, implementação, integração com a API, critérios de aceite e documentação | Até 03/12       |
| Requisitos extras | 20%  | Requisitos adicionais escolhidos pela equipe, com pesos fixos e limite de 20%                                | Até 03/12       |

A nota do grupo é a soma dos três blocos. A nota individual é a nota do grupo ajustada por um fator de participação e domínio demonstrado na apresentação, conforme a rubrica.

## Requisitos Obrigatórios — API Base (60%)

Os requisitos abaixo descrevem o mínimo que a API base deve atender. Não atender a um requisito obrigatório resulta em perda de pontos no bloco correspondente.

- Implementa um serviço backend que atenda clientes HTTP;
- Possui pelo menos três entidades, sendo duas delas relacionadas entre si (1:N ou N:N);
- Implementa o CRUD completo das entidades (getOne, getAll, create, update, delete);
- Realiza a persistência de dados usando um sistema de gerenciamento de banco de dados;
- Está estruturado em camadas (Controller, Service e Repository);
- Utiliza DTOs de entrada e saída para cada entidade;
- Valida os dados de entrada e retorna erros apropriados para dados inválidos;
- Trata exceções e retorna códigos HTTP adequados (200, 201, 204, 400, 404, 409, 422, 500 etc.);
- Implementa paginação no endpoint de listagem (GET ALL);
- Implementa pelo menos um filtro e ordenação em endpoint de listagem (GET ALL);
- Possui README.md com instruções de execução;
- Disponibiliza o repositório de maneira pública no GitHub ou Gitlab, com participação de todos os integrantes no histórico de commits.

## Carta-Desafio (40%)

Cada equipe receberá uma carta-desafio, sorteada em 19/11. A carta é uma história de usuário que descreve que deve ser interpretada e implementada como uma alteração na API original.

> Uma história de usuário (user story) é uma descrição curta e simples de uma funcionalidade do ponto de vista do usuário final. Ela deve conter critérios de aceite que definem quando a história está completa e funcionando corretamente.

- A equipe deve abrir uma issue com a história de usuário e os critérios de aceite;
- A implementação deve ser feita em uma branch e integrada por meio de um pull request;
- A carta deve alterar a API existente; não pode ser um projeto separado nem uma entidade isolada;
- Entidades de apoio são permitidas quando necessárias (ex.: auditoria, job, arquivo), desde que relacionadas ao domínio e justificadas no PR;
- A carta deve ser documentada no README.md e demonstrada na apresentação.

## Cronograma

| Data  | Atividade                                                                     | Entrega                                                           |
| ----- | ----------------------------------------------------------------------------- | ----------------------------------------------------------------- |
| 12/11 | Lançamento do desafio, formação das equipes, definição do tema e da aplicação |                                                                   |
| 19/11 | Definição do domínio, diagrama UML inicial e sorteio das cartas-desafio       | E1: repositório, tema, integrantes e domínio                      |
| 26/11 | API base com CRUD e requisitos obrigatórios                                   | E2: API base, README e evidências                                 |
| 03/12 | Entrega final e apresentação                                                  | E3: API base, implementação da carta desafio, slides e evidências |

## Entregas

- **E1 (19/11):** endereço do repositório público, README.md com tema, integrantes, problema, domínio e UML inicial das entidades.
- **E2 (26/11):** API base com CRUD e requisitos obrigatórios, README.md atualizado e evidências de execução.
- **E3 (03/12):** repositório final com a API base, a carta-desafio integrada por PR, os extras implementados, a documentação completa, os slides e as evidências de execução.

O repositório precisa ser público, acessível e clonável por qualquer pessoa usando git. Espera-se histórico de commits de todos os integrantes e, para a carta-desafio, uma issue e um pull request.

## Documentação

A documentação deve estar no README.md na raiz do projeto, contendo:

- Descrição do projeto, tema, aplicação e integrantes com seus papéis;
- Tecnologias utilizadas;
- Diagrama UML com entidades e relacionamentos;
- Descrição das entidades e dos relacionamentos;
- Descrição dos DTOs de entrada e saída;
- Descrição da validação de dados e exemplos de erros HTTP;
- Descrição da paginação, dos filtros e da ordenação, com exemplos de requisições e respostas;
- Descrição da carta-desafio: história de usuário, critérios de aceite, implementação, endpoints;
- Instruções para executar o projeto localmente, incluindo variáveis de ambiente e banco de dados;

## Apresentação

A apresentação será realizada em sala de aula, em 03/12. Todos os integrantes devem estar presentes.

Cada equipe terá até 10 minutos de apresentação. O roteiro sugerido é:

1. Tema da aplicação e problema;
2. Diagrama UML com entidades e relacionamentos;
3. DTOs de entrada e saída;
4. Validação de dados e tratamento de erros;
5. Paginação, filtros e ordenação;
6. Slide da história de usuário da carta-desafio;
7. Explicação direta no código de como a carta foi implementada;
8. Perguntas e respostas.

Regras da apresentação:

- Cada integrante deve apresentar uma parte e responder pelo menos uma pergunta;

## Ajuste individual

A nota individual pode ser ajustada com base na participação e domínio demonstrado na apresentação. O ajuste é feito pelo professor, com base em observações de:
