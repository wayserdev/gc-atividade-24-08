-- =========================================================
-- ARQUIVO: seed_dados.sql (Dados iniciais para testes)
-- =========================================================

-- 1. Inserindo Instituição
INSERT INTO instituicoes (id, nome) 
VALUES ('11111111-1111-1111-1111-111111111111', 'Universidade Federal de Goiás');

-- 2. Inserindo Campus
INSERT INTO campi (id, instituicao_id, nome, cidade, endereco_referencia) 
VALUES ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 'Campus Samambaia', 'Goiânia', 'Av. Esperança, s/n');

-- 3. Inserindo Bairro
INSERT INTO bairros (id, nome, cidade) 
VALUES ('33333333-3333-3333-3333-333333333333', 'Setor Bueno', 'Goiânia');

-- 4. Inserindo Usuários (1 Administrador e 1 Aluno Motorista)
INSERT INTO usuarios (id, instituicao_id, nome_completo, email, senha_hash, nivel, telefone, curso) 
VALUES 
('44444444-4444-4444-4444-444444444444', '11111111-1111-1111-1111-111111111111', 'Carlos Eduardo Admin', 'admin@faculdade.br', '$2b$10$e/testHashBcrypt12345678', 'ADMIN', '62999998888', 'Docente / Coordenação'),
('55555555-5555-5555-5555-555555555555', '11111111-1111-1111-1111-111111111111', 'João Paulo Motorista', 'joao.motorista@gmail.com', '$2b$10$e/testHashBcrypt12345678', 'ALUNO', '62988887777', 'Engenharia de Software');

-- 5. Inserindo Veículo
INSERT INTO veiculos (id, motorista_id, modelo, placa, capacidade_assentos) 
VALUES ('66666666-6666-6666-6666-666666666666', '55555555-5555-5555-5555-555555555555', 'Chevrolet Onix Prata', 'BRA2E19', 4);