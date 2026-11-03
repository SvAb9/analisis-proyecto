# Reporte de trabajo: R1 (similitud textual) — parte de Natalia

Fecha: 2026-11-01 · Rama actual: `feature/similitud` (sin commit; los cambios están en el árbol de trabajo para que los revises antes de hacer commit y pull request).

> **Actualización (corrección "mvn: instrucción no encontrada" y error de compilación).** Ver la sección 0.

## 0. Corrección posterior: adaptación a los contratos de Sara

Al correr el proyecto aparecieron dos problemas:

1. **`mvn: instrucción no encontrada`**: Maven no está instalado en el sistema. Se agregó el **Maven Wrapper** (`backend/mvnw`, `mvnw.cmd`, `.mvn/wrapper/`), que descarga Maven 3.9.11 la primera vez. Ahora se corre con `./mvnw spring-boot:run` y no hay que instalar nada (solo Java 21+).
2. **Error de compilación en `StringMatcher` (`Traza.desactivada()` no existe)**: la rama `feature/similitud` trae el commit `c525d66` de Sara con los contratos compartidos (`docs/contratos.md`). Mi trabajo se había hecho sobre el esqueleto anterior y, al aplicarlo encima, **reemplazó sus versiones** de `Traza`, `Paso`, `SimilitudStrategy`, `ResultadoSimilitud`, `EmbeddingProvider`, `SimilitudRequest`, `SimilitudResponse` y `TrazaDTO`.

Qué se hizo:

- Se **restauraron los 8 archivos de Sara exactamente como están en `c525d66`** (no se les cambió nada) y se adaptó R1 a ellos:
  - `nombre()` con los nombres estables del contrato: `Levenshtein`, `NeedlemanWunsch`, `Jaccard`, `CosenoTfIdf`, `EmbeddingsOracion`, `EmbeddingsPalabras`; el método es `comparar(a, b, traza)`.
  - `Traza.activada()/desactivada()` con `registrar(descripcion, datos)`. Como esa Traza no tiene límite, Levenshtein y Needleman-Wunsch registran solo las celdas con i, j ≤ 25 (si no, una traza de dos abstracts completos tendría más de un millón de pasos).
  - `EmbeddingProvider` (`modelo()`, `dimension()`, `double[] vectorizar()`): un proveedor sin modelo cargado informa dimensión 0. La cobertura de GloVe y las ventanas de MiniLM quedan en `vectorizarConDetalle()` de cada proveedor.
  - `CosenoTfIdf` recibe el `RepositorioCorpus` por constructor, como pide el contrato.
  - `SimilitudService` recibe `{ids, algoritmos, conTraza}` y devuelve `{pares: [{idA, idB, resultados}]}`. `GET /api/similitud/algoritmos` devuelve la lista de nombres.
  - La pantalla Angular arma en el cliente las matrices y el resumen clásicos vs IA a partir de los pares, y lee el corpus de `GET /api/corpus`.
- Se eliminó `FuenteCorpus` (lo reemplaza `RepositorioCorpus`).
- **`corpus/Articulo` estaba vacío** también en el commit de Sara, pero `RepositorioCorpus` lo devuelve. Se definió con los campos que ya documentan el esqueleto y `contratos.md`: `id, titulo, autores (List<String>), abstractCrudo, abstractProcesado`, más `textoParaAlgoritmos()` (procesado si existe, si no crudo). **Avisar a Zuleta**: es su archivo; si prefiere otros tipos (p. ej. `autores` como String) se ajusta fácil.
- **Corpus**: el servicio recibe `CorpusRepository`, que carga los 20 PDF. MiniLM recibe el abstract crudo; los demás algoritmos, el normalizado.
- Validado: 41 pruebas pasan con `./mvnw test`, sin omitidas. La app arranca con `./mvnw spring-boot:run` y responde los endpoints con el formato del contrato. El caso de estudio da **exactamente los mismos números** que ya estaban en el informe.
- En el informe solo cambió el texto de la sección 5.1 (API, traza, corpus por constructor); ninguna cifra.

Pendientes que deja este cambio:

- **El modelo MiniLM quedó dentro de `stash@{0}`** (`git stash show --stat stash@{0}` lo muestra como agregado). Ya se volvió a descargar con `backend/scripts/descargar-modelos.sh`. **No hagas `git stash pop` de ese stash sin revisarlo**, y no hagas commit de `model.onnx` (90 MB; está en `.gitignore`).
- **Texto procesado vs. crudo**: el contrato dice que los algoritmos reciben `abstractProcesado`. Si el preprocesamiento de Sara aplica *stemming*, los modelos de IA empeoran ("assessment" → "assess" puede no tener vector en GloVe, y MiniLM espera texto natural). Proponer al equipo que los dos algoritmos de IA usen `abstractCrudo`; el caso de estudio del informe usa los abstracts sin procesar.
- La pantalla espera `GET /api/corpus` (Zuleta); mientras no exista, el backend de R1 funciona (probado con curl) pero la página no puede listar artículos.


