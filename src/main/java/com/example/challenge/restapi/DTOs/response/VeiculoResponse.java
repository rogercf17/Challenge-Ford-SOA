package com.example.challenge.restapi.DTOs.response;

import com.example.challenge.restapi.model.Veiculo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Dados do veículo retornados pela API")
public record VeiculoResponse(
        @Schema(description = "ID do veículo", example = "1")
        Long id,

        @Schema(description = "Marca", example = "Ford")
        String marca,

        @Schema(description = "Modelo", example = "Ranger")
        String modelo,

        @Schema(description = "Versão", example = "Raptor")
        String versao,

        @Schema(description = "Ano do modelo", example = "2024")
        Integer ano,

        @Schema(description = "Especificações técnicas padronizadas")
        List<EspecificacaoResponse> especificacoes,

        @Schema(description = "Data de criação do registro")
        LocalDateTime createdAt,

        @Schema(description = "Data da última atualização")
        LocalDateTime updatedAt
) {
    public static VeiculoResponse from(Veiculo v) {
        return new VeiculoResponse(
                v.getId(),
                v.getMarca(),
                v.getModelo(),
                v.getVersao(),
                v.getAno(),
                v.getEspecificacoes().stream()
                        .map(EspecificacaoResponse::from)
                        .toList(),
                v.getCreatedAt(),
                v.getUpdatedAt()
        );
    }
}
