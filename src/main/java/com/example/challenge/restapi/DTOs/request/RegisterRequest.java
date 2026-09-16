package com.example.challenge.restapi.DTOs.request;

import com.example.challenge.restapi.model.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro de um novo usuário")
public record RegisterRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        @Schema(description = "Nome completo do usuário", example = "Maria Silva")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Schema(description = "E-mail do usuário", example = "maria.silva@fordchallenge.com")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
        @Schema(description = "Senha do usuário", example = "senhaForte123")
        String senha,

        @NotNull(message = "Perfil é obrigatório")
        @Schema(description = "Perfil de acesso do usuário", example = "USER")
        Perfil perfil
) { }
