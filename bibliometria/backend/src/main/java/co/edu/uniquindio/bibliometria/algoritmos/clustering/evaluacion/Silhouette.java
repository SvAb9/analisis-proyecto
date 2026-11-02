package co.edu.uniquindio.bibliometria.algoritmos.clustering.evaluacion;

import co.edu.uniquindio.bibliometria.algoritmos.clustering.Dendrograma;
import java.util.List;

/**
 * Coeficiente de silueta (métrica interna). Para cada artículo i:
 * a(i) = distancia media a los demás de su clúster, b(i) = menor distancia media a otro clúster,
 * s(i) = (b - a) / max(a, b). Un clúster de un solo artículo vale 0.
 * El resultado es el promedio de s(i): cerca de 1 = clústeres compactos y bien separados.
 */
public final class Silhouette {

    /** Silueta obtenida al cortar el dendrograma en k clústeres. */
    public record Corte(int k, double silhouette) {
    }

    private Silhouette() {
    }

    public static double calcular(double[][] distancias, List<List<Integer>> clusters) {
        if (clusters.size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 clústeres");
        }
        double suma = 0.0;
        int total = 0;
        for (int c = 0; c < clusters.size(); c++) {
            List<Integer> propio = clusters.get(c);
            for (int i : propio) {
                total++;
                if (propio.size() == 1) {
                    continue; // s(i) = 0
                }
                double a = 0.0;
                for (int j : propio) {
                    if (j != i) {
                        a += distancias[i][j];
                    }
                }
                a /= (propio.size() - 1);

                double b = Double.POSITIVE_INFINITY;
                for (int o = 0; o < clusters.size(); o++) {
                    if (o == c) {
                        continue;
                    }
                    double media = 0.0;
                    for (int j : clusters.get(o)) {
                        media += distancias[i][j];
                    }
                    media /= clusters.get(o).size();
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

    /** Prueba todos los cortes de k = 2 a n - 1 y devuelve el de mayor silueta (empate: menor k). */
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
