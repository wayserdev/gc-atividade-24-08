# Guia de entrega do projeto UniRide

## Estado em 24 de outubro de 2026

O build e os testes estão automatizados na main. A tag anotada v1.1.0
identifica a introdução do CI. A v1.2.0 identifica o empacotamento e a
publicação automática. A release v1.2.0 está publicada com ZIP executável,
checksum e notas. Não mover ou recriar essas tags.

Ainda faltam a proteção da main pelo proprietário, a gravação dos vídeos,
a demonstração de merge efetivamente bloqueado e a participação de cada
integrante no período exigido. Um check vermelho sozinho não bloqueia
merge sem regra ativa. A conta JPClow3 possui escrita, mas não administração.

## Links para a pessoa responsável pela entrega

- Repositório: https://github.com/wayserdev/gc-atividade-24-08
- CI e histórico vermelho/corrigido: https://github.com/wayserdev/gc-atividade-24-08/pull/11
- Empacotamento: https://github.com/wayserdev/gc-atividade-24-08/pull/12
- Demonstração de compilação com falha: https://github.com/wayserdev/gc-atividade-24-08/pull/13
- Workflow CI: https://github.com/wayserdev/gc-atividade-24-08/actions/workflows/ci.yml
- Workflow Release: https://github.com/wayserdev/gc-atividade-24-08/actions/workflows/release.yml
- Release: https://github.com/wayserdev/gc-atividade-24-08/releases/tag/v1.2.0
- Tag CI: https://github.com/wayserdev/gc-atividade-24-08/tree/v1.1.0
- Tag distribuição: https://github.com/wayserdev/gc-atividade-24-08/tree/v1.2.0

## O que foi implementado

O CI prepara Temurin 17, verifica `dependencies.sha256`, compila com UTF-8
e executa Feature1Test e FullAppTest com assertions habilitadas. O FlatLaf
3.5.4 está versionado e seu hash é conferido; não existe resolvedor de
dependências que busque silenciosamente a versão mais nova.

`empacotar.sh` repete a validação, gera `UniRide.jar` com Main-Class e
Class-Path, copia o FlatLaf, inclui instruções e inicializadores e testa as
duas suítes a partir dos JARs distribuídos. O CI armazena o ZIP como
artifact. Em uma tag de versão, `release.yml` publica o ZIP e seu SHA-256
com as notas de `RELEASE_NOTES.md`.

O pacote requer Java 17+ e ambiente gráfico; não inclui o Java nem é um
instalador assinado. A aplicação usa dados em memória para demonstração,
sem exigir PostgreSQL. O checksum prova integridade dos bytes baixados,
não substitui uma assinatura de editor.

## Evidências de validação

- JDK Temurin 17 local: 12 testes de Feature1Test e 7 de FullAppTest passaram.
- PR #11: CI passou, falhou por hash intencionalmente incorreto e passou
  após correção em novo commit; o histórico foi preservado.
- Falha controlada: https://github.com/wayserdev/gc-atividade-24-08/actions/runs/40066955118
- Correção verde: https://github.com/wayserdev/gc-atividade-24-08/actions/runs/40067036683
- PR #12: compilação, testes e geração do ZIP passaram.
- Release publicada pelo workflow: https://github.com/wayserdev/gc-atividade-24-08/actions/runs/40067803774
- O ZIP publicado foi baixado novamente, seu SHA-256 foi conferido e a
  suíte FullAppTest passou usando os JARs extraídos.
- Isso valida o build e as suítes automatizadas. A apresentação em vídeo
  e a proteção da main ainda não foram comprovadas.

## Proteção da main e demonstração de bloqueio

O proprietário deve acessar Settings > Branches e criar uma regra para
`main`, exigindo pull request e o status check `Build e testes`. Conferir
`docs/CI.md`. Se a interface disponibilizar Rulesets no lugar de Branches,
criar a regra equivalente para a mesma branch. Não habilitar force push.

O PR #13 contém somente uma classe de demonstração com uma expressão
Java inválida. Ele é propositalmente vermelho. **Não integrar esse arquivo.**

1. Com a regra ativa, abrir #13 e gravar o check vermelho e o merge bloqueado.
2. Na branch `demo/ci-bloqueio`, remover `src/br/com/caronas/test/CiBuildDemo.java`
   e criar um novo commit, ou reverter o commit de demonstração.
