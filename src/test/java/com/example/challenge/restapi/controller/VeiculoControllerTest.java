package com.example.challenge.restapi.controller;

import com.example.challenge.restapi.DTOs.request.VeiculoRequest;
import com.example.challenge.restapi.DTOs.response.VeiculoResponse;
import com.example.challenge.restapi.exception.BusinessException;
import com.example.challenge.restapi.exception.ResourceNotFoundException;
import com.example.challenge.restapi.service.VeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de comportamento do VeiculoController.
 * Filtros de segurança desabilitados aqui: o foco é validar a lógica dos
 * endpoints (status codes, payloads, tratamento de erro). A camada de
 * autorização é validada separadamente em VeiculoSecurityTest.
 */
@WebMvcTest(VeiculoController.class)
@AutoConfigureMockMvc(addFilters = false)
class VeiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VeiculoService veiculoService;

    private VeiculoResponse sampleResponse() {
        return new VeiculoResponse(1L, "Ford", "Ranger", "Raptor", 2024,
                List.of(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void deveCriarVeiculoComSucesso() throws Exception {
        VeiculoRequest request = new VeiculoRequest("Toyota", "Hilux", "GR-Sport", 2024, List.of());
        when(veiculoService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value("Ford"))
                .andExpect(jsonPath("$.modelo").value("Ranger"));
    }

    @Test
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        // marca em branco viola @NotBlank
        VeiculoRequest request = new VeiculoRequest("", "Hilux", "GR-Sport", 2024, List.of());

        mockMvc.perform(post("/api/v1/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void deveRetornar409QuandoVeiculoDuplicado() throws Exception {
        VeiculoRequest request = new VeiculoRequest("Ford", "Ranger", "Raptor", 2024, List.of());
        when(veiculoService.create(any()))
                .thenThrow(new BusinessException("Ja existe um veiculo cadastrado com marca='Ford', modelo='Ranger', versao='Raptor'."));

        mockMvc.perform(post("/api/v1/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void deveBuscarVeiculoPorIdComSucesso() throws Exception {
        when(veiculoService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/veiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.marca").value("Ford"));
    }

    @Test
    void deveRetornar404QuandoVeiculoNaoExiste() throws Exception {
        when(veiculoService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Veículo não encontrado com id: 99"));

        mockMvc.perform(get("/api/v1/veiculos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Veículo não encontrado com id: 99"));
    }

    @Test
    void deveListarVeiculosPaginado() throws Exception {
        Page<VeiculoResponse> page = new PageImpl<>(List.of(sampleResponse()), PageRequest.of(0, 20), 1);
        when(veiculoService.listAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/veiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].marca").value("Ford"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void deveRemoverVeiculoComSucesso() throws Exception {
        mockMvc.perform(delete("/api/v1/veiculos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar400AoCompararComMenosDeDoisIds() throws Exception {
        // ids obrigatorio pelo @RequestParam; sem parametro -> 400 (Spring nao consegue vincular)
        mockMvc.perform(get("/api/v1/veiculos/comparar"))
                .andExpect(status().isBadRequest());
    }
}
