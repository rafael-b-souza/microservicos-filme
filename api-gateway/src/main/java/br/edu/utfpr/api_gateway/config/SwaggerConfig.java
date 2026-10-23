package br.edu.utfpr.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.models.GroupedOpenApi;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi avaliacaoApi() {
        return GroupedOpenApi.builder()
                .group("avaliacao")
                .addOpenApiCustomizer(openApi -> openApi.getInfo().setTitle("API de Avaliação"))
                .pathsToMatch("/avaliacoes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi filmeApi() {
        return GroupedOpenApi.builder()
                .group("filme")
                .addOpenApiCustomizer(openApi -> openApi.getInfo().setTitle("API de Filme"))
                .pathsToMatch("/filmes/**")
                .build();
    }

    @Bean
    public GroupedOpenApi usuarioApi() {
        return GroupedOpenApi.builder()
                .group("usuario")
                .addOpenApiCustomizer(openApi -> openApi.getInfo().setTitle("API de Usuário"))
                .pathsToMatch("/usuarios/**")
                .build();
    }

    @Bean
    public GroupedOpenApi recomendacaoApi() {
        return GroupedOpenApi.builder()
                .group("recomendacao")
                .addOpenApiCustomizer(openApi -> openApi.getInfo().setTitle("API de Recomendação"))
                .pathsToMatch("/recomendacoes/**")
                .build();
    }
}
