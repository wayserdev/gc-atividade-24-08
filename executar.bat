@echo off
setlocal
cd /d "%~dp0"
echo [1/2] Compilando arquivos Java...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/flatlaf-3.5.4.jar;src" -d bin src/br/com/caronas/Main.java src/br/com/caronas/model/*.java src/br/com/caronas/service/*.java src/br/com/caronas/theme/*.java src/br/com/caronas/components/*.java src/br/com/caronas/views/*.java src/br/com/caronas/test/*.java
if %errorlevel% neq 0 (
    echo [ERRO] Falha na compilacao.
    pause
    exit /b %errorlevel%
)
echo [2/2] Iniciando Interface Grafica UniRide...
java -cp "bin;lib/flatlaf-3.5.4.jar" br.com.caronas.Main