## 1. Alcance

Según `División del trabajo Análisis de algoritmos en bibliometría.txt`, a Natalia le corresponde:

| Bloque | Estado |
|---|---|
| `similitud/clasicos` (Levenshtein, Needleman-Wunsch, Jaccard, Coseno TF-IDF) | Implementado y probado |
| `similitud/ia` (2 modelos de embeddings) | Implementado y probado (MiniLM + GloVe) |
| `infra/embeddings`, `util/VectorMath` | Implementado |
| `SimilitudService`, `SimilitudController` | Implementado; endpoint probado con curl sobre la app real |
| `frontend/pages/similitud` | Componente Angular standalone; compila con `ngc --strictTemplates` |
| Informe: secciones 3, 4, 5 y 11 | Escritas en `Informe proyecto final analisis.docx` |

No se tocó R2, R3, corpus, despliegue ni las secciones de los demás.

## 2. Qué se hizo

### Backend (`backend/src/main/java/co/edu/uniquindio/bibliometria/`)

| Archivo | Contenido |
|---|---|
| `util/estructuras/` (nuevo) | `Nodo`, `ListaEnlazada`, `Pila`, `Cola`, `NodoArbol`, `ArbolBinarioBusqueda`, basadas en los modelos del curso |
| `algoritmos/similitud/PreparadorTexto` (nuevo) | Normalización y tokenización carácter por carácter, stopwords en un ABB |
| `algoritmos/similitud/clasicos/*` | Los 4 clásicos + `TfIdfVectorizador` |
| `algoritmos/similitud/ia/*` | `EmbeddingProvider`, base común `SimilitudEmbeddings` (nueva), los 2 de IA |
| `infra/embeddings/*` | `ModeloOracionProvider` (ONNX), `Word2VecProvider` (GloVe), `EmbeddingCache` |
| `util/VectorMath` | Producto punto, norma, coseno, euclidiana, normalizar |
| `service/SimilitudService` | Orquestación |
| `api/SimilitudController`, `api/dto/Similitud*` | `GET /api/similitud/algoritmos`, `POST /api/similitud` |
| `resources/similitud/stopwords-en.txt` (nuevo) | 167 stopwords en inglés |

### Pruebas (`backend/src/test/`)

- 41 pruebas unitarias (tras la sección 0) con resultados conocidos (p. ej. Levenshtein("kitten", "sitting") = 3, IDF calculado a mano, cosenos de vectores fijos). Todas pasan.
- `ModeloOracionProviderTest`: comprueba que nuestro pooling y normalización reproducen los valores publicados por sentence-transformers para all-MiniLM-L6-v2 (0,6660 / 0,1046 / 0,1411). Coinciden con error < 0,0005.
- `CasoEstudioR1`: generador del caso de estudio. Se ejecuta con `mvn test -Dtest=CasoEstudioR1 -Dcaso.estudio=true` y escribe `docs/casos-estudio/r1-similitud.md` y `.json`. **Todas las cifras de la sección 5.4–5.5 del informe salen de aquí**, no se escribieron a mano.

### Modelos

- `backend/data/modelos/glove/glove.6B.100d.corpus.txt` (0,9 MB, **sí se versiona**): GloVe 6B 100d filtrado a las 1 114 palabras del corpus (1 096 tienen vector).
- `backend/data/modelos/all-MiniLM-L6-v2/` (~90 MB, **no se versiona**, está en `.gitignore`): se descarga con `backend/scripts/descargar-modelos.sh`. Ya está descargado en tu máquina.
- `backend/scripts/filtrar_glove.py`: regenera el subconjunto de GloVe (`descargar-modelos.sh --glove`).

### Frontend

`frontend/src/app/pages/similitud/similitud.component.ts`: selección de artículos con filtro, selección de algoritmos (separados en clásicos/IA, deshabilitados si el modelo no está cargado), tabla de resultados con barra para 2 artículos, matrices con escala de color para 3 o más, tabla "clásicos frente a IA" y pasos intermedios desplegables.

### Informe

Secciones modificadas en `Informe proyecto final analisis.docx` (el original quedó como `Informe proyecto final analisis (respaldo antes de R1).docx` en la misma carpeta):

