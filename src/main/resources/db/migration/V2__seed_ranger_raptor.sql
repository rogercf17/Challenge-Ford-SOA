-- ============================================================
-- V2 - Dados de validação: Ford Ranger Raptor (referência)
-- ============================================================

INSERT INTO veiculo (marca, modelo, versao, ano)
VALUES ('Ford', 'Ranger', 'Raptor', 2024)
ON CONFLICT (marca, modelo, versao) DO NOTHING;

-- Inserir especificações da Ford Ranger Raptor
WITH v AS (SELECT id FROM veiculo WHERE marca='Ford' AND modelo='Ranger' AND versao='Raptor')
INSERT INTO especificacao (veiculo_id, atributo, valor)
SELECT v.id, esp.atributo, esp.valor
FROM v,
(VALUES
    ('Motor',               '2.0 Bi-Turbo EcoBlue (petróleo)'),
    ('Potência (cv)',       '213 cv a 3.750 rpm'),
    ('Torque (Nm)',         '500 Nm a 1.750–2.000 rpm'),
    ('Transmissão',        'Automática 10 marchas'),
    ('Tração',             '4x4 com reduzida'),
    ('Caçamba (L)',         '1.075'),
    ('Capacidade de Carga (kg)', '1.010'),
    ('Capacidade Reboque (kg)',  '3.500'),
    ('Tanque (L)',          '80'),
    ('Suspensão Dianteira', 'Independente com molas helicoidais FOX'),
    ('Suspensão Traseira',  'Folhas de mola com amortecedores FOX'),
    ('Freios Dianteiros',   'Disco ventilado'),
    ('Freios Traseiros',    'Disco'),
    ('Pneus',               '285/70 R17'),
    ('Dimensão (LxAxA mm)', '5.425 x 2.028 x 1.873'),
    ('Distância entre eixos (mm)', '3.270'),
    ('Altura livre do solo (mm)',  '283'),
    ('Profundidade de vau (mm)',   '850'),
    ('Peso (kg)',            '2.385'),
    ('Ar-condicionado',     'Automático dual zone'),
    ('Central multimídia',  'SYNC 4A - 12 polegadas'),
    ('Apple CarPlay / Android Auto', 'Sim, sem fio'),
    ('Câmera de Ré',        'Sim'),
    ('Câmeras 360°',        'Sim'),
    ('Sensor de estacionamento', 'Dianteiro e traseiro'),
    ('Bancos',              'Couro aquecido e ventilado'),
    ('Airbags',             '7'),
    ('Alerta de ponto cego','Sim'),
    ('Assist. manutenção de faixa', 'Sim'),
    ('Frenagem autônoma de emergência', 'Sim'),
    ('Pre-Colisão com Pedestres', 'Sim'),
    ('Hill Descent Control','Sim'),
    ('Modos de condução',   '6 (Normal, Eco, Sport, Baja, Rock, Sand)'),
    ('Preço médio (BRL)',   'R$ 399.990')
) AS esp(atributo, valor)
ON CONFLICT (veiculo_id, atributo) DO NOTHING;