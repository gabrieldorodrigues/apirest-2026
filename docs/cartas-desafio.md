---
title: "Cartas-Desafio - Projeto Final"
description: "Catálogo de histórias de usuário para a etapa de carta-desafio do projeto final da disciplina de Backend 2026.2."
draft: true
---

# Cartas-Desafio - Projeto Final

Esta página reúne as cartas-desafio do projeto final. Cada equipe recebe uma carta sorteada em 19/11 e deve implementá-la como uma alteração na API construída na etapa base.

A carta vale 40% da nota do projeto. Ela deve ser implementada após a entrega da API base, entre 26/11 e 03/12, e demonstrada na apresentação de 03/12.

> Consulte a página do Projeto Final - API REST para os requisitos da API base, os requisitos extras, o cronograma e a rubrica completa.

## Como funciona

1. A equipe sorteia uma carta em 19/11.
2. A equipe abre uma issue com a história de usuário e os critérios de aceite.
3. A implementação é feita em uma branch própria.
4. A equipe abre um pull request descrevendo a mudança, as decisões, as evidências e como testar.
5. O PR é integrado à API existente.
6. A carta é documentada no README.md e demonstrada na apresentação.

A carta deve ser uma alteração na API existente. Não pode ser um projeto separado nem uma entidade isolada, sem relação com o domínio principal.

> Entidades de apoio são permitidas quando necessárias para a carta (ex.: auditoria, job, arquivo), desde que relacionadas ao domínio e justificadas no PR. O que não vale é criar um CRUD isolado sem relação com o projeto.

## Requisitos Extras (20%)

Os requisitos extras são opcionais. A equipe escolhe quais implementar, com pesos fixos e limite de 20% na nota final. Extras não substituem a carta-desafio.

| Requisito extra | Peso |
|---|---|
| Autenticação e autorização com JWT, roles e ownership | 10% |
| Processamento assíncrono/filas | 10% |
| Deploy em produção | 10% |
| WebSockets/tempo real | 10% |
| Load balancer/múltiplas instâncias | 10% |
| Observabilidade: health check, Actuator, métricas e logs | 5% |
| Docker/Compose | 5% |
| Swagger/OpenAPI | 5% |
| Testes automatizados | 5% |
| Cache HTTP/ETag ou cache de aplicação | 5% |
| Rate limiting | 5% |
| SGBD diferente do H2 | 5% |
| Integração com API externa | 5% |
| CI/CD | 5% |
| Versionamento de API | 5% |
| HATEOAS | 5% |

Regras dos extras:

- O aluno escolhe quais extras implementar; não é necessário implementar todos;
- Cada extra deve ser funcional, documentado, evidenciado e explicado na apresentação;
- Implementação parcial vale proporcionalmente;
- Extras devem estar integrados ao projeto e ao domínio; não valem funcionalidades isoladas;
- O limite de pontuação é 20%, mesmo que a equipe implemente mais de 20% em extras.

## Template de issue e PR

Use a estrutura abaixo na issue e no pull request da carta:

```markdown
## História de usuário

Como [papel], quero [funcionalidade] para [benefício].

## Critérios de aceite

- [ ] Critério 1
- [ ] Critério 2
- [ ] Critério 3

## Decisões de projeto

- Como a carta foi integrada à API existente
- Entidades, endpoints e regras alterados
- Estratégia de tratamento de erros

## Como testar

- Passos para reproduzir
- Dados de exemplo
- Endpoints e parâmetros

## Evidências

- Prints de requisições e respostas
- Vídeo curto, se houver
```

## Definição de pronto

A carta será considerada concluída quando:

- Os critérios de aceite forem atendidos;
- O código estiver integrado à API existente;
- Erros e casos de borda estiverem tratados;
- O README.md descrever a história, a implementação e os exemplos de uso;
- As evidências de execução estiverem disponíveis;
- O pull request estiver mergeado com descrição;
- A carta for demonstrada e explicada na apresentação.

## Catálogo

### 1. Acesso por perfil e propriedade

**História de usuário:** Como cliente, quero que apenas o dono possa consultar e alterar seus registros, para proteger meus dados.

**O que implementar:** autenticação de usuários, roles (USER e ADMIN), hash de senha e verificação de propriedade dos recursos.

**Critérios de aceite:**

