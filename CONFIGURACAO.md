# Itens de Configuração do Projeto

Este documento lista os principais Itens de Configuração de Software (ICS) do repositório, com um identificador único para cada um, facilitando rastreabilidade e auditoria conforme os conceitos de Gerência de Configuração estudados na disciplina.

| ID | Item de Configuração | Descrição |
|---|---|---|
| DOC-01 | README.md | Documentação inicial e identificação do projeto e da equipe |
| CFG-01 | .gitignore | Regras de arquivos que não devem ser versionados |
| DOC-02 | CONFIGURACAO.md | Este arquivo — lista dos itens de configuração do repositório |
| SPEC-01 | Especificação Dev01 - Autenticação e Perfis | Contrato de rotas do módulo de autenticação JWT e RBAC |
| SPEC-02 | Especificação Dev02 - Catálogo e Veículos | Contrato de rotas de bairros, instituições, campi e veículos |
| SPEC-03 | Especificação Dev03 - Caronas | Contrato de rotas e máquina de estados da carona |
| SPEC-05 | Especificação Dev05 - Avaliações | Contrato de rotas de avaliação mútua e moderação |
| DB-01 | Modelagem do Banco de Dados | Dicionário de tabelas e Diagrama Entidade-Relacionamento (DER) |
| SRC-01 | MockApiService.java | Implementação em memória das rotas de Auth, Perfil e Admin |
| SRC-02 | PasswordHasher.java | Hash de senhas PBKDF2-HMAC-SHA256 com salt aleatório |
| SRC-03 | JwtService.java | Emissão e validação de JWT HS256 com expiração |
| TEST-01 | AuthSecurityTest.java | Testes de hash, JWT, sessão, isolamento e validações |

## Observações

- Cada item listado possui um identificador único (prefixo por categoria: `DOC` para documentação, `CFG` para configuração de ambiente, `SPEC` para especificações técnicas de features, `DB` para modelagem de dados, `SRC` para código-fonte e `TEST` para testes).
- A padronização por categoria facilita localizar rapidamente a que tipo de artefato cada ICS pertence.
- Novos itens de configuração devem seguir o mesmo padrão de identificador ao serem adicionados ao repositório.
