@echo off
setlocal
cd /d "%~dp0"
echo ==========================================================
echo [1/3] Compilando arquivos Java do UniRide...
echo ==========================================================
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/flatlaf-3.5.4.jar;src" -d bin src/br/com/caronas/Main.java src/br/com/caronas/model/*.java src/br/com/caronas/service/*.java src/br/com/caronas/theme/*.java src/br/com/caronas/components/*.java src/br/com/caronas/views/*.java src/br/com/caronas/test/*.java
if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilacao.
    pause
    exit /b %errorlevel%
)
echo.
echo ==========================================================
echo [2/3] Executando Testes Feature 1 (Auth / JWT / RBAC)...
echo ==========================================================
java -ea -cp "bin;lib/flatlaf-3.5.4.jar" br.com.caronas.test.Feature1Test
if %errorlevel% neq 0 (
    echo [ERRO] Falha na suite Feature 1.
    pause
    exit /b %errorlevel%
)
echo.
echo ==========================================================
echo [3/3] Executando Suite Completa do App de Caronas...
echo ==========================================================
java -ea -cp "bin;lib/flatlaf-3.5.4.jar" br.com.caronas.test.FullAppTest
if %errorlevel% neq 0 (
    echo [ERRO] Falha na suite completa.
    pause
    exit /b %errorlevel%
)
echo.
echo ==========================================================
echo [SUCESSO] TODOS OS TESTES PASSARAM COM EXCELENCIA!
echo ==========================================================
