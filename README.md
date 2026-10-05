# 🚗 UniRide — Sistema Completo de Caronas Universitárias

Plataforma desktop completa para mobilidade acadêmica e caronas solidárias compartilhadas entre estudantes e docentes universitários. Desenvolvido com interface gráfica moderna em **Java Swing**, tema **FlatLaf**, controle de acesso **RBAC**, autenticação por tokens **JWT**, simulação de arquitetura **REST API**, gestão de veículos, busca com múltiplos filtros, solicitação e aprovação de reservas, máquina de estados da carona e sistema mútuo de avaliações.

---

## 👥 Integrantes da Equipe

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

**Disciplina:** Gerência de Configuração  
**Instituição:** UEG / UFG / IFG

---

## 📋 Pré-requisitos

Para clonar, compilar e executar o projeto, são necessárias as seguintes ferramentas instaladas:

| Ferramenta | Versão Recomendada / Mínima | Finalidade |
|---|---|---|
| **Git** | 2.30+ | Controle de versão e obtenção do repositório |
| **Java JDK** | 17 LTS ou 21 LTS (Oracle JDK ou OpenJDK) | Compilação e execução da aplicação Java |
| **PostgreSQL** (Opcional) | 14+ | Execução dos scripts DDL/DML de banco de dados |

---

## 📥 Como Clonar o Repositório

No terminal da sua máquina, execute:

```bash
git clone https://github.com/wayserdev/gc-atividade-24-08.git
cd gc-atividade-24-08
```

---

## 🏷️ Como Acessar a Versão Entregue

Para garantir que você está executando exatamente a versão oficial da entrega:

```bash
git checkout v1.0.0
```

### Como Verificar a Versão

Execute o comando:

```bash
git describe --tags
```

O resultado esperado no terminal deverá ser exatamente:

```text
v1.0.0
```

---

## ⚙️ Configuração do Ambiente

### 1. Variáveis de Ambiente (Opcional)

O projeto inclui um arquivo modelo com variáveis seguras e fictícias. Para utilizá-lo:

```bash
# No Windows (CMD/PowerShell)
copy .env.example .env

# No Linux / macOS
cp .env.example .env
```

> **Nota de Segurança:** O arquivo `.env` gerado localmente está protegido pelo `.gitignore` e nunca será enviado ao repositório.

---

## 🗄️ Banco de Dados (PostgreSQL)

O projeto possui os scripts estruturais e de dados na raiz:

- `modelo_banco.sql`: DDL com a criação de todas as 8 tabelas (`instituicoes`, `campi`, `bairros`, `usuarios`, `veiculos`, `caronas`, `reservas`, `avaliacoes`), chaves primárias UUID, foreign keys com integridade referencial, constraints de validação (`CHECK`, `UNIQUE`) e índices de busca.
- `seed_dados.sql`: DML com registros de teste e sementes iniciais para o banco.

### Como Inicializar o Banco de Dados (PostgreSQL 14+):

```bash
# 1. Conectar ao PostgreSQL e criar o banco
psql -U postgres -c "CREATE DATABASE uniride_db;"

# 2. Executar o script DDL de modelagem
psql -U postgres -d uniride_db -f modelo_banco.sql

# 3. Executar o script DML de carga inicial
psql -U postgres -d uniride_db -f seed_dados.sql
```

> **Nota:** A aplicação Java inclui uma camada de serviço em memória (`MockApiService`) pronta para demonstração e testes imediatos, permitindo que qualquer integrante execute a interface gráfica e a suíte completa de testes de forma 100% autônoma, sem exigir a inicialização prévia de um servidor PostgreSQL local.

---

## 📦 Dependências

O projeto utiliza a biblioteca de interface visual moderna:
- **FlatLaf 3.5.4** (`lib/flatlaf-3.5.4.jar` já incluído no repositório).

Não é necessário baixar dependências adicionais pela internet.

---

## 🔨 Compilação do Projeto

### No Windows (Prompt de Comando ou PowerShell):

```cmd
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/flatlaf-3.5.4.jar;src" -d bin src/br/com/caronas/Main.java src/br/com/caronas/model/*.java src/br/com/caronas/service/*.java src/br/com/caronas/theme/*.java src/br/com/caronas/components/*.java src/br/com/caronas/views/*.java src/br/com/caronas/test/*.java
```

### No Linux / macOS:

```bash
mkdir -p bin
javac -encoding UTF-8 -cp "lib/flatlaf-3.5.4.jar:src" -d bin $(find src -name "*.java")
```

---

## 🚀 Como Executar o Sistema

### Opção 1: Via Scripts Automáticos

- **Windows:**
  ```cmd
  executar.bat
  ```

- **Linux / macOS:**
  ```bash
  chmod +x executar.sh testar.sh
  ./executar.sh
  ```