- **3.2**: nueva fila `util/estructuras/` en la tabla del backend.
- **3.4 (nueva)**: estructuras de datos utilizadas, complejidad de cada operación y por qué.
- **4**: la fila "Embeddings (IA)" ya no dice "Por definir".
- **5.1 a 5.5**: completas (diseño, los 4 clásicos con fórmula, pseudocódigo y complejidad, los 2 modelos de IA, caso de estudio paso a paso con matrices y tablas, análisis crítico). Al final de la 5.5, un párrafo corto de uso de IA generativa en R1.
- **11**: referencias bibliográficas de los algoritmos y modelos + los 20 artículos del corpus en formato APA (autores, año y revista tomados de cada PDF).

## 3. Decisiones y por qué

1. **Estructuras del curso antes que colecciones de Java.** Lo pediste y además el enunciado exige que el comportamiento sea "transparente y analizable". Lista enlazada con cabecera y `ultimo` (inserción al final O(1)), pila con `tope`, cola con `inicio`/`fin` y ABB, tal como en *Estructuras de Datos.pdf*. Se hicieron genéricas (`<T>`, `<K,V>`) y se agregó un valor por nodo al ABB para usarlo como diccionario. Cada una tiene un uso real, no decorativo:
   - **ABB**: conjuntos de Jaccard, frecuencias TF/DF, stopwords, vocabulario GloVe, caché. Se eligió sobre una tabla hash porque el **inorden da las claves ordenadas** y eso permite intersecar conjuntos y hacer el producto punto TF-IDF con un recorrido de mezcla O(|A|+|B|).
   - **Pila**: reconstrucción del alineamiento de Needleman-Wunsch (va de atrás hacia adelante) y el inorden iterativo.
   - **Cola**: tareas (algoritmo, par) en el servicio.
   - **ListaEnlazada**: traza de pasos, vectores dispersos, listas ordenadas.
   - **Arreglos** solo donde la complejidad lo exige: matrices de programación dinámica y vectores densos (acceso O(1) por índice). Está justificado en la sección 3.4.
2. **`rebalancear()` en el ABB.** El archivo de stopwords está en orden alfabético y el árbol se degeneraba en una lista (altura 166). Es el peor caso que muestra el material del curso, así que se documentó como hallazgo y se resolvió reconstruyendo por medianas (altura 9 para 1 023 claves, hay prueba).
3. **Levenshtein a nivel de carácter; Needleman-Wunsch a nivel de palabra.** Así los dos de programación dinámica no son redundantes y cada uno usa su definición más habitual. Levenshtein usa dos filas (espacio Θ(m)) cuando no hay traza.
4. **IDF suavizado** `ln((1+N)/(1+df)) + 1`. Con el IDF clásico, al comparar dos textos sin corpus todo término común tiene idf 0 y el coseno siempre da 0.
5. **Escala [0, 1] común** en el contrato, para mostrar los seis juntos y que R2 use cualquiera como distancia `1 − s`. El coseno negativo de los embeddings se recorta a 0 (función monótona; no altera el orden).
6. **Embeddings locales, sin API** (lo elegiste): MiniLM en ONNX y GloVe. La app desplegada no depende de claves ni de internet. Las librerías solo tokenizan y ejecutan la red; mean pooling, normalización, promedio de palabras, coseno y euclidiana están escritos a mano.
7. **GloVe filtrado al corpus**: el corpus es fijo (20 PDF), así que se guardan solo sus palabras (0,9 MB en vez de 822 MB). Desventaja: si se agregara un artículo nuevo habría que regenerar el archivo.
8. **Caso de estudio: artículos 11 y 16.** Mismo tema (IA generativa y evaluación) con vocabulario distinto: es el par donde clásicos e IA deberían discrepar, lo que da material para el análisis crítico.
9. **Contexto de 190 pares.** Además del par pedido se ejecutaron los seis algoritmos sobre los 190 pares del corpus (media, rango, puesto del par y correlación de Spearman). Sin eso, comparar 0,07 de Needleman-Wunsch con 0,93 de GloVe no dice nada.

## 4. Resultados principales (par 11–16)

| Algoritmo | Similitud | Puesto entre 190 pares |
|---|---|---|
| Levenshtein | 0,2985 | 48 |
| Needleman-Wunsch | 0,0684 | 44 |
| Jaccard | 0,0863 | 104 |
| Coseno TF-IDF | 0,1864 | 15 |
| MiniLM | 0,7355 | 9 |
| GloVe promedio | 0,9349 | 179 |

