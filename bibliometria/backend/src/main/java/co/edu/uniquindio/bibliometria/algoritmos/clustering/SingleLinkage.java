package co.edu.uniquindio.bibliometria.algoritmos.clustering;

/** Enlace simple: distancia mínima entre un miembro de A y uno de B. */
public class SingleLinkage implements CriterioEnlace {

    @Override
    public String nombre() {
        return "Single";
    }

    @Override
    public double distancia(int[] clusterA, int[] clusterB, double[][] distancias) {
        double minima = Double.POSITIVE_INFINITY;
        for (int i : clusterA) {
            for (int j : clusterB) {
                if (distancias[i][j] < minima) {
                    minima = distancias[i][j];
                }
            }
        }
        return minima;
    }
}