- Cadastro e login com senha armazenada com hash seguro (BCrypt ou Argon2);
- Endpoints protegidos retornam 401 sem credencial válida;
- Usuário autenticado sem permissão recebe 403;
- Usuário comum acessa e altera apenas seus próprios registros; administrador acessa todos;
- Respostas não expõem senha, hash ou dados sensíveis.

**Integração com o domínio:** associar os registros existentes ao usuário autenticado. Não criar uma entidade isolada apenas para o login.

### 2. Auditoria de alterações

**História de usuário:** Como auditor, quero saber quem criou, alterou ou removeu registros e quando, para acompanhar mudanças no sistema.

**O que implementar:** registro de auditoria para operações de escrita, com consulta protegida.

**Critérios de aceite:**

- Registrar usuário, data/hora, operação, entidade e identificador do registro;
- Registrar criação, atualização e remoção;
- Disponibilizar consulta de auditoria com paginação e filtros;
- Restringir a consulta a usuários autorizados;
- Não registrar senhas, tokens ou outros dados sensíveis.

**Integração com o domínio:** a auditoria deve observar as operações das entidades existentes. Entidade de apoio permitida, desde que relacionada ao domínio.

### 3. Idempotência em operações críticas

**História de usuário:** Como cliente, quero repetir uma requisição de criação sem gerar registros duplicados, para evitar problemas em caso de timeout ou reenvio.

**O que implementar:** suporte ao cabeçalho `Idempotency-Key` em uma operação crítica de criação.

**Critérios de aceite:**

- A mesma chave com o mesmo payload retorna o mesmo resultado, sem duplicar o registro;
- A mesma chave com payload diferente retorna 409 ou 422;
- A chave possui tempo de expiração definido;
- A estratégia está documentada no README.md;
- Há evidência de teste com repetição da requisição.

**Integração com o domínio:** aplicar a uma operação relevante, como criação de pedido, reserva, inscrição ou cadastro.

### 4. Configuração segura e gestão de segredos

**História de usuário:** Como responsável pela operação, quero que segredos não fiquem no código e que a API suba com configuração segura.

**O que implementar:** variáveis de ambiente, perfis de configuração, CORS restrito, headers de segurança e erros sem detalhes internos.

**Critérios de aceite:**

- Nenhum segredo ou senha está versionado no Git;
- A aplicação falha ao iniciar se uma variável obrigatória não existir;
- O CORS está restrito às origens necessárias;
- Erros não expõem stacktrace, versões ou detalhes internos;
- Actuator e console do H2 estão protegidos ou desabilitados quando não forem necessários.

**Integração com o domínio:** aplicar ao projeto existente e documentar as variáveis de ambiente necessárias.

### 5. Cache de consultas frequentes

**História de usuário:** Como cliente, quero respostas rápidas para consultas frequentes de dados que mudam pouco.

**O que implementar:** cache HTTP com ETag/Cache-Control ou cache de aplicação com Spring Cache/Redis.

**Critérios de aceite:**

- O cache possui TTL definido;
- As entradas são invalidadas ou renovadas quando os dados mudam;
- Há evidência de ganho de desempenho ou teste comparativo;
- Dados sensíveis não são cacheados sem autorização;
- A estratégia está documentada no README.md.

**Integração com o domínio:** escolher um recurso de leitura frequente e documentar a chave, o TTL e a invalidação.

### 6. Limite de requisições por cliente

**História de usuário:** Como operador, quero limitar requisições por cliente para proteger a API de abuso e picos de tráfego.

**O que implementar:** rate limiting por IP, usuário ou chave de API, com limite configurável.

**Critérios de aceite:**

- O limite é configurável por minuto e/ou por hora;
- Requisições acima do limite recebem 429 Too Many Requests;
- A resposta inclui `Retry-After`;
- O limite é aplicado a endpoints críticos;
- A estratégia está documentada e evidenciada.

**Integração com o domínio:** aplicar aos endpoints existentes. Não criar um endpoint isolado apenas para demonstrar o limite.

### 7. Processamento assíncrono de tarefas pesadas

**História de usuário:** Como usuário, quero solicitar tarefas pesadas sem esperar a resposta terminar, para continuar usando a API.

**O que implementar:** processamento em segundo plano com fila/worker ou `@Async`, com endpoint de acompanhamento do job.

**Critérios de aceite:**

