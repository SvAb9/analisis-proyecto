package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import java.util.List;

/**
 * Contrato de un criterio de enlace (cómo se mide la distancia entre dos clústeres).
 * Implementan: SingleLinkage, CompleteLinkage y AverageLinkage (Sara).
 * Lo usa ClusteringJerarquico en su bucle aglomerativo.
 */
public interface CriterioEnlace {

    /** Nombre para la API y el informe (ej. "Single", "Complete", "Average"). */
    String nombre();

    /**
     * Distancia entre dos clústeres.
     *
     * @param clusterA índices (filas de la matriz) de los artículos del primer clúster
     * @param clusterB índices de los artículos del segundo clúster
     * @param distancias matriz simétrica de distancias entre artículos (diagonal en 0)
     */
    double distancia(List<Integer> clusterA, List<Integer> clusterB, double[][] distancias);
}
