package com.example.challenge.restapi.controller;

import com.example.challenge.restapi.DTOs.request.VeiculoRequest;
import com.example.challenge.restapi.DTOs.response.VeiculoResponse;
import com.example.challenge.restapi.config.SecurityConfig;
import com.example.challenge.restapi.repository.UsuarioRepository;
import com.example.challenge.restapi.security.CustomUserDetailsService;
import com.example.challenge.restapi.security.JwtAccessDeniedHandler;
import com.example.challenge.restapi.security.JwtAuthenticationEntryPoint;
import com.example.challenge.restapi.security.JwtAuthenticationFilter;
import com.example.challenge.restapi.security.JwtService;
import com.example.challenge.restapi.service.VeiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(VeiculoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, CustomUserDetailsService.class,
        JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
class VeiculoSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VeiculoService veiculoService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private JwtService jwtService;

    private VeiculoResponse sampleResponse() {
        return new VeiculoResponse(1L, "Ford", "Ranger", "Raptor", 2024,
                List.of(), LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void deveRetornar401AoListarSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/veiculos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401AoCriarSemToken() throws Exception {
        VeiculoRequest request = new VeiculoRequest("Toyota", "Hilux", "GR-Sport", 2024, List.of());

        mockMvc.perform(post("/api/v1/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void devePermitirLeituraParaUsuarioComum() throws Exception {
        when(veiculoService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/veiculos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveRetornar403AoCriarComPerfilUser() throws Exception {
        VeiculoRequest request = new VeiculoRequest("Toyota", "Hilux", "GR-Sport", 2024, List.of());

        mockMvc.perform(post("/api/v1/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void devePermitirCriacaoComPerfilAdmin() throws Exception {
        VeiculoRequest request = new VeiculoRequest("Toyota", "Hilux", "GR-Sport", 2024, List.of());
        when(veiculoService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/veiculos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}