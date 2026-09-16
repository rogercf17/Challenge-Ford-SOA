-- ============================================================
-- V3 - Criação da tabela de usuários para autenticação/autorização
--      Desafio 01 - Inteligência Competitiva Automotiva - Ford FIAP 2026
-- ============================================================

CREATE TABLE IF NOT EXISTS usuario (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(150) NOT NULL,
    email            VARCHAR(150) NOT NULL,
    senha            VARCHAR(255) NOT NULL,
    perfil           VARCHAR(20)  NOT NULL DEFAULT 'USER',
    ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_usuario_email UNIQUE (email),
    CONSTRAINT chk_usuario_perfil CHECK (perfil IN ('ADMIN', 'USER'))
);

CREATE INDEX IF NOT EXISTS idx_usuario_email ON usuario(email);

-- Usuário admin inicial para testes
-- Senha: admin123 (hash BCrypt, strength 10 — compatível com BCryptPasswordEncoder padrão)
INSERT INTO usuario (nome, email, senha, perfil)
VALUES (
    'Administrador',
    'admin@fordchallenge.com',
    '$2b$10$tDnOkq1CzYQD/sqciM4wsO8OzRgxIrZ2vuD4VsrrWPWf58LXNTPJi',
    'ADMIN'
);