package co.edu.uniquindio.bibliometria.algoritmos.clustering.evaluacion;

import co.edu.uniquindio.bibliometria.algoritmos.clustering.Dendrograma;

/** Coeficiente de silueta de un corte del dendrograma. */
public final class Silhouette {

    public record Corte(int k, double silhouette) {
    }

    private Silhouette() {
    }

    public static double calcular(double[][] distancias, int[][] clusters) {
        if (clusters.length < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 clústeres");
        }
        double suma = 0.0;
        int total = 0;
        for (int c = 0; c < clusters.length; c++) {
            int[] propio = clusters[c];
            for (int i : propio) {
                total++;
                if (propio.length == 1) {
                    continue;
                }
                double a = 0.0;
                for (int j : propio) {
                    if (j != i) {
                        a += distancias[i][j];
                    }
                }
                a /= (propio.length - 1);

                double b = Double.POSITIVE_INFINITY;
                for (int o = 0; o < clusters.length; o++) {
                    if (o == c) {
                        continue;
                    }
                    double media = 0.0;
                    for (int j : clusters[o]) {
                        media += distancias[i][j];
                    }
                    media /= clusters[o].length;
                    if (media < b) {
                        b = media;
                    }
                }
                double max = Math.max(a, b);
                suma += (max == 0.0) ? 0.0 : (b - a) / max;
            }
        }
        return suma / total;
    }

    public static Corte mejorCorte(double[][] distancias, Dendrograma dendrograma) {
        int n = dendrograma.ids().size();
        if (n < 3) {
            throw new IllegalArgumentException("Se necesitan al menos 3 artículos");
        }
        Corte mejor = null;
        for (int k = 2; k <= n - 1; k++) {
            double s = calcular(distancias, dendrograma.cortar(k));
            if (mejor == null || s > mejor.silhouette()) {
                mejor = new Corte(k, s);
            }
        }
        return mejor;
    }
}
