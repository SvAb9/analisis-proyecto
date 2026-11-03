package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import java.util.List;

/** Árbol resultante del agrupamiento y sus fusiones. */
public class Dendrograma {

    public record Fusion(int paso, int[] clusterA, int[] clusterB, double distancia) {
    }

    private final String criterio;
    private final NodoDendrograma raiz;
    private final List<String> ids;
    private final Fusion[] fusiones;

    public Dendrograma(String criterio, NodoDendrograma raiz, List<String> ids, Fusion[] fusiones) {
        this.criterio = criterio;
        this.raiz = raiz;
        this.ids = List.copyOf(ids);
        this.fusiones = fusiones;
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

    public Fusion[] fusiones() {
        return fusiones;
    }

    public int[][] cortar(int k) {
        int n = ids.size();
        if (k < 1 || k > n) {
            throw new IllegalArgumentException("k debe estar entre 1 y " + n);
        }
        int[] etiqueta = new int[n];
        for (int i = 0; i < n; i++) {
            etiqueta[i] = i;
        }
        for (int f = 0; f < n - k; f++) {
            Fusion fusion = fusiones[f];
            int a = etiqueta[fusion.clusterA()[0]];
            int b = etiqueta[fusion.clusterB()[0]];
            for (int i = 0; i < n; i++) {
                if (etiqueta[i] == b) {
                    etiqueta[i] = a;
                }
            }
        }
        int[] orden = new int[k];
        int[] tamano = new int[k];
        int[] grupo = new int[n];
        int grupos = 0;
        for (int i = 0; i < n; i++) {
            int pos = 0;
            while (pos < grupos && orden[pos] != etiqueta[i]) {
                pos++;
            }
            if (pos == grupos) {
                orden[grupos++] = etiqueta[i];
            }
            grupo[i] = pos;
            tamano[pos]++;
        }
        int[][] clusters = new int[k][];
        for (int c = 0; c < k; c++) {
            clusters[c] = new int[tamano[c]];
            tamano[c] = 0;
        }
        for (int i = 0; i < n; i++) {
            clusters[grupo[i]][tamano[grupo[i]]++] = i;
        }
        return clusters;
    }

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
