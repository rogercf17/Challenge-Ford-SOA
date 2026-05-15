package com.example.challenge.restapi.DTOs.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.List;

@Schema(description = "Dados para cadastro ou atualização de um veículo")
public record VeiculoRequest (
        @NotBlank(message = "Marca é obrigatório")
        @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
        @Schema(description = "Marca do veículo", example = "Toyota")
        String marca,

        @NotBlank(message = "Modelo é obrigatório")
        @Size(max = 100, message = "Modelo deve ter no máximo 100 caracteres")
        @Schema(description = "Modelo do veículo", example = "Hilux")
        String modelo,

        @NotBlank(message = "Versão é obrigatória")
        @Size(max = 100, message = "Versão deve ter no máximo 100 caracteres")
        @Schema(description = "Versão / trim do veículo", example = "GR-S")
        String versao,

        @Min(value = 1900, message = "Ano inválido")
        @Max(value = 2100, message = "Ano inválido")
        @Schema(description = "Ano do modelo", example = "2024")
        Integer ano,

        @Schema(description = "Lista de especificações técnicas do veículo")
        List<EspecificacaoRequest> especificacoes
) { }
