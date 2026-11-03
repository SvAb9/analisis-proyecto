#!/usr/bin/env bash
# Descarga MiniLM en data/modelos; con --glove regenera los vectores GloVe filtrados al corpus.
set -euo pipefail
DIR="$(cd "$(dirname "$0")/.." && pwd)/data/modelos"
HF=https://huggingface.co/sentence-transformers/all-MiniLM-L6-v2/resolve/main

mkdir -p "$DIR/all-MiniLM-L6-v2"
curl -fL -o "$DIR/all-MiniLM-L6-v2/model.onnx" "$HF/onnx/model.onnx"
curl -fL -o "$DIR/all-MiniLM-L6-v2/tokenizer.json" "$HF/tokenizer.json"

if [[ "${1:-}" == "--glove" ]]; then
  TMP="$(mktemp -d)"
  curl -fL -o "$TMP/glove.6B.zip" https://huggingface.co/stanfordnlp/glove/resolve/main/glove.6B.zip
  unzip -o -q "$TMP/glove.6B.zip" glove.6B.100d.txt -d "$TMP"
  python3 "$(dirname "$0")/filtrar_glove.py" "$TMP/glove.6B.100d.txt" \
    "$(dirname "$0")/vocabulario-corpus.txt" \
    "$DIR/glove/glove.6B.100d.corpus.txt"
  rm -rf "$TMP"
fi
echo "Modelos listos en $DIR"
