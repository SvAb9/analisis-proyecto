#!/usr/bin/env python3
"""Deja en un archivo GloVe solo las palabras del corpus: filtrar_glove.py glove.txt vocabulario.txt salida.txt"""
import sys


def main(glove, vocabulario, salida):
    with open(vocabulario, encoding="utf-8") as archivo:
        palabras = {linea.strip() for linea in archivo if linea.strip()}
    escritas = 0
    with open(glove, encoding="utf-8") as entrada, open(salida, "w", encoding="utf-8") as out:
        for linea in entrada:
            if linea.split(" ", 1)[0] in palabras:
                out.write(linea)
                escritas += 1
    print(f"vocabulario del corpus: {len(palabras)}, con vector: {escritas}")


if __name__ == "__main__":
    main(*sys.argv[1:4])
