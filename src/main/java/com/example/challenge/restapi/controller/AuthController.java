package com.example.challenge.restapi.controller;

import com.example.challenge.restapi.DTOs.request.LoginRequest;
import com.example.challenge.restapi.DTOs.request.RegisterRequest;
import com.example.challenge.restapi.DTOs.response.AuthResponse;
import com.example.challenge.restapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Registro e login de usuários, emissão de token JWT")
@CrossOrigin("https://challenge-ford-soa.onrender.com/")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário e retorna o token JWT de acesso.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "E-mail já cadastrado")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Valida as credenciais e retorna o token JWT de acesso.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "E-mail ou senha incorretos")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
