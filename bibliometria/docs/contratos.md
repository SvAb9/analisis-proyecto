# Contratos del proyecto

Todo lo de esta página ya está escrito en el código (carpeta `backend/src/main/java/co/edu/uniquindio/bibliometria/`). Nadie debe cambiar una interfaz sin avisar al resto.

## Interfaces y quién las implementa

| Contrato | Ubicación | Implementa | Lo consume |
|---|---|---|---|
| `SimilitudStrategy` + `ResultadoSimilitud` | `algoritmos/similitud` | Natalia: Levenshtein, NeedlemanWunsch, JaccardSimilitud, CosenoTfIdf, SimilitudEmbeddingsOracion, SimilitudEmbeddingsPalabras | `SimilitudService` (Natalia), `ClusteringService` (Sara) |
| `EmbeddingProvider` | `algoritmos/similitud/ia` | Natalia: ModeloOracionProvider, Word2VecProvider | Los 2 algoritmos de IA |
| `CriterioEnlace` | `algoritmos/clustering` | Sara: SingleLinkage, CompleteLinkage, AverageLinkage | `ClusteringJerarquico` |
| `StringMatcher` + `ResultadoBusqueda` | `algoritmos/stringmatching` | Zuleta: KMP, BoyerMoore, RabinKarp | `BusquedaService`, `BenchmarkService` (Zuleta) |
| `RepositorioCorpus` + `Articulo` | `corpus` | Zuleta: CorpusRepository | Todos los servicios |
| `Preprocesador` | `corpus/preprocess` | Sara: PreprocesadorTexto | CorpusLoader (Zuleta), servicios |
| `Traza` + `Paso` | `algoritmos/traza` | Ya implementada | Todos los algoritmos |

## Reglas comunes

- Los algoritmos reciben texto ya preprocesado (`Articulo.abstractProcesado`); el patrón de búsqueda lo normaliza el servicio.
- Cada algoritmo recibe una `Traza`: si está activa registra sus pasos, si no, no hace nada.
- Nombres de algoritmo (`nombre()`) estables, porque son los que usa la API y el frontend: `Levenshtein`, `NeedlemanWunsch`, `Jaccard`, `CosenoTfIdf`, `EmbeddingsOracion`, `EmbeddingsPalabras`, `Single`, `Complete`, `Average`, `KMP`, `BoyerMoore`, `RabinKarp`.
- Probar sin el corpus: crear `Articulo` a mano en los tests.

## API REST (lo que el frontend puede asumir)

| Método y ruta | Entrada | Salida | Dueño |
|---|---|---|---|
| `GET /api/corpus` | — | Lista de `Articulo` (id, titulo, autores, abstractCrudo) | Zuleta |
| `GET /api/corpus/{id}` | — | Un `Articulo` | Zuleta |
| `GET /api/similitud/algoritmos` | — | Lista de nombres | Natalia |
| `POST /api/similitud` | `SimilitudRequest` | `SimilitudResponse` | Natalia |
| `POST /api/clustering` | `ClusteringRequest` | `ClusteringResponse` (un árbol por criterio) | Sara |
| `POST /api/busqueda` | `BusquedaRequest` | `BusquedaResponse` (un resultado por algoritmo) | Zuleta |
| `GET /api/benchmark?patron=...` | — | Tiempos de preprocesamiento y búsqueda por algoritmo | Zuleta |

Los campos exactos de cada petición y respuesta están en `api/dto/`.
