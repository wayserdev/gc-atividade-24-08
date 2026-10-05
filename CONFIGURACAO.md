# Itens de Configuração do Projeto

Este documento lista os principais Itens de Configuração de Software (ICS) do repositório, com um identificador único para cada um, facilitando a rastreabilidade e auditoria conforme os conceitos de Gerência de Configuração estudados na disciplina.

| ID | Item de Configuração | Descrição |
|---|---|---|
| DOC-01 | README.md | Documentação do projeto, instruções de reprodução do ambiente e equipe |
| DOC-02 | CONFIGURACAO.md | Lista formal dos Itens de Configuração de Software (ICS) do repositório |
| CFG-01 | .gitignore | Regras de exclusão de artefatos de build, IDEs, SO e arquivos locais |
| CFG-02 | .env.example | Modelo de variáveis de ambiente com dados fictícios e seguros |
| CFG-03 | executar.bat / executar.sh | Scripts de compilação e inicialização do sistema para Windows e Linux/macOS |
| CFG-04 | testar.bat / testar.sh | Scripts de execução das suítes de testes automatizados |
| DB-01 | modelo_banco.sql | Script DDL do banco PostgreSQL (tabelas, índices, constraints e integridade) |
| DB-02 | seed_dados.sql | Script DML com carga de dados iniciais e sementes para testes |
| SPEC-01 | Especificação Dev01 - Autenticação e Perfis | Contrato de rotas do módulo de autenticação JWT e RBAC |
| SPEC-02 | Especificação Dev02 - Catálogo e Veículos | Contrato de rotas de bairros, instituições, campi e veículos |
| SPEC-03 | Especificação Dev03 - Caronas e Reservas | Contrato de rotas, ciclo de reservas e máquina de estados da carona |
| SPEC-05 | Especificação Dev05 - Avaliações | Contrato de rotas de avaliação mútua e reputação média |

## Observações

- Cada item listado possui um identificador único (prefixo por categoria: `DOC` para documentação, `CFG` para configuração de ambiente, `DB` para modelagem e dados, `SPEC` para especificações técnicas de features).
- A padronização por categoria facilita localizar rapidamente a que tipo de artefato cada ICS pertence.
- Novos itens de configuração devem seguir o mesmo padrão de identificador ao serem adicionados ao repositório.