Conclusión del análisis crítico: MiniLM es el que mejor reconoce el tema común y TF-IDF es el mejor clásico (Spearman 0,66 entre ambos). El promedio de GloVe casi no discrimina (todos los pares entre 0,92 y 0,98), así que un modelo de IA mal aplicado puede rendir peor que un clásico bien ponderado. **Recomendación para R2: matriz de distancias con Coseno TF-IDF o MiniLM.**

## 5. Puntos a tener en cuenta

### Archivos compartidos que se tocaron (avisar a Sara)

La división dice que estos los toca solo Sara. Hubo que llenarlos porque estaban vacíos y R1 no compila ni corre sin ellos. Conviene que ella los revise y los acepte o ajuste en el PR:

- ~~Contratos (`SimilitudStrategy`, `ResultadoSimilitud`, `Traza`, `Paso`, `TrazaDTO`, DTOs de similitud)~~: ya **no** se modifican; se usan los de Sara (ver sección 0).
- `corpus/Articulo`: definido con los campos documentados (es de Zuleta; ver sección 0).
- `BibliometriaApplication`: solo se agregó `@SpringBootApplication` y `main`.
- `pom.xml`: dependencias `onnxruntime 1.20.0` y `ai.djl.huggingface:tokenizers 0.30.0`.
- `application.yml`: claves `app.modelos.oracion` y `app.modelos.palabras`.
- `.gitignore` y `README.md` del proyecto: una línea y una sección de R1.

### Integración pendiente

- **Corpus (Zuleta)**: el frontend usa `GET /api/corpus` (`id`, `titulo`), como dice `contratos.md`.
- **IDF sobre los 20 abstracts**: resuelto con `RepositorioCorpus` (o el corpus temporal); para 11–16 da 0,1864, igual que en el informe.
- **R2 (Sara)**: `SimilitudService.estrategia("CosenoTfIdf")` o `"EmbeddingsOracion"` devuelve la estrategia lista para usar; distancia = `1 − similitud`.
- **Frontend**: el componente hace las llamadas HTTP él mismo (marcadas con `// API`) porque `api.service.ts` es de Sara; moverlas allá al integrar. Falta registrar la ruta y crear el proyecto Angular (`ng new`), que no existe aún. En desarrollo hace falta CORS (`CorsConfig`, de Sara) o un proxy de Angular hacia `localhost:8080`.
- **Preprocesamiento**: R1 usa su propio `PreparadorTexto` para no depender de `corpus/preprocess`. Si se unifica, cuidar que no haga stemming antes de GloVe/MiniLM (cambia las palabras y pierden vector).

### Despliegue (Zuleta)

- El JAR pesa ~140 MB porque ONNX Runtime trae binarios nativos de varias plataformas.
- El Dockerfile copia `data/`; el modelo MiniLM tiene que estar descargado antes de construir la imagen (correr `scripts/descargar-modelos.sh` en el build o en CI). Si falta, la app arranca igual y los algoritmos de IA aparecen como "no disponible".
- Se compiló y probó con JDK 25 (`release 21`) y Maven 3.9.11 a través de `./mvnw`.

### Antes de entregar (para ti)

- **Revisa y entiende cada cálculo de la sección 5.** La sustentación es individual y el enunciado exige que la IA no reemplace el análisis formal. Las matrices de las tablas 5.1 y 5.2 se pueden rehacer a mano en papel: vale la pena hacerlo.
- **Declaración de IA**: el párrafo al final de la 5.5 dice que Claude (Claude Code) se usó para el código, las pruebas, el generador del caso de estudio y un borrador del informe. Pásale a Zuleta lo que corresponda para la sección 9 y ajusta el texto si tu uso fue distinto.
- Las secciones 3 y 4 ya tenían texto del equipo; solo se agregó (3.4, fila nueva, fila de embeddings). La fila "IA generativa" de la tabla 4 la dejé como estaba.
- Los tiempos (ms por par) se midieron en este portátil; cambiarán en otra máquina o en la nube.
- Los números usan coma decimal en el informe y punto en el código y en `r1-similitud.md`.
- Hacer commit en la rama de R1 y abrir el pull request hacia `main` cuando lo hayas revisado.

### Posibles mejoras (no hechas)

- Embeddings de palabras con ponderación SIF o restando el vector medio del corpus: probablemente harían que GloVe sí discrimine. No se aplicó para usar el modelo en su forma estándar; está mencionado como trabajo futuro.
- Persistir la caché de embeddings en `data/cache` (hoy vive en memoria; con 20 abstracts no hace falta).
