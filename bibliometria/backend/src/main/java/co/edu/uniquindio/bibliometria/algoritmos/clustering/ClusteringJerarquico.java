package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import java.util.Locale;

/** Agrupamiento jerárquico aglomerativo con el criterio de enlace dado. */
public final class ClusteringJerarquico {

    private final CriterioEnlace criterio;

    public ClusteringJerarquico(CriterioEnlace criterio) {
        this.criterio = criterio;
    }

    public Dendrograma agrupar(MatrizDistancias matriz) {
        return agrupar(matriz, Traza.desactivada());
    }

    public Dendrograma agrupar(MatrizDistancias matriz, Traza traza) {
        int n = matriz.tamano();
        if (n < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 artículos");
        }
        double[][] d = matriz.valores();

        int[][] clusters = new int[n][];
        NodoDendrograma[] nodos = new NodoDendrograma[n];
        for (int i = 0; i < n; i++) {
            clusters[i] = new int[] {i};
            nodos[i] = NodoDendrograma.hoja(i, matriz.id(i));
        }

        Dendrograma.Fusion[] fusiones = new Dendrograma.Fusion[n - 1];
        int activos = n;
        int paso = 1;
        while (activos > 1) {
            int mejorI = -1;
            int mejorJ = -1;
            double mejor = Double.POSITIVE_INFINITY;
            for (int i = 0; i < activos; i++) {
                for (int j = i + 1; j < activos; j++) {
                    double dist = criterio.distancia(clusters[i], clusters[j], d);
                    if (dist < mejor) {
                        mejor = dist;
                        mejorI = i;
                        mejorJ = j;
                    }
                }
            }

            int[] a = clusters[mejorI];
            int[] b = clusters[mejorJ];
            traza.registrar(
                    "Paso " + paso + " (" + criterio.nombre() + "): fusionar " + nombre(matriz, a)
                            + " y " + nombre(matriz, b),
                    "distancia = " + String.format(Locale.ROOT, "%.4f", mejor)
                            + ", clústeres restantes = " + (activos - 1));

            fusiones[paso - 1] = new Dendrograma.Fusion(paso, a, b, mejor);
            nodos[mejorI] = NodoDendrograma.fusion(nodos[mejorI], nodos[mejorJ], mejor);
            clusters[mejorI] = NodoDendrograma.unir(a, b);
            for (int k = mejorJ; k < activos - 1; k++) {
                clusters[k] = clusters[k + 1];
                nodos[k] = nodos[k + 1];
            }
            activos--;
            paso++;
        }
        return new Dendrograma(criterio.nombre(), nodos[0], matriz.ids(), fusiones);
    }

    private String nombre(MatrizDistancias matriz, int[] cluster) {
        StringBuilder sb = new StringBuilder("{");
        for (int k = 0; k < cluster.length; k++) {
            if (k > 0) {
                sb.append(", ");
            }
            sb.append(matriz.id(cluster[k]));
        }
        return sb.append("}").toString();
    }
}
