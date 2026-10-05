#!/bin/bash
set -e
cd "$(dirname "$0")"
echo "=========================================================="
echo "[1/3] Compilando arquivos Java do UniRide..."
echo "=========================================================="
mkdir -p bin
javac -encoding UTF-8 -cp "lib/flatlaf-3.5.4.jar:src" -d bin $(find src -name "*.java")

echo ""
echo "=========================================================="
echo "[2/3] Executando Testes Feature 1 (Auth / JWT / RBAC)..."
echo "=========================================================="
java -ea "-Dfile.encoding=UTF-8" -cp "bin:lib/flatlaf-3.5.4.jar" br.com.caronas.test.Feature1Test

echo ""
echo "=========================================================="
echo "[3/3] Executando Suite Completa do App de Caronas..."
echo "=========================================================="
java -ea "-Dfile.encoding=UTF-8" -cp "bin:lib/flatlaf-3.5.4.jar" br.com.caronas.test.FullAppTest

echo ""
echo "=========================================================="
echo "[SUCESSO] TODOS OS TESTES PASSARAM COM EXCELENCIA!"
echo "=========================================================="
