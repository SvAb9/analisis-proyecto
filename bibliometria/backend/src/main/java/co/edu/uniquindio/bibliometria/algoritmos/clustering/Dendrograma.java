package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado del agrupamiento jerárquico: el árbol, el orden de las fusiones y los ids
 * de los artículos (posición i = fila i de la matriz de distancias).
 */
public class Dendrograma {

    /** Una fusión: qué dos clústeres (índices de artículos) se unieron y a qué distancia. */
    public record Fusion(int paso, List<Integer> clusterA, List<Integer> clusterB, double distancia) {
    }

    private final String criterio;
    private final NodoDendrograma raiz;
    private final List<String> ids;
    private final List<Fusion> fusiones;

    public Dendrograma(String criterio, NodoDendrograma raiz, List<String> ids, List<Fusion> fusiones) {
        this.criterio = criterio;
        this.raiz = raiz;
        this.ids = List.copyOf(ids);
        this.fusiones = List.copyOf(fusiones);
    }

    public String criterio() {
        return criterio;
    }

    public NodoDendrograma raiz() {
        return raiz;
    }

    public List<String> ids() {
        return ids;
    }

    /** Fusiones en el orden en que ocurrieron (n - 1 en total). */
    public List<Fusion> fusiones() {
        return fusiones;
    }

    /**
     * Corta el árbol en k clústeres aplicando solo las primeras n - k fusiones.
     * k = n da un clúster por artículo; k = 1 da un solo clúster.
     *
     * @return lista de clústeres, cada uno con los índices de sus artículos
     */
    public List<List<Integer>> cortar(int k) {
        int n = ids.size();
        if (k < 1 || k > n) {
            throw new IllegalArgumentException("k debe estar entre 1 y " + n);
        }
        int[] etiqueta = new int[n];
        for (int i = 0; i < n; i++) {
            etiqueta[i] = i;
        }
        for (int f = 0; f < n - k; f++) {
            Fusion fusion = fusiones.get(f);
            int a = etiqueta[fusion.clusterA().get(0)];
            int b = etiqueta[fusion.clusterB().get(0)];
            for (int i = 0; i < n; i++) {
                if (etiqueta[i] == b) {
                    etiqueta[i] = a;
                }
            }
        }
        List<Integer> orden = new ArrayList<>();
        List<List<Integer>> clusters = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int pos = orden.indexOf(etiqueta[i]);
            if (pos < 0) {
                orden.add(etiqueta[i]);
                clusters.add(new ArrayList<>());
                pos = orden.size() - 1;
            }
            clusters.get(pos).add(i);
        }
        return clusters;
    }

    /**
     * Matriz cofenética: para cada par de artículos, la altura del nodo donde
     * se juntan por primera vez en el árbol.
     */
    public double[][] matrizCofenetica() {
        int n = ids.size();
        double[][] m = new double[n][n];
        llenarCofenetica(raiz, m);
        return m;
    }

    private void llenarCofenetica(NodoDendrograma nodo, double[][] m) {
        if (nodo.esHoja()) {
            return;
        }
        for (int i : nodo.izquierda().hojas()) {
            for (int j : nodo.derecha().hojas()) {
                m[i][j] = nodo.altura();
                m[j][i] = nodo.altura();
            }
        }
        llenarCofenetica(nodo.izquierda(), m);
        llenarCofenetica(nodo.derecha(), m);
    }
}
