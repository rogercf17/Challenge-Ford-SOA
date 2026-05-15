package com.example.challenge.restapi.DTOs.response;

import com.example.challenge.restapi.model.Especificacao;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Especificação técnica retornada pela API")
public record EspecificacaoResponse(
        @Schema(description = "ID da especificação", example = "1")
        Long id,

        @Schema(description = "Nome do atributo técnico", example = "Motor")
        String atributo,

        @Schema(description = "Valor do atributo; 'Não disponível' quando ausente", example = "2.0 Bi-Turbo")
        String valor
) {
    public static EspecificacaoResponse from(Especificacao e) {
        return new EspecificacaoResponse(
                e.getId(),
                e.getAtributo(),
                e.getValor() != null ? e.getValor() : "Não disponível"
        );
    }
}
