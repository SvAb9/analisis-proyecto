package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Agrupamiento jerárquico aglomerativo. Empieza con un clúster por artículo y, en cada paso,
 * fusiona los dos clústeres más cercanos según el CriterioEnlace, hasta quedar uno solo.
 * Los tres algoritmos del R2 (Single, Complete, Average) comparten este bucle y solo
 * difieren en el criterio. En empates se fusiona el primer par encontrado (resultado determinista).
 */
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

        List<List<Integer>> clusters = new ArrayList<>();
        List<NodoDendrograma> nodos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            List<Integer> unitario = new ArrayList<>();
            unitario.add(i);
            clusters.add(unitario);
            nodos.add(NodoDendrograma.hoja(i, matriz.id(i)));
        }

        List<Dendrograma.Fusion> fusiones = new ArrayList<>();
        int paso = 1;
        while (clusters.size() > 1) {
            int mejorI = -1;
            int mejorJ = -1;
            double mejor = Double.POSITIVE_INFINITY;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double dist = criterio.distancia(clusters.get(i), clusters.get(j), d);
                    if (dist < mejor) {
                        mejor = dist;
                        mejorI = i;
                        mejorJ = j;
                    }
                }
            }

            List<Integer> a = clusters.get(mejorI);
            List<Integer> b = clusters.get(mejorJ);
            traza.registrar(
                    "Paso " + paso + " (" + criterio.nombre() + "): fusionar " + nombre(matriz, a)
                            + " y " + nombre(matriz, b),
                    "distancia = " + String.format(Locale.ROOT, "%.4f", mejor)
                            + ", clústeres restantes = " + (clusters.size() - 1));

            fusiones.add(new Dendrograma.Fusion(paso, List.copyOf(a), List.copyOf(b), mejor));
            NodoDendrograma nodo = NodoDendrograma.fusion(nodos.get(mejorI), nodos.get(mejorJ), mejor);

            List<Integer> unido = new ArrayList<>(a);
            unido.addAll(b);
            clusters.set(mejorI, unido);
            nodos.set(mejorI, nodo);
            clusters.remove(mejorJ);
            nodos.remove(mejorJ);
            paso++;
        }
        return new Dendrograma(criterio.nombre(), nodos.get(0), matriz.ids(), fusiones);
    }

    private String nombre(MatrizDistancias matriz, List<Integer> cluster) {
        StringBuilder sb = new StringBuilder("{");
        for (int k = 0; k < cluster.size(); k++) {
            if (k > 0) {
                sb.append(", ");
            }
            sb.append(matriz.id(cluster.get(k)));
        }
        return sb.append("}").toString();
    }
}
