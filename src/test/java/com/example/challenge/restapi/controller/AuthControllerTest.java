package com.example.challenge.restapi.controller;

import com.example.challenge.restapi.DTOs.request.LoginRequest;
import com.example.challenge.restapi.DTOs.request.RegisterRequest;
import com.example.challenge.restapi.DTOs.response.AuthResponse;
import com.example.challenge.restapi.exception.BusinessException;
import com.example.challenge.restapi.model.Perfil;
import com.example.challenge.restapi.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// ⬇️ IMPORTS ALTERADOS
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do AuthController. Filtros de segurança desabilitados porque /api/v1/auth/**
 * já é público — o foco aqui é a lógica de login/registro em si.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void deveAutenticarComSucesso() throws Exception {
        LoginRequest request = new LoginRequest("admin@fordchallenge.com", "admin123");
        when(authService.login(any())).thenReturn(
                AuthResponse.of("token.jwt.fake", 3600000L, "admin@fordchallenge.com", "ADMIN"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token.jwt.fake"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.perfil").value("ADMIN"));
    }

    @Test
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        LoginRequest request = new LoginRequest("admin@fordchallenge.com", "senhaErrada");
        when(authService.login(any())).thenThrow(new BadCredentialsException("E-mail ou senha inválidos"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void deveRetornar400QuandoEmailInvalidoNoLogin() throws Exception {
        LoginRequest request = new LoginRequest("nao-e-um-email", "admin123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRegistrarUsuarioComSucesso() throws Exception {
        RegisterRequest request = new RegisterRequest("Maria Silva", "maria@fordchallenge.com", "senha123", Perfil.USER);
        when(authService.register(any())).thenReturn(
                AuthResponse.of("token.jwt.fake", 3600000L, "maria@fordchallenge.com", "USER"));

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria@fordchallenge.com"));
    }

    @Test
    void deveRetornar422QuandoEmailJaCadastrado() throws Exception {
        RegisterRequest request = new RegisterRequest("Maria Silva", "admin@fordchallenge.com", "senha123", Perfil.USER);
        when(authService.register(any()))
                .thenThrow(new BusinessException("Já existe um usuário cadastrado com o e-mail: admin@fordchallenge.com"));

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }
}