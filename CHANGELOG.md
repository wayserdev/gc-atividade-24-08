# Histórico de mudanças

## [Não versionado]

### Adicionado

- Guia de entrega das atividades de CI e release (`docs/ENTREGA_GC.md`), com roteiro de gravação e evidências — PR #14.

### Corrigido

- Revisão e organização dos scripts de banco de dados (`modelo_banco.sql` e `seed_dados.sql`), rastreada na issue #9 — PR #10.

## 1.2.0

### Adicionado

- Script `empacotar.sh`, JAR executável, distribuição ZIP, inicializadores e checksum.
- Workflow de release que testa e publica o pacote na tag correspondente.
- Testes das duas suítes usando os JARs da distribuição antes de publicar.

## 1.1.0

### Adicionado

- CI em push e pull request com Temurin 17 e validação SHA-256 do FlatLaf.
- Compilação completa e execução das suítes existentes com assertions habilitadas.
- Orientação para proteção da main pelo proprietário do repositório.
- Evidência de falha controlada e correção no PR #11.

## 1.0.1

### Corrigido

- Instruções de PowerShell no README, rastreadas na issue #5 e no PR #6.

## 1.0.0

### Adicionado

- Base de ambiente reproduzível, dependência FlatLaf versionada, scripts de execução e teste.
- Documentação do projeto UniRide, suas funcionalidades e itens de configuração.

Os registros de 1.0.0 e 1.0.1 resumem as respectivas tags existentes.
