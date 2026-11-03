package co.edu.uniquindio.bibliometria.algoritmos.clustering;

/** Criterio que mide la distancia entre dos clústeres. */
public interface CriterioEnlace {

    String nombre();

    double distancia(int[] clusterA, int[] clusterB, double[][] distancias);
}
