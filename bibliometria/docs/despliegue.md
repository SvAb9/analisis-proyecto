# Despliegue (R4)

La aplicación son dos contenedores: `backend` (Spring Boot, con los 20 PDF y los modelos de R1) y `frontend` (nginx sirve Angular y reenvía `/api` al backend). Solo el frontend queda expuesto.

## En cualquier servidor con Docker

```
git clone https://github.com/SvAb9/analisis-proyecto.git
cd analisis-proyecto/bibliometria
docker compose up -d --build
```

La primera construcción compila los dos proyectos y descarga el modelo de oración (~90 MB). La aplicación queda en `http://<ip-del-servidor>` (puerto 80).

Comprobar: `curl http://localhost/api/corpus` debe devolver 20 artículos.

## Variables

| Variable | Por defecto | Para qué |
|---|---|---|
| `CORPUS_PATH` | `data/corpus` | Carpeta de los PDF; si no existe o está vacía, el backend no arranca |
| `MODELO_ORACION_PATH` | `data/modelos/all-MiniLM-L6-v2` | Modelo ONNX de R1; si falta, ese algoritmo no aparece en la lista |
| `MODELO_PALABRAS_PATH` | `data/modelos/glove/glove.6B.100d.corpus.txt` | Vectores GloVe de R1 |
