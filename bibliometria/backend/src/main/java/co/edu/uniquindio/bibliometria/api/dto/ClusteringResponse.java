package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/** Salida de POST /api/clustering. */
public record ClusteringResponse(List<Resultado> resultados) {

    public record Resultado(String criterio, Nodo arbol, double silhouette,
                            double correlacionCofenetica, long tiempoNs) {
    }

    public record Nodo(String articuloId, double altura, Nodo izquierda, Nodo derecha) {
    }
}
