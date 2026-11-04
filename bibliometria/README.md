# README.md

## Proyecto de Analítica Bibliométrica e Inteligencia Artificial
* **Asignatura:** Análisis de Algoritmos  
* **Programa:** Ingeniería de Sistemas y Computación - Universidad del Quindío  
* **Dominio de Conocimiento:** Inteligencia Artificial Generativa (Corpus de 20 Artículos Científicos)

---

## 1. Descripción General
Este proyecto implementa una plataforma web para el análisis computacional de información no estructurada a partir del abstract (resumen), títulos y autores de un corpus controlado de 20 artículos científicos en formato PDF. 

La aplicación integra preprocesamiento de lenguaje natural (NLP), algoritmos clásicos y modelos de Inteligencia Artificial para similitud textual, agrupamiento jerárquico aglomerativo (clustering) y búsqueda exacta de patrones (string matching) desarrollados desde cero.

---

## 2. Enlaces del Proyecto
* **Aplicación Desplegada:** https://bibliometria.astercol.com/corpus
* **Documentación Técnica:** https://docs.google.com/document/d/1DLltlXuWVvyznnT-58UiGrlKjgB2r3g7a_PnOtOV_0o/edit?tab=t.0
* **Repositorio de Código:** https://github.com/SvAb9/analisis-proyecto/tree/main/bibliometria

---

## 3. Requerimientos Funcionales e Implementación

### Preprocesamiento de Texto (`com.bibliometria.preprocessing`)
Antes de someter los textos a los algoritmos, los abstracts son procesados mediante una canalización (pipeline) propia:
* **TextNormalizer:** Conversión a minúsculas, remoción de tildes/acentos (NFD) y eliminación de caracteres especiales.
* **StopwordsRemover:** Filtrado de palabras huecas/conectores en español e inglés.
* **Lemmatizer:** Reducción de términos a sus formas canónicas para la vectorización (TF-IDF).

---

### Módulos Algorítmicos

#### Requerimiento 1: Análisis de Similitud Textual
Permite la comparación dinámica entre 2 o más artículos mediante 6 enfoques distintos:
* **Algoritmos Clásicos (Desde Cero):**
  1. Distancia de Levenshtein (Programación Dinámica).
  2. Algoritmo de Needleman-Wunsch (Alineamiento de secuencias / PD).
  3. Similitud del Coseno basada en pesos TF-IDF.
  4. Coeficiente de Jaccard (Basado en teoría de conjuntos).
* **Modelos de Inteligencia Artificial:**
  5. Vectorización densa (Embeddings) + Distancia Coseno.
  6. Vectorización densa (Embeddings) + Distancia Euclidiana.

#### Requerimiento 2: Agrupamiento Jerárquico Aglomerativo (Clustering)
Construcción interactiva de dendrogramas que representan la similitud entre los 20 artículos:
* **Criterios de Enlace Implementados (Desde Cero):**
  * **Single Linkage:** Distancia mínima entre clústeres.
  * **Complete Linkage:** Distancia máxima entre clústeres.
  * **Average Linkage:** Distancia promedio entre miembros de clústeres.
* **Representación Visual:** Generación automática del árbol dendrograma.
* **Evaluación Interna:** Métrica matemática de validación de cohesión y separación clúster.

#### Requerimiento 3: Búsqueda de Patrones de Texto (String Matching)
Localización exacta de cadenas de texto en los abstracts sin utilizar funciones nativas del lenguaje (`indexOf`, `find`):
1. **Knuth-Morris-Pratt (KMP):** Tabla de prefijos/sufijos (LPS) para evitar retrocesos.
2. **Boyer-Moore:** Heurística de desplazamiento basada en la regla del carácter malo.
3. **Rabin-Karp:** Cálculo numérico iterativo con Rolling Hash y aritmética modular.

#### Requerimiento 4: Despliegue y Documentación Técnica
El proyecto cuenta con soporte integral de infraestructura y análisis formal:
* **Despliegue en la Nube:** Aplicación web fully-functional accesible públicamente.
* **Documento Técnico de Diseño:** Documentación completa que incluye la arquitectura del sistema, demostraciones matemáticas paso a paso sobre casos de estudio concretos para cada algoritmo, comparativas de complejidad teórica ($O$) frente a rendimiento empírico, y la fundamentación explícita del uso de IA Generativa.

---

## 4. Arquitectura y Tecnologías

El proyecto sigue una arquitectura desacoplada y estructurada por capas:

* **Backend:** Java + Spring Boot (Patrón Controller-Service-Repository).
* **Frontend:** React / Vue / Angular / HTML-JS con visualización dinámica de dendrogramas.
* **Base de Datos:** H2 / PostgreSQL / MySQL para el almacenamiento y filtrado de metadatos (Req. 2: Rango de Años, Autores y Keywords).
