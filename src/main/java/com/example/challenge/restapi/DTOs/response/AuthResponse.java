package com.example.challenge.restapi.DTOs.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de autenticação com o token JWT")
public record AuthResponse(
        @Schema(description = "Token de acesso JWT")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tipo,

        @Schema(description = "Tempo de expiração em milissegundos", example = "3600000")
        long expiresIn,

        @Schema(description = "E-mail do usuário autenticado")
        String email,

        @Schema(description = "Perfil de acesso do usuário", example = "ADMIN")
        String perfil
) {
    public static AuthResponse of(String token, long expiresIn, String email, String perfil) {
        return new AuthResponse(token, "Bearer", expiresIn, email, perfil);
    }
}
