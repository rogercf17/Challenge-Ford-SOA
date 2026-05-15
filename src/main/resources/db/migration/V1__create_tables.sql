-- ============================================================
-- V1 - Criação das tabelas para o Desafio 01: Inteligência
--      Competitiva Automotiva - Ford FIAP 2026
-- ============================================================

-- Tabela de veículos concorrentes
CREATE TABLE IF NOT EXISTS veiculo (
    id               BIGSERIAL PRIMARY KEY,
    marca            VARCHAR(100) NOT NULL,
    modelo           VARCHAR(100) NOT NULL,
    versao           VARCHAR(100) NOT NULL,
    ano              INT,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_veiculo UNIQUE (marca, modelo, versao)
);

-- Tabela de especificações técnicas (chave-valor por veículo)
CREATE TABLE IF NOT EXISTS especificacao (
    id               BIGSERIAL PRIMARY KEY,
    veiculo_id       BIGINT NOT NULL REFERENCES veiculo(id) ON DELETE CASCADE,
    atributo         VARCHAR(150) NOT NULL,
    valor            VARCHAR(500),
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_especificacao UNIQUE (veiculo_id, atributo)
);

-- Índices para buscas comuns
CREATE INDEX IF NOT EXISTS idx_veiculo_marca   ON veiculo(marca);
CREATE INDEX IF NOT EXISTS idx_veiculo_modelo  ON veiculo(modelo);
CREATE INDEX IF NOT EXISTS idx_espec_veiculo   ON especificacao(veiculo_id);
CREATE INDEX IF NOT EXISTS idx_espec_atributo  ON especificacao(atributo);