package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import java.util.List;

/** Enlace completo: distancia máxima entre un miembro de A y uno de B. */
public class CompleteLinkage implements CriterioEnlace {

    @Override
    public String nombre() {
        return "Complete";
    }

    @Override
    public double distancia(List<Integer> clusterA, List<Integer> clusterB, double[][] distancias) {
        double maxima = Double.NEGATIVE_INFINITY;
        for (int i : clusterA) {
            for (int j : clusterB) {
                if (distancias[i][j] > maxima) {
                    maxima = distancias[i][j];
                }
            }
        }
        return maxima;
    }
}