### Opção 2: Via Linha de Comando Manual

- **Windows:**
  ```cmd
  java -cp "bin;lib/flatlaf-3.5.4.jar" br.com.caronas.Main
  ```

- **Linux / macOS:**
  ```bash
  java -cp "bin:lib/flatlaf-3.5.4.jar" br.com.caronas.Main
  ```

---

## 🧪 Execução dos Testes Automatizados

O sistema conta com duas suítes automatizadas de testes com asserts completos:
1. `Feature1Test`: Validação de autenticação JWT, validação de entradas, endpoints RBAC e permissões de perfil.
2. `FullAppTest`: Validação de ciclo ponta a ponta (login, veículos, oferta de caronas, filtros de busca, fluxo de reservas, máquina de estados e cálculo de reputação por avaliações).

### Executar via Script:

- **Windows:**
  ```cmd
  testar.bat
  ```

- **Linux / macOS:**
  ```bash
  ./testar.sh
  ```

### Executar Manualmente:

- **Windows:**
  ```cmd
  java -ea "-Dfile.encoding=UTF-8" -cp "bin;lib/flatlaf-3.5.4.jar" br.com.caronas.test.Feature1Test
  java -ea "-Dfile.encoding=UTF-8" -cp "bin;lib/flatlaf-3.5.4.jar" br.com.caronas.test.FullAppTest
  ```

- **Linux / macOS:**
  ```bash
  java -ea "-Dfile.encoding=UTF-8" -cp "bin:lib/flatlaf-3.5.4.jar" br.com.caronas.test.Feature1Test
  java -ea "-Dfile.encoding=UTF-8" -cp "bin:lib/flatlaf-3.5.4.jar" br.com.caronas.test.FullAppTest
  ```

---

## 🔍 Verificação Rápida e Credenciais de Teste

Para testar as funcionalidades diretamente na interface gráfica, utilize os botões de atalho na tela de login ou os dados abaixo:

| Nome do Usuário | E-mail | Senha | Perfil | Veículo Cadastrado |
|---|---|---|---|---|
| **Carlos Eduardo** | `carlos.edu@gmail.com` | `senhaSegura123` | `ALUNO` (Motorista) | Chevrolet Onix Plus (`BRA-2E19`) |
| **Mariana Oliveira** | `mariana.oli@gmail.com` | `senha123` | `ALUNO` (Passageira) | Hyundai HB20 (`GOI-4B22`) |
| **Lucas Ribeiro** | `lucas.rib@gmail.com` | `senha123` | `ALUNO` (Motorista) | Volkswagen Gol (`KDX-9981`) |
| **Ana Carolina Lima** | `ana.lima@faculdade.br` | `admin123` | `ADMIN` (Gestor) | — |

### Roteiro Rápido de Teste:
1. Faça login como **Carlos Eduardo** (Motorista) e visualize o Dashboard com indicadores em tempo real.
2. Acesse a aba **"Minhas Caronas"** para gerenciar a carona agendada e responder a reservas de passageiros.
3. Desconecte e faça login como **Mariana Oliveira** (Passageira).
4. Acesse a aba **"Buscar Caronas"**, filtre por direção ou bairro e solicite uma vaga.
5. Acesse com a conta **Ana Carolina Lima** (Admin) e abra a aba **"Gestão de Usuários"** para verificar os recursos administrativos RBAC.
6. Abra a aba **"Console API"** para inspecionar os logs de requisições HTTP REST em tempo real.

---

## 📁 Estrutura do Repositório

```text
gc-atividade-24-08/
├── .env.example              # Modelo de variáveis de ambiente
├── .gitignore                # Regras de exclusão do Git
├── CONFIGURACAO.md           # Tabela de Itens de Configuração de Software (ICS)
├── README.md                 # Documentação e guia de reprodução do ambiente
├── executar.bat              # Script de inicialização (Windows)
├── executar.sh               # Script de inicialização (Linux/macOS)
├── testar.bat                # Script de testes automatizados (Windows)
├── testar.sh                 # Script de testes automatizados (Linux/macOS)
├── modelo_banco.sql          # Script DDL de modelagem PostgreSQL
├── seed_dados.sql            # Script DML com carga inicial de dados
├── lib/
│   └── flatlaf-3.5.4.jar     # Biblioteca de tema FlatLaf Look and Feel
└── src/
    └── br/com/caronas/
        ├── Main.java         # Ponto de entrada da aplicação
        ├── components/       # Componentes visuais reutilizáveis
        ├── model/            # Modelos de dados e DTOs
        ├── service/          # Camada de serviços e API Mock
        ├── test/             # Suítes de testes automatizados
        ├── theme/            # Definições visuais e paleta de cores
        └── views/            # Telas da interface gráfica
```