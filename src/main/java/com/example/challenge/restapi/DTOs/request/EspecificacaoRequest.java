package com.example.challenge.restapi.DTOs.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Especificação técnica (atributo-valor) de um veículo")
public record EspecificacaoRequest(
        @NotBlank(message = "Atributo é obrigatório")
        @Size(max = 150, message = "Atributo deve ter no máximo 150 caracteres")
        @Schema(description = "Nome do atributo técnico", example = "Motor")
        String atributo,

        @Size(max = 500, message = "Valor deve ter no máximo 500 caracteres")
        @Schema(description = "Valor do atributo (null = não disponível)", example = "2.8 Diesel Turbo")
        String valor
) { }
