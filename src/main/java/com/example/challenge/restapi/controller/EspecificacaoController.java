package com.example.challenge.restapi.controller;

import com.example.challenge.restapi.DTOs.request.EspecificacaoRequest;
import com.example.challenge.restapi.DTOs.response.EspecificacaoResponse;
import com.example.challenge.restapi.service.EspecificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/veiculos/{veiculoId}/especificacoes")
@RequiredArgsConstructor
@Tag(name = "Especificações", description = "Gestão individual de especificações técnicas de um veículo")
public class EspecificacaoController {
    private final EspecificacaoService service;

    @GetMapping
    @Operation(summary = "Listar especificações",
            description = "Retorna todas as especificações técnicas de um veículo.")
    @ApiResponse(responseCode = "200", description = "Lista retornada")
    public ResponseEntity<List<EspecificacaoResponse>> findAll(
            @Parameter(description = "ID do veículo", example = "1") @PathVariable Long veiculoId) {
        return ResponseEntity.ok(service.findAll(veiculoId));
    }

    @GetMapping("/{especId}")
    @Operation(summary = "Buscar especificação", description = "Retorna uma especificação técnica pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Especificação encontrada"),
            @ApiResponse(responseCode = "404", description = "Não encontrada")
    })
    public ResponseEntity<EspecificacaoResponse> findById(
            @PathVariable Long veiculoId,
            @Parameter(description = "ID da especificação", example = "5") @PathVariable Long especId) {
        return ResponseEntity.ok(service.findById(veiculoId, especId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar / atualizar especificação",
            description = "Adiciona uma nova especificação ao veículo. Se o atributo já existir, atualiza o valor (upsert).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Especificação salva"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Veículo não encontrado")
    })
    public ResponseEntity<EspecificacaoResponse> addOrUpdate(
            @PathVariable Long veiculoId,
            @Valid @RequestBody EspecificacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addOrUpdate(veiculoId, request));
    }

    @DeleteMapping("/{especId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover especificação", description = "Remove uma especificação técnica do veículo.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Não encontrada")
    })
    public ResponseEntity<Void> delete(
            @PathVariable Long veiculoId,
            @PathVariable Long especId) {
        service.delete(veiculoId, especId);
        return ResponseEntity.noContent().build();
    }
}
