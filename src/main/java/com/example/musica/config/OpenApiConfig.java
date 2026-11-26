package com.example.musica.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.tags.Tag;

@Configuration
public class OpenApiConfig {

    private static final String PROBLEM_JSON = "application/problem+json";
    private static final String PROBLEM_SCHEMA = "Problema";

    // mesmos títulos usados pelo ApiExceptionHandler
    private static final Map<String, String> TITULOS = Map.of(
            "400", "Erro de validação",
            "404", "Recurso não encontrado",
            "409", "Conflito",
            "422", "Regra de negócio violada",
            "500", "Erro interno");

    @Bean
    OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Avaliação Musical")
                        .version("1.0.0")
                        .description("""
                                API REST para registrar artistas, álbuns, músicas e gêneros e para que \
                                usuários avaliem álbuns e músicas com nota (0,5 a 5, em passos de 0,5) e resenha.

                                Todas as listagens aceitam `page`, `size` e `sort` (ex.: `sort=anoLancamento,desc`). \
                                Os erros seguem o formato Problem Details (RFC 9457)."""))
                // define a ordem das seções na Swagger UI, do catálogo até as avaliações
                .addTagsItem(new Tag().name("Artistas").description("Pessoas ou bandas que lançam álbuns"))
                .addTagsItem(new Tag().name("Gêneros").description("Classificações musicais dos álbuns"))
                .addTagsItem(new Tag().name("Álbuns").description("Lançamentos de um artista, com nota média calculada"))
                .addTagsItem(new Tag().name("Músicas").description("Faixas de um álbum, com nota média calculada"))
                .addTagsItem(new Tag().name("Usuários").description("Pessoas que avaliam álbuns e músicas"))
                .addTagsItem(new Tag().name("Avaliações").description("Nota e resenha de um usuário sobre um álbum ou uma música"))
                .addTagsItem(new Tag().name("Relatórios").description("Totais e contagens das avaliações de um período"));
    }

    // toda resposta de erro (4xx/5xx) declarada nos controllers passa a mostrar o corpo Problem Details,
    // com um exemplo montado a partir do código e da descrição da própria resposta
    @Bean
    OpenApiCustomizer respostasDeErroComProblemDetails() {
        return openApi -> {
            openApi.getComponents().addSchemas(PROBLEM_SCHEMA, problemaSchema());
            openApi.getPaths().forEach((rota, path) ->
                    path.readOperations().forEach(operation ->
                            operation.getResponses().forEach((codigo, resposta) -> {
                                if (codigo.startsWith("4") || codigo.startsWith("5")) {
                                    resposta.setContent(new Content().addMediaType(PROBLEM_JSON, new MediaType()
                                            .schema(new Schema<>().$ref(PROBLEM_SCHEMA))
                                            .example(exemplo(codigo, resposta.getDescription(), rota))));
                                }
                            })));
        };
    }

    private Map<String, Object> exemplo(String codigo, String descricao, String rota) {
        Map<String, Object> exemplo = new LinkedHashMap<>();
        exemplo.put("title", TITULOS.getOrDefault(codigo, "Erro"));
        exemplo.put("status", Integer.valueOf(codigo));
        exemplo.put("detail", descricao);
        exemplo.put("instance", rota);
        if (codigo.equals("400")) {
            exemplo.put("errors", Map.of("campo", "mensagem de validação"));
        }
        return exemplo;
    }

    private Schema<?> problemaSchema() {
        return new ObjectSchema()
                .description("Erro no formato Problem Details (RFC 9457)")
                .addProperty("type", new StringSchema().example("about:blank"))
                .addProperty("title", new StringSchema().example("Conflito"))
                .addProperty("status", new IntegerSchema().example(409))
                .addProperty("detail", new StringSchema().example("Este usuário já avaliou este álbum"))
                .addProperty("instance", new StringSchema().example("/avaliacoes"))
                .addProperty("errors", new ObjectSchema()
                        .description("Só nos erros 400 de validação: campo -> mensagem")
                        .addProperty("nota", new StringSchema().example("A nota máxima é 5")));
    }
}
