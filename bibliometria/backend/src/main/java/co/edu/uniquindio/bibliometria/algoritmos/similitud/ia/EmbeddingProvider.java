package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

/**
 * Contrato para obtener embeddings (vectores densos) de un texto.
 * Implementan: ModeloOracionProvider y Word2VecProvider (Natalia, en infra/embeddings).
 * Lo usan SimilitudEmbeddingsOracion y SimilitudEmbeddingsPalabras.
 */
public interface EmbeddingProvider {

    /** Nombre del modelo, para el informe (ej. "MiniLM-L6", "Word2Vec-es"). */
    String modelo();

    /** Tamaño del vector que devuelve vectorizar(...). */
    int dimension();

    /** Convierte un texto en su vector. Debe devolver siempre un arreglo de longitud dimension(). */
    double[] vectorizar(String texto);
}
