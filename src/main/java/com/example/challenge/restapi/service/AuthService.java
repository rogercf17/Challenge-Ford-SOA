package com.example.challenge.restapi.service;

import com.example.challenge.restapi.DTOs.request.LoginRequest;
import com.example.challenge.restapi.DTOs.request.RegisterRequest;
import com.example.challenge.restapi.DTOs.response.AuthResponse;
import com.example.challenge.restapi.exception.BusinessException;
import com.example.challenge.restapi.model.Usuario;
import com.example.challenge.restapi.repository.UsuarioRepository;
import com.example.challenge.restapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Já existe um usuário cadastrado com o e-mail: " + request.email());
        }

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .perfil(request.perfil())
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario, Map.of("perfil", usuario.getPerfil().name()));
        return AuthResponse.of(token, jwtService.getExpirationMs(), usuario.getEmail(), usuario.getPerfil().name());
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.senha())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

        String token = jwtService.generateToken(usuario, Map.of("perfil", usuario.getPerfil().name()));
        return AuthResponse.of(token, jwtService.getExpirationMs(), usuario.getEmail(), usuario.getPerfil().name());
    }
}
