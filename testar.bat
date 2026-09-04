@echo off
chcp 65001 > nul
echo [1/2] Compilando arquivos Java...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/*;src" -d bin src/br/com/caronas/Main.java src/br/com/caronas/model/*.java src/br/com/caronas/service/*.java src/br/com/caronas/theme/*.java src/br/com/caronas/components/*.java src/br/com/caronas/views/*.java src/br/com/caronas/test/*.java
if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilação.
    pause
    exit /b %errorlevel%
)
echo [2/2] Executando Suíte de Testes Automatizados (Auth, Perfis e Feature 1)...
java -ea "-Dfile.encoding=UTF-8" -cp "bin;lib/*" br.com.caronas.test.Feature1Test
if %errorlevel% neq 0 (
    echo [ERRO] Falha nos testes da Feature 1.
    pause
    exit /b %errorlevel%
)
java -ea "-Dfile.encoding=UTF-8" -cp "bin;lib/*" br.com.caronas.test.AuthSecurityTest
pause
