package co.edu.uniquindio.bibliometria.algoritmos.similitud;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;

/**
 * Contrato común de los 6 algoritmos de similitud de R1 (4 clásicos y 2 de IA).
 * Implementan: Natalia (similitud/clasicos y similitud/ia).
 * Lo consume ClusteringService (Sara) para construir la matriz de distancias.
 *
 * Reglas:
 *  - Los textos llegan ya preprocesados (Articulo.abstractProcesado).
 *  - La similitud devuelta es simétrica y está en [0, 1].
 *  - Los algoritmos que necesiten el corpus (ej. TF-IDF) lo reciben por constructor
 *    (RepositorioCorpus), no lo buscan por su cuenta.
 *  - Sin librerías que implementen el algoritmo; solo estructuras básicas de Java.
 */
public interface SimilitudStrategy {

    /** Nombre único y estable, usado por la API (ej. "Levenshtein", "CosenoTfIdf"). */
    String nombre();

    /** Compara dos textos y registra pasos intermedios en la traza si está activa. */
    ResultadoSimilitud comparar(String textoA, String textoB, Traza traza);

    /** Comparación normal, sin traza. */
    default ResultadoSimilitud comparar(String textoA, String textoB) {
        return comparar(textoA, textoB, Traza.desactivada());
    }
}
