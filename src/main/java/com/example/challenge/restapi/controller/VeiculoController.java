package com.example.challenge.restapi.controller;

import com.example.challenge.restapi.DTOs.response.ComparacaoResponse;
import com.example.challenge.restapi.DTOs.response.VeiculoResponse;
import com.example.challenge.restapi.DTOs.request.VeiculoRequest;
import com.example.challenge.restapi.service.VeiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/veiculos")
@RequiredArgsConstructor
@Tag(name = "Veiculos", description = "Gestao de veiculos concorrentes e suas especificacoes tecnicas - Desafio 01 Ford FIAP 2026")
public class VeiculoController {
    private final VeiculoService veiculoService;

    private Pageable safePageable(Pageable pageable) {
        try {
            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();

            if (page < 0 || page > 10_000) page = 0;
            if (size <= 0 || size > 100)   size = 20;

            Sort sort = Sort.by("id");
            boolean sortValido = true;
            for (Sort.Order order : pageable.getSort()) {
                String prop = order.getProperty();
                if (prop.isBlank() || prop.startsWith("[")) {
                    sortValido = false;
                    break;
                }
            }
            if (sortValido && pageable.getSort().isSorted()) {
                sort = pageable.getSort();
            }

            return PageRequest.of(page, size, sort);
        } catch (Exception e) {
            return PageRequest.of(0, 20, Sort.by("id"));
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar veiculo", description = "Registra um novo veiculo concorrente com suas especificacoes tecnicas padronizadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Veiculo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "409", description = "Veiculo ja cadastrado"),
            @ApiResponse(responseCode = "422", description = "Violacao de regra de negocio")
    })
    public ResponseEntity<VeiculoResponse> create(@Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(veiculoService.create(request));
    }

    @GetMapping
    @Operation(summary = "Listar veiculos", description = "Retorna todos os veiculos cadastrados de forma paginada.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<VeiculoResponse>> listAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(veiculoService.listAll(safePageable(pageable)));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar veiculos", description = "Pesquisa veiculos por marca, modelo e/ou versao (busca parcial, case-insensitive).")
    @ApiResponse(responseCode = "200", description = "Resultados encontrados")
    public ResponseEntity<Page<VeiculoResponse>> search(
            @Parameter(description = "Filtro por marca (parcial)") @RequestParam(required = false) String marca,
            @Parameter(description = "Filtro por modelo (parcial)") @RequestParam(required = false) String modelo,
            @Parameter(description = "Filtro por versao (parcial)")  @RequestParam(required = false) String versao,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(veiculoService.search(marca, modelo, versao, safePageable(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar veiculo por ID", description = "Retorna os dados completos e especificacoes de um veiculo pelo ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veiculo encontrado"),
            @ApiResponse(responseCode = "404", description = "Veiculo nao encontrado")
    })
    public ResponseEntity<VeiculoResponse> findById(
            @Parameter(description = "ID do veiculo", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar veiculo", description = "Atualiza dados e especificacoes de um veiculo existente (substituicao completa).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Veiculo atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados invalidos"),
            @ApiResponse(responseCode = "404", description = "Veiculo nao encontrado"),
            @ApiResponse(responseCode = "422", description = "Violacao de regra de negocio")
    })
    public ResponseEntity<VeiculoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody VeiculoRequest request) {
        return ResponseEntity.ok(veiculoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover veiculo", description = "Remove um veiculo e todas as suas especificacoes.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Veiculo removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Veiculo nao encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comparar")
    @Operation(
            summary = "Comparar veiculos",
            description = "Gera uma tabela comparativa padronizada de especificacoes tecnicas entre dois ou mais veiculos. Atributos ausentes sao marcados como 'Nao disponivel'.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comparacao gerada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Menos de 2 IDs informados"),
            @ApiResponse(responseCode = "404", description = "Um ou mais veiculos nao encontrados")
    })
    public ResponseEntity<ComparacaoResponse> comparar(
            @Parameter(description = "IDs dos veiculos a comparar (minimo 2)", example = "1,2")
            @RequestParam List<Long> ids) {
        return ResponseEntity.ok(veiculoService.comparar(ids));
    }
}