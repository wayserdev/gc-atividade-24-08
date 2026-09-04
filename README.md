# GitHub na Prática

## Integrantes
- Nycolas Souza Linard
- Elias Vieira Silva
- Joao Paulo Goncalves Santos
- Wander Gabriel Oliveira Tavares
- Wayser Henrique de Faria Lopes
- Pedro Vitor Marques Carneiro Jai
- Thallys heduardo borba marques
- Henrique Pereira Martins
- Matheus Henrique Bessa Oliveira
- Divino Marcio de Souza Junior
- Lucas Ribeiro de Almeida Marques
- Jhonathan Batista Faria
- Isaac Rodrigues Vieira
- Rowan Falcão

## Disciplina
Gerência de Configuração

## Objetivo
Este repositório foi criado para praticar os conceitos de Gerência de Configuração utilizando Git e GitHub.

## Feature 1 — Auth & Perfis

O aplicativo Swing demonstra o fluxo de autenticação e autorização do sistema de caronas:

- `POST /auth/cadastrar`: valida o DTO, garante instituição existente, cria o usuário como `ALUNO` e emite JWT.
- `POST /auth/login`: compara a senha com hash PBKDF2-HMAC-SHA256 e emite um JWT HS256 com `sub`, `nivel`, `iat`, `exp` e `jti`.
- `GET/PUT /usuarios/me`: consulta e altera somente os dados permitidos do próprio perfil.
- `GET/PUT/DELETE /admin/usuarios`: gestão de usuários protegida por RBAC (`ADMIN`), incluindo busca, filtro, paginação, promoção e bloqueio de autoexclusão.

O serviço atual é um mock em memória para a apresentação acadêmica; `modelo_banco.sql` permanece como modelo PostgreSQL. Nenhuma resposta da API ou tela expõe o hash de senha, e sessões rejeitam assinatura inválida, token expirado ou usuário removido.

### Executar

Com um JDK instalado e disponível no `PATH`:

```bat
executar.bat
```

Para validar os contratos e as regras de segurança:

```bat
testar.bat
```

Credenciais de demonstração: `carlos.edu@gmail.com` / `senhaSegura123` (aluno) e `ana.lima@faculdade.br` / `admin123` (admin). A aplicação inicia deslogada; os atalhos estão identificados como demonstração.

Em uma implantação real, defina `CARONAS_JWT_SECRET` com pelo menos 32 caracteres e substitua o `MockApiService` por um repositório persistente. Sem essa variável, o mock gera um segredo efêmero a cada execução, adequado somente para a demonstração local.
