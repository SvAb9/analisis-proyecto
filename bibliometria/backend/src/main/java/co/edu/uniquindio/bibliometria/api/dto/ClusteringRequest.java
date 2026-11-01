package co.edu.uniquindio.bibliometria.api.dto;

/**
 * Entrada de POST /api/clustering.
 *
 * @param algoritmoSimilitud nombre del SimilitudStrategy con que se arma la matriz de distancias
 *                           (ej. "CosenoTfIdf")
 */
public record ClusteringRequest(String algoritmoSimilitud) {
}
