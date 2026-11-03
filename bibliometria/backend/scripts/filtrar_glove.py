#!/usr/bin/env python3
"""Filtra un archivo GloVe/word2vec en texto dejando solo las palabras del corpus.

Uso: python3 filtrar_glove.py glove.6B.100d.txt abstracts.json salida.txt

El vocabulario son todas las palabras (letras y dígitos, en minúscula) de los títulos y
abstracts del JSON. Se conserva el orden original del archivo GloVe (por frecuencia).
"""
import json
import sys


def palabras(texto):
    actual = []
    for c in texto.lower():
        if c.isalnum():
            actual.append(c)
        elif actual:
            yield "".join(actual)
            actual = []
    if actual:
        yield "".join(actual)


def main(glove, corpus, salida):
    vocabulario = set()
    for articulo in json.load(open(corpus, encoding="utf-8")):
        vocabulario.update(palabras(articulo["titulo"] + " " + articulo["abstract"]))
    escritas = 0
    with open(glove, encoding="utf-8") as entrada, open(salida, "w", encoding="utf-8") as out:
        for linea in entrada:
            if linea.split(" ", 1)[0] in vocabulario:
                out.write(linea)
                escritas += 1
    print(f"vocabulario del corpus: {len(vocabulario)}, con vector: {escritas}")


if __name__ == "__main__":
    main(*sys.argv[1:4])
