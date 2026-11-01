package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/** Salida de POST /api/clustering: un resultado por cada criterio de enlace. */
public record ClusteringResponse(List<Resultado> resultados) {

    /**
     * @param criterio              "Single", "Complete" o "Average"
     * @param arbol                 raíz del dendrograma
     * @param silhouette            métrica interna de coherencia
     * @param correlacionCofenetica fidelidad del árbol a las distancias originales
     * @param tiempoNs              tiempo de ejecución del agrupamiento
     */
    public record Resultado(String criterio, Nodo arbol, double silhouette,
                            double correlacionCofenetica, long tiempoNs) {
    }

    /**
     * Nodo del dendrograma. Hoja: articuloId != null y sin hijos.
     * Nodo interno: articuloId == null, con izquierda y derecha, y altura = distancia de fusión.
     */
    public record Nodo(String articuloId, double altura, Nodo izquierda, Nodo derecha) {
    }
}
