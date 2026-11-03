package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

/** Contrato para convertir un texto en su vector. */
public interface EmbeddingProvider {

    String modelo();

    int dimension();

    double[] vectorizar(String texto);
}
