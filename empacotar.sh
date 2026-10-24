#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
versao="${1:-dev}"
if [[ ! "$versao" =~ ^(v[0-9]+\.[0-9]+\.[0-9]+|dev)$ ]]; then
  echo 'Versao invalida: use vMAJOR.MINOR.PATCH ou dev' >&2
  exit 1
fi
sha256sum --check dependencies.sha256
bash testar.sh
pacote="uniride-${versao}"
mkdir -p dist
stage="$(mktemp -d)"
trap 'rm -rf "$stage"' EXIT
mkdir -p "$stage/$pacote/lib"
printf 'Class-Path: lib/flatlaf-3.5.4.jar\nImplementation-Version: %s\n' "$versao" > "$stage/manifest.mf"
jar --create --file "$stage/$pacote/UniRide.jar" --main-class br.com.caronas.Main --manifest "$stage/manifest.mf" -C bin .
cp lib/flatlaf-3.5.4.jar "$stage/$pacote/lib/"
cp packaging/LEIA-ME.md "$stage/$pacote/"
cp packaging/iniciar.bat packaging/iniciar.sh "$stage/$pacote/"
chmod +x "$stage/$pacote/iniciar.sh"
java -ea -Djava.awt.headless=true -cp "$stage/$pacote/UniRide.jar:$stage/$pacote/lib/flatlaf-3.5.4.jar" br.com.caronas.test.Feature1Test
java -ea -Djava.awt.headless=true -cp "$stage/$pacote/UniRide.jar:$stage/$pacote/lib/flatlaf-3.5.4.jar" br.com.caronas.test.FullAppTest
taskRoot="$PWD"
(cd "$stage" && zip -qr "$taskRoot/dist/$pacote.zip" "$pacote")
(cd dist && sha256sum "$pacote.zip" > "$pacote.zip.sha256")
echo "Pacote criado: dist/$pacote.zip"
