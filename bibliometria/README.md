# Análisis de algoritmos en el contexto de la bibliometría

Universidad del Quindío · Análisis de Algoritmos 2026-2

## Estructura
- `backend/`: API REST en Java + Spring Boot (corpus, algoritmos, servicios)
- `frontend/`: aplicación web (selección de artículos, resultados, dendrogramas, búsqueda)
- `docs/`: informe y casos de estudio
- `docker-compose.yml`: despliegue conjunto (R4)

## Cómo ejecutar
1. Copiar los 20 PDFs en `backend/data/corpus/`.
2. Backend: `cd backend && mvn spring-boot:run` (puerto 8080).
3. Frontend: ver `frontend/README.md`.

## Backend (`backend/src/main/java/co/edu/uniquindio/bibliometria`)
- `api`: controladores y DTOs
- `service`: orquestación
- `corpus`: ingesta y preprocesamiento
- `algoritmos`: núcleo (similitud, clustering, stringmatching, traza)
- `infra`: embeddings y medición
- `util`: utilidades matemáticas
