package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import java.util.List;

/** Enlace promedio (UPGMA): promedio de todas las distancias entre miembros de A y de B. */
public class AverageLinkage implements CriterioEnlace {

    @Override
    public String nombre() {
        return "Average";
    }

    @Override
    public double distancia(List<Integer> clusterA, List<Integer> clusterB, double[][] distancias) {
        double suma = 0.0;
        for (int i : clusterA) {
            for (int j : clusterB) {
                suma += distancias[i][j];
            }
        }
        return suma / ((double) clusterA.size() * clusterB.size());
    }
}
