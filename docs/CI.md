# Build e integração contínua

O workflow `.github/workflows/ci.yml` executa em cada push e pull request.
Ele prepara o JDK Temurin 17, confere o SHA-256 do FlatLaf 3.5.4 já
versionado, compila todos os fontes e executa `Feature1Test` e `FullAppTest`
com assertions habilitadas (`-ea`). Qualquer erro encerra o job com falha.

## Proteção da main

Um administrador do repositório deve configurar uma regra para `main`:

1. Settings > Branches > Add branch protection rule.
2. Branch name pattern: `main`.
3. Require a pull request before merging.
4. Require status checks to pass before merging: `Build e testes`.
5. Require branches to be up to date before merging.
6. Impedir force push e exclusão da branch; aplicar a regra também aos
   administradores se a política da equipe assim determinar.

A permissão de escrita de colaborador não permite administrar essa regra.
CI vermelho só bloqueia o merge quando a regra estiver configurada e ativa.

## Demonstração

Em um PR de demonstração, introduzir uma falha temporária de compilação,
observar o check vermelho e corrigir em um novo commit. O histórico deve
preservar ambas as execuções. Nunca integrar o commit com falha à main.
Com a proteção ativa, registrar também a mensagem de merge bloqueado.

Após integrar a alteração validada, marcar `v1.1.0` sem mover tags existentes.
Verificar com `git describe --tags --exact-match v1.1.0`.
