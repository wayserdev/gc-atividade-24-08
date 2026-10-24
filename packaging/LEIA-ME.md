# UniRide

Aplicativo desktop de caronas universitárias. Requer Java 17 ou superior
e ambiente gráfico. Este pacote contém o JAR da aplicação e o FlatLaf
3.5.4; não é necessário instalar um compilador ou baixar bibliotecas.

Extraia o ZIP inteiro, preservando a pasta `lib` ao lado de `UniRide.jar`.

- Windows: execute `iniciar.bat`.
- Linux/macOS: execute `bash iniciar.sh`.
- Qualquer sistema com Java: `java -jar UniRide.jar`.

O aplicativo usa uma implementação em memória para demonstração.
Os dados e as contas de exemplo não representam um serviço de produção.
Não é necessário preparar PostgreSQL para utilizar esta demonstração.

Conta de demonstração: `carlos.edu@gmail.com` / `senhaSegura123`.
Outras contas estão documentadas no README do repositório.

Código-fonte e instruções completas:
https://github.com/wayserdev/gc-atividade-24-08

O arquivo `.sha256` publicado junto ao ZIP permite conferir sua integridade:
`sha256sum --check uniride-v1.2.0.zip.sha256`.
