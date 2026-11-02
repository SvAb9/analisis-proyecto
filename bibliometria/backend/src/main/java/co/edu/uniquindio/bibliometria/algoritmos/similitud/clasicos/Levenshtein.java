package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.PreparadorTexto;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;

/**
 * Distancia de edición con matriz DP, normalizada a similitud.
 *
 * <p>Trabaja a nivel de carácter sobre el texto normalizado (minúsculas, sin signos).
 * Recurrencia, con D[i][0] = i y D[0][j] = j:</p>
 * <pre>
 * D[i][j] = min( D[i-1][j] + 1,                      (eliminar a_i)
 *                D[i][j-1] + 1,                      (insertar b_j)
 *                D[i-1][j-1] + (a_i == b_j ? 0 : 1)) (sustituir / coincidir)
 * </pre>
 * <p>Similitud = 1 - D[n][m] / max(n, m).</p>
 *
 * <p>Tiempo Θ(n·m). Espacio Θ(n·m) cuando la traza está activa (hace falta la matriz para
 * mostrarla) y Θ(m) si no, porque cada fila solo depende de la anterior. Con traza activa solo
 * se registran las celdas de la esquina superior izquierda (hasta {@link #LIMITE_TRAZA}): la
 * matriz de dos abstracts completos tiene más de un millón de celdas.</p>
 */
public class Levenshtein implements SimilitudStrategy {

    public static final int LIMITE_TRAZA = 25;

    @Override
    public String nombre() {
        return "Levenshtein";
    }

    @Override
    public ResultadoSimilitud comparar(String textoA, String textoB, Traza traza) {
        long inicio = System.nanoTime();
        String a = PreparadorTexto.normalizar(textoA);
        String b = PreparadorTexto.normalizar(textoB);
        boolean conTraza = traza != null && traza.estaActiva();
        int distancia = conTraza ? matriz(a, b, traza)[a.length()][b.length()] : distanciaDosFilas(a, b);
        double similitud = similitudDesdeDistancia(distancia, a.length(), b.length());
        long tiempo = System.nanoTime() - inicio;
        if (conTraza) {
            traza.registrar("distancia D[" + a.length() + "][" + b.length() + "] = " + distancia,
                    a.length() + " y " + b.length() + " caracteres normalizados");
            traza.registrar("similitud = " + similitud,
                    "1 - " + distancia + " / max(" + a.length() + ", " + b.length() + ")");
        }
        return new ResultadoSimilitud(nombre(), similitud, distancia, tiempo, conTraza ? traza.pasos() : java.util.List.of());
    }

    public static double similitudDesdeDistancia(int distancia, int n, int m) {
        int mayor = Math.max(n, m);
        return mayor == 0 ? 1.0 : 1.0 - (double) distancia / mayor;
    }

    /** Matriz completa D de tamaño (n+1)×(m+1). Si la traza está activa registra las primeras celdas. */
    public static int[][] matriz(String a, String b, Traza traza) {
        int n = a.length();
        int m = b.length();
        boolean conTraza = traza != null && traza.estaActiva();
        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        if (conTraza && (n > LIMITE_TRAZA || m > LIMITE_TRAZA)) {
            traza.registrar("matriz de " + (n + 1) + " x " + (m + 1) + " celdas",
                    "se registran solo las celdas con i, j <= " + LIMITE_TRAZA);
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int costo = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int eliminar = d[i - 1][j] + 1;
                int insertar = d[i][j - 1] + 1;
                int sustituir = d[i - 1][j - 1] + costo;
                d[i][j] = Math.min(eliminar, Math.min(insertar, sustituir));
                if (conTraza && i <= LIMITE_TRAZA && j <= LIMITE_TRAZA) {
                    traza.registrar("D[" + i + "][" + j + "] = " + d[i][j],
                            "'" + a.charAt(i - 1) + "' vs '" + b.charAt(j - 1) + "': min(" + eliminar + ", "
                                    + insertar + ", " + d[i - 1][j - 1] + "+" + costo + ")");
                }
            }
        }
        return d;
    }

    /** Misma recurrencia guardando solo dos filas: espacio Θ(m). */
    public static int distanciaDosFilas(String a, String b) {
        int m = b.length();
        int[] anterior = new int[m + 1];
        int[] actual = new int[m + 1];
        for (int j = 0; j <= m; j++) {
            anterior[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            actual[0] = i;
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= m; j++) {
                int costo = ca == b.charAt(j - 1) ? 0 : 1;
                actual[j] = Math.min(anterior[j] + 1, Math.min(actual[j - 1] + 1, anterior[j - 1] + costo));
            }
            int[] temporal = anterior;
            anterior = actual;
            actual = temporal;
        }
        return anterior[m];
    }
}
