package com.example.challenge.restapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI fordChallengeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Challenge Ford  — Desafio 01: Inteligência Competitiva Automotiva")
                        .description("""
                                API RESTful para coleta, padronização e comparação de especificações técnicas
                                de veículos concorrentes, desenvolvida como solução para o Desafio 01 do
                                programa Ford & FIAP: Dados na Prática 2026.
                                
                                **Funcionalidades principais:**
                                - Cadastro de veículos (marca / modelo / versão) com atributos livres
                                - Consulta e busca de especificações padronizadas
                                - Comparação técnica side-by-side entre múltiplos veículos
                                - Atributos ausentes explicitados como "Não disponível"
                                
                                **Validação da solução:** utilizar a Ford Ranger Raptor (id=1) pré-carregada via Flyway.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Ford FIAP 2026")
                                .email("equipe@fiap.com.br"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor local"),
                        new Server().url("https://ford-challenge.exemplo.com.br").description("Produção")
                ));
    }
}