3. Enviar a correção no mesmo PR e aguardar todos os checks verdes.
4. Gravar o estado corrigido e então integrar o PR.
5. As tags anteriores continuam intactas; não é necessário movê-las.

Comandos para corrigir a demonstração em um clone limpo:

```sh
git fetch origin
git switch demo/ci-bloqueio
git pull --ff-only
git revert --no-edit 743592b382d98220af4c050ee7a3c96bf9f552f1
git push origin demo/ci-bloqueio
```

Se a branch não existir localmente, usar `git switch --track origin/demo/ci-bloqueio`.
Se outra pessoa já tiver corrigido o PR, não repetir o revert: verificar
primeiro o diff e os checks atuais. Não executar estes comandos sobre
trabalho local não salvo.

## Roteiro do vídeo de CI de 5 a 10 minutos

Sugestão de cerca de 7 minutos:

1. 0:00 a 0:45: apresentar o UniRide e o objetivo do CI.
2. 0:45 a 2:00: explicar os gatilhos, Temurin 17, dependência travada,
   build e testes em `.github/workflows/ci.yml`.
3. 2:00 a 3:00: mostrar uma execução verde e os resultados das duas suítes.
4. 3:00 a 5:00: com a proteção já ativa, mostrar o PR #13 vermelho e
   bloqueado, a correção em novo commit e os checks verdes.
5. 5:00 a 6:00: mostrar a regra da main e explicar seu papel no controle
   de modificação.
6. 6:00 a 7:00: mostrar a tag v1.1.0 e `git describe --tags --exact-match`.

Para demonstrar a linha-base exata em um clone limpo:

```sh
git fetch --tags origin
git switch --detach v1.1.0
git describe --tags --exact-match
git switch main
```

O resultado é `v1.1.0`. Na main atual, `git describe --tags` pode mostrar
commits posteriores à tag; isso é esperado. Não confundir a versão em
checkout com a versão mais nova do repositório.

## Roteiro do vídeo de release de 5 a 10 minutos

1. 0:00 a 1:30: explicar CHANGELOG.md e relacionar 1.1.0 ao PR #11 e
   1.2.0 ao PR #12.
2. 1:30 a 3:30: mostrar o script, o log de empacotamento, os testes e o ZIP.
3. 3:30 a 5:00: mostrar a release publicada, notas, tag e anexos.
4. 5:00 a 6:30: baixar o ZIP, extrair integralmente e demonstrar a execução
   com Java 17+ no ambiente gráfico.
5. 6:30 a 7:30: justificar SemVer e explicar a verificação SHA-256.

A passagem 1.1.0 para 1.2.0 é MINOR por adicionar uma capacidade compatível
de distribuição e publicação. Não é apenas correção de defeito (patch),
nem introduz incompatibilidade no contrato da aplicação (major).

## Participação dos integrantes

Os 14 nomes estão no README. O enunciado de CI exige pelo menos um commit
ou PR por integrante no período, e a atividade de release também confere
participação no histórico. Não atribuir artificialmente commits a terceiros.
Cada integrante deve realizar uma contribuição real e verificável.

Na consulta ao histórico da main a partir de 10/10/2026, apenas João Paulo
Gonçalves Santos apareceu como autor. Isso não prova ausência de trabalho
fora da main; o responsável deve conferir as branches e contribuições
efetivamente integradas e providenciar a participação faltante.

## Texto para a entrega de CI

Projeto: UniRide — Sistema de Caronas Universitárias.
Repositório: https://github.com/wayserdev/gc-atividade-24-08
Tag entregue: v1.1.0.

Adicionar o link real do vídeo, os nomes dos integrantes e as evidências
da proteção ativa antes de enviar pelo grupo. O vídeo ainda não existe.

## Texto para a entrega de release

Projeto: UniRide — Sistema de Caronas Universitárias.
Repositório: https://github.com/wayserdev/gc-atividade-24-08
Release: https://github.com/wayserdev/gc-atividade-24-08/releases/tag/v1.2.0
Tag entregue: v1.2.0.

Adicionar o link real do vídeo e os nomes dos integrantes antes de enviar.
O registro do grupo e a entrega no Classroom serão feitos pelo representante.

## Atividade anterior e definição do grupo

A tag v1.0.0 já existia antes desta execução. Isso não comprova que todos
os integrantes a executaram nas suas máquinas nem substitui o vídeo
exigido na atividade anterior. A definição do grupo solicita planilha de
e-mails e descrição do projeto; esse cadastro fica com o representante.
