package com.example.challenge.restapi.DTOs.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta de erro padronizada")
public record ErrorResponse(
        @Schema(description = "Momento do erro")
        LocalDateTime timestamp,

        @Schema(description = "Código HTTP", example = "404")
        int status,

        @Schema(description = "Categoria do erro", example = "Not Found")
        String error,

        @Schema(description = "Mensagem principal do erro")
        String message,

        @Schema(description = "Detalhes de validação (quando aplicável)")
        List<String> details
) {
    public static ErrorResponse of(int status, String error, String message,
                           List<String> details) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, details);
    }

    public static ErrorResponse of(int status, String error, String message) {
        return of(status, error, message, List.of());
    }
}
