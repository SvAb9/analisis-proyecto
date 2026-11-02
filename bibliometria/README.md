# Análisis de algoritmos en el contexto de la bibliometría

Universidad del Quindío · Análisis de Algoritmos 2026-2

## Estructura
- `backend/`: API REST en Java + Spring Boot (corpus, algoritmos, servicios)
- `frontend/`: aplicación web (selección de artículos, resultados, dendrogramas, búsqueda)
- `docs/`: informe y casos de estudio
- `docker-compose.yml`: despliegue conjunto (R4)

## Cómo ejecutar
1. Copiar los 20 PDFs en `backend/data/corpus/`.
2. Backend: `cd backend && ./mvnw spring-boot:run` (puerto 8080). No hace falta instalar Maven: el wrapper
   `mvnw` lo descarga la primera vez (en Windows, `mvnw.cmd`). Requiere Java 21 o superior.
3. Frontend: ver `frontend/README.md`.

## Backend (`backend/src/main/java/co/edu/uniquindio/bibliometria`)
- `api`: controladores y DTOs
- `service`: orquestación
- `corpus`: ingesta y preprocesamiento
- `algoritmos`: núcleo (similitud, clustering, stringmatching, traza)
- `infra`: embeddings y medición
- `util`: utilidades matemáticas

## R1: similitud textual

Modelos de IA (una vez, ~90 MB; los vectores GloVe filtrados ya vienen en el repositorio):

```bash
backend/scripts/descargar-modelos.sh
```

- `GET  /api/similitud/algoritmos`: nombres de los algoritmos disponibles (los de IA solo si su modelo está cargado).
- `POST /api/similitud`: `{"ids":["11","16"], "algoritmos":["CosenoTfIdf"], "conTraza":false}` (ver `docs/contratos.md`).
  Con `algoritmos` vacío se ejecutan todos los disponibles.
- Caso de estudio del informe: `cd backend && ./mvnw test -Dtest=CasoEstudioR1 -Dcaso.estudio=true`
  (escribe `docs/casos-estudio/r1-similitud.md` y `.json`).
- Estructuras de datos basadas en nodos (lista, pila, cola, ABB) en `util/estructuras/`, reutilizables por R2 y R3.