- A requisição inicial retorna 202 Accepted com identificador do job;
- Existe endpoint para consultar o status (processando, concluído, falhou);
- O processamento ocorre fora da requisição principal;
- Falhas são registradas e podem ser consultadas;
- O fluxo está documentado e evidenciado.

**Integração com o domínio:** usar uma operação real, como importação, geração de relatório ou envio de notificações.

### 8. Relatório agregado por período

**História de usuário:** Como gestor, quero obter totais e contagens de registros em um período, para acompanhar o movimento do domínio.

**O que implementar:** endpoint de relatório com filtro por data inicial e final e agregação de dados.

**Critérios de aceite:**

- O filtro de período é validado e retorna erro para datas inválidas;
- O relatório retorna a contagem de registros e o total ou métrica adequada ao domínio;
- A consulta é eficiente e evita N+1;
- O endpoint possui exemplos documentados;
- O relatório é demonstrável com dados reais.

**Integração com o domínio:** usar as entidades e os relacionamentos existentes, sem criar uma entidade isolada para o relatório.

### 9. Exportação e importação CSV

**História de usuário:** Como gestor, quero exportar dados e importar lotes em CSV, para integrar a API com planilhas e outros sistemas.

**O que implementar:** exportação para download e importação de arquivo CSV com validação.

**Critérios de aceite:**

- A exportação retorna um arquivo CSV válido;
- A importação valida colunas, tipos e obrigatoriedade;
- Linhas inválidas são rejeitadas com relatório de erros;
- A importação respeita limite de tamanho e tempo;
- Se o processamento for assíncrono, retorna 202 e permite consultar o status.

**Integração com o domínio:** usar a entidade principal do projeto. Não criar uma entidade isolada apenas para a importação.

### 10. Upload de arquivos

**História de usuário:** Como usuário, quero anexar arquivos a registros do domínio, para complementar as informações.

**O que implementar:** upload multipart, validação de tipo/tamanho, armazenamento seguro e associação a um registro.

**Critérios de aceite:**

- O upload valida tipo e tamanho do arquivo;
- O nome do arquivo é tratado para evitar path traversal;
- O arquivo é associado a um registro existente;
- Existe endpoint para download e remoção;
- O acesso ao arquivo respeita as regras de autorização do domínio.

**Integração com o domínio:** associar o arquivo a uma entidade existente, como foto de produto, documento de pedido ou anexo de ocorrência.

### 11. Notificações por email

**História de usuário:** Como usuário, quero ser notificado quando eventos relevantes ocorrerem, para acompanhar o que acontece no sistema.

**O que implementar:** envio de emails a partir de eventos de criação ou atualização, com configuração SMTP e preferências.

**Critérios de aceite:**

- A configuração SMTP usa variáveis de ambiente;
- O envio é assíncrono e tolerante a falhas;
- Existe template de email com informações do evento;
- O usuário pode optar por receber ou não notificações;
- Segredos e dados sensíveis não são registrados em log.

**Integração com o domínio:** disparar a notificação a partir de operações existentes. Não criar uma entidade isolada apenas para o envio.

### 12. Regras temporais

**História de usuário:** Como negócio, quero permitir certas operações apenas em dias ou horários definidos, para respeitar as regras de funcionamento.

**O que implementar:** validação de janela temporal configurável para uma operação do domínio.

**Critérios de aceite:**

- A operação é rejeitada fora da janela permitida com 409 ou 422;
- A mensagem de erro explica o motivo da rejeição;
- A regra é configurável por dia da semana e horário;
- Há testes ou evidências cobrindo dentro e fora da janela;
- A regra está documentada no README.md.

**Integração com o domínio:** aplicar a uma operação existente, como reserva, check-in, pedido ou agendamento.

## Avaliação da carta

A carta vale 40% da nota do projeto e é avaliada pelos seguintes critérios:

| Critério | Peso |
|---|---|
| Entendimento da história e critérios de aceite | 5% |
| Implementação e integração com a API existente | 15% |
| Qualidade, casos de borda e tratamento de erros | 10% |
| Documentação, PR e evidências | 5% |
| Apresentação e explicação no código | 5% |
| **Total** | **40%** |

A equipe deve demonstrar a carta na apresentação, explicar a história de usuário e mostrar diretamente no código como a implementação foi feita.
