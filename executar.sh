#!/bin/bash
set -e
cd "$(dirname "$0")"
echo "[1/2] Compilando arquivos Java..."
mkdir -p bin
javac -encoding UTF-8 -cp "lib/flatlaf-3.5.4.jar:src" -d bin $(find src -name "*.java")
echo "[2/2] Iniciando Interface Grafica UniRide..."
java -cp "bin:lib/flatlaf-3.5.4.jar" br.com.caronas.Main
