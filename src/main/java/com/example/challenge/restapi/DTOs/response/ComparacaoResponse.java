package com.example.challenge.restapi.DTOs.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Resultado de comparação técnica entre dois veículos")
public record ComparacaoResponse(
        @Schema(description = "Lista de veículos comparados (marca/modelo/versão)")
        List<String> veiculos,

        @Schema(description = "Tabela comparativa: atributo -> lista d valores por veículo " +
                "(mesma ordem)")
        Map<String, List<String>> tabela
) { }
