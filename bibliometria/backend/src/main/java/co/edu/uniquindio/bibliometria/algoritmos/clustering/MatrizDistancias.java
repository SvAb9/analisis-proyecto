package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Matriz simétrica de distancias entre artículos (diagonal en 0).
 * La fila/columna i corresponde al artículo ids().get(i).
 */
public class MatrizDistancias {

    private static final double TOLERANCIA = 1e-9;

    private final List<String> ids;
    private final double[][] valores;

    public MatrizDistancias(List<String> ids, double[][] valores) {
        int n = ids.size();
        if (valores.length != n) {
            throw new IllegalArgumentException("La matriz debe ser de " + n + "x" + n);
        }
        double[][] copia = new double[n][n];
        for (int i = 0; i < n; i++) {
            if (valores[i].length != n) {
                throw new IllegalArgumentException("La matriz debe ser cuadrada");
            }
            for (int j = 0; j < n; j++) {
                if (valores[i][j] < 0) {
                    throw new IllegalArgumentException("Distancia negativa en [" + i + "][" + j + "]");
                }
                if (Math.abs(valores[i][j] - valores[j][i]) > TOLERANCIA) {
                    throw new IllegalArgumentException("La matriz no es simétrica en [" + i + "][" + j + "]");
                }
                copia[i][j] = valores[i][j];
            }
            if (Math.abs(copia[i][i]) > TOLERANCIA) {
                throw new IllegalArgumentException("La diagonal debe ser 0");
            }
        }
        this.ids = List.copyOf(ids);
        this.valores = copia;
    }

    /** Distancia = 1 - similitud. La diagonal se fuerza a 0. */
    public static MatrizDistancias desdeSimilitud(List<String> ids, double[][] similitud) {
        int n = ids.size();
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = (i == j) ? 0.0 : Math.max(0.0, 1.0 - similitud[i][j]);
            }
        }
        return new MatrizDistancias(ids, d);
    }

    /**
     * Compara todos los pares de textos con el algoritmo de similitud dado
     * (solo el triángulo superior; la matriz es simétrica) y la convierte en distancias.
     */
    public static MatrizDistancias desdeTextos(List<String> ids, List<String> textos, SimilitudStrategy estrategia) {
        int n = ids.size();
        if (textos.size() != n) {
            throw new IllegalArgumentException("ids y textos deben tener el mismo tamaño");
        }
        double[][] sim = new double[n][n];
        for (int i = 0; i < n; i++) {
            sim[i][i] = 1.0;
            for (int j = i + 1; j < n; j++) {
                double s = estrategia.comparar(textos.get(i), textos.get(j)).similitud();
                sim[i][j] = s;
                sim[j][i] = s;
            }
        }
        return desdeSimilitud(ids, sim);
    }

    public int tamano() {
        return ids.size();
    }

    public List<String> ids() {
        return new ArrayList<>(ids);
    }

    public String id(int i) {
        return ids.get(i);
    }

    public double distancia(int i, int j) {
        return valores[i][j];
    }

    /** Copia de la matriz, para que nadie la modifique por fuera. */
    public double[][] valores() {
        int n = valores.length;
        double[][] copia = new double[n][n];
        for (int i = 0; i < n; i++) {
            copia[i] = valores[i].clone();
        }
        return copia;
    }
}
