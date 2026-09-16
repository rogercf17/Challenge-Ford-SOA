package com.example.challenge.restapi.DTOs.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação")
public record LoginRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Schema(description = "E-mail do usuário", example = "admin@fordchallenge.com")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Schema(description = "Senha do usuário", example = "admin123")
        String senha
) { }