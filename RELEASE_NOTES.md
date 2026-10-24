# UniRide v1.2.0

## Novidades

- Distribuição ZIP com `UniRide.jar`, FlatLaf 3.5.4 e inicializadores para Windows e Linux/macOS.
- Empacotamento automatizado com validação de integridade e execução dos 19 testes a partir dos JARs distribuídos.
- Publicação automática de release sobre a tag com notas e SHA-256 do ZIP.

## Como usar

Baixe `uniride-v1.2.0.zip`, extraia todo o conteúdo e execute `iniciar.bat`
ou `bash iniciar.sh`. Requer Java 17+ e ambiente gráfico. Não requer JDK
nem PostgreSQL para a demonstração em memória. O ZIP é distribuível,
mas não inclui um runtime Java nem constitui instalador assinado.

## Versão e rastreabilidade

O incremento MINOR de 1.1.0 para 1.2.0 representa a nova capacidade
compatível de distribuição e publicação automatizadas. Não há alteração
incompatível no contrato da aplicação. As mudanças estão no PR de
empacotamento; a base de CI foi integrada pelo PR #11.

Confira `CHANGELOG.md` para o histórico e `docs/CI.md` para a configuração
da proteção da main, que deve ser feita por um administrador.
