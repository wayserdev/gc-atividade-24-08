-- =========================================================
-- BANCO DE DADOS: MVP CARONA UNIVERSITÁRIA
-- PostgreSQL
-- =========================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- =========================================================
-- 1. INSTITUICOES
-- =========================================================

CREATE TABLE instituicoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(150) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =========================================================
-- 2. CAMPI
-- =========================================================

CREATE TABLE campi (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    instituicao_id UUID NOT NULL,
    nome VARCHAR(120) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    endereco_referencia VARCHAR(255),

    CONSTRAINT fk_campi_instituicao
        FOREIGN KEY (instituicao_id)
        REFERENCES instituicoes(id)
        ON DELETE RESTRICT
);

-- =========================================================
-- 3. BAIRROS
-- =========================================================

CREATE TABLE bairros (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL
);

-- =========================================================
-- 4. USUARIOS
-- =========================================================

CREATE TABLE usuarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    instituicao_id UUID NOT NULL,
    nome_completo VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    nivel VARCHAR(20) NOT NULL DEFAULT 'ALUNO',
    telefone VARCHAR(20),
    curso VARCHAR(100),
    foto_url VARCHAR(255),
    media_avaliacao DECIMAL(3,2),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuarios_instituicao
        FOREIGN KEY (instituicao_id)
        REFERENCES instituicoes(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_usuario_nivel
        CHECK (nivel IN ('ALUNO', 'ADMIN')),

    CONSTRAINT chk_media_avaliacao
        CHECK (
            media_avaliacao IS NULL
            OR media_avaliacao BETWEEN 1.00 AND 5.00
        )
);

-- =========================================================
-- 5. VEICULOS
-- =========================================================

CREATE TABLE veiculos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    motorista_id UUID NOT NULL,
    modelo VARCHAR(80) NOT NULL,
    placa VARCHAR(10) NOT NULL,
    capacidade_assentos SMALLINT NOT NULL,

    CONSTRAINT fk_veiculos_motorista
        FOREIGN KEY (motorista_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_capacidade_assentos
        CHECK (capacidade_assentos > 0),

    CONSTRAINT uq_veiculo_placa
        UNIQUE (placa)
);

-- =========================================================
-- 6. CARONAS
-- =========================================================

CREATE TABLE caronas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    motorista_id UUID NOT NULL,
    veiculo_id UUID NOT NULL,
    direcao VARCHAR(25) NOT NULL,
    bairro_id UUID NOT NULL,
    campus_id UUID NOT NULL,
    ponto_encontro VARCHAR(255),
    horario_saida TIMESTAMP NOT NULL,
    assentos_totais SMALLINT NOT NULL,
    assentos_disponiveis SMALLINT NOT NULL,
    valor_contribuicao DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_caronas_motorista
        FOREIGN KEY (motorista_id)
        REFERENCES usuarios(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_caronas_veiculo
        FOREIGN KEY (veiculo_id)
        REFERENCES veiculos(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_caronas_bairro
        FOREIGN KEY (bairro_id)
        REFERENCES bairros(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_caronas_campus
        FOREIGN KEY (campus_id)
        REFERENCES campi(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_direcao
        CHECK (
            direcao IN (
                'BAIRRO_PARA_CAMPUS',
                'CAMPUS_PARA_BAIRRO'
            )
        ),

    CONSTRAINT chk_status_carona
        CHECK (
            status IN (
                'AGENDADA',
                'EM_ANDAMENTO',
                'FINALIZADA',
                'CANCELADA'
            )
        ),

    CONSTRAINT chk_assentos_totais
        CHECK (assentos_totais > 0),

    CONSTRAINT chk_assentos_disponiveis
        CHECK (
            assentos_disponiveis >= 0
            AND assentos_disponiveis <= assentos_totais
        ),

    CONSTRAINT chk_valor_contribuicao
        CHECK (valor_contribuicao >= 0)
);

-- =========================================================
-- 7. RESERVAS
-- =========================================================

CREATE TABLE reservas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    carona_id UUID NOT NULL,
    passageiro_id UUID NOT NULL,
    vagas_solicitadas SMALLINT NOT NULL DEFAULT 1,
    ponto_embarque VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_reservas_carona
        FOREIGN KEY (carona_id)
        REFERENCES caronas(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reservas_passageiro
        FOREIGN KEY (passageiro_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_vagas_solicitadas
        CHECK (vagas_solicitadas > 0),

    CONSTRAINT chk_status_reserva
        CHECK (
            status IN (
                'PENDENTE',
                'ACEITA',
                'RECUSADA',
                'CANCELADA'
            )
        ),

    CONSTRAINT uq_reserva_passageiro_carona
        UNIQUE (carona_id, passageiro_id)
);

-- =========================================================
-- 8. AVALIACOES
-- =========================================================

CREATE TABLE avaliacoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    carona_id UUID NOT NULL,
    avaliador_id UUID NOT NULL,
    avaliado_id UUID NOT NULL,
    nota SMALLINT NOT NULL,
    comentario TEXT,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_avaliacoes_carona
        FOREIGN KEY (carona_id)
        REFERENCES caronas(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_avaliacoes_avaliador
        FOREIGN KEY (avaliador_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_avaliacoes_avaliado
        FOREIGN KEY (avaliado_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_nota
        CHECK (nota BETWEEN 1 AND 5),

    CONSTRAINT chk_avaliador_diferente_avaliado
        CHECK (avaliador_id <> avaliado_id),

    CONSTRAINT uq_avaliacao
        UNIQUE (carona_id, avaliador_id, avaliado_id)
);

-- =========================================================
-- INDICES
-- =========================================================

CREATE INDEX idx_campi_instituicao
    ON campi(instituicao_id);

CREATE INDEX idx_usuarios_instituicao
    ON usuarios(instituicao_id);

CREATE INDEX idx_veiculos_motorista
    ON veiculos(motorista_id);

CREATE INDEX idx_caronas_motorista
    ON caronas(motorista_id);

CREATE INDEX idx_caronas_bairro
    ON caronas(bairro_id);

CREATE INDEX idx_caronas_campus
    ON caronas(campus_id);

CREATE INDEX idx_caronas_horario
    ON caronas(horario_saida);

CREATE INDEX idx_reservas_carona
    ON reservas(carona_id);

CREATE INDEX idx_reservas_passageiro
    ON reservas(passageiro_id);

CREATE INDEX idx_avaliacoes_avaliado
    ON avaliacoes(avaliado_id);
