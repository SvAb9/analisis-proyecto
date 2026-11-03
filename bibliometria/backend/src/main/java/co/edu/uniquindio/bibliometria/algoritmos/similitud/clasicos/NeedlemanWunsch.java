package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.PreparadorTexto;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import co.edu.uniquindio.bibliometria.util.estructuras.Pila;

import java.util.List;

/** Alineamiento global de secuencias de palabras. */
public class NeedlemanWunsch implements SimilitudStrategy {

    public static final int COINCIDENCIA = 1;
    public static final int SUSTITUCION = -1;
    public static final int HUECO = -1;
    public static final String SIMBOLO_HUECO = "-";
    public static final int LIMITE_TRAZA = 25;

    public record ParAlineado(String a, String b) {
        public boolean coincide() {
            return a.equals(b);
        }
    }

    public record Alineamiento(int[][] matriz, int puntaje, ListaEnlazada<ParAlineado> pares, int coincidencias) {}

    @Override
    public String nombre() {
        return "NeedlemanWunsch";
    }

    @Override
    public ResultadoSimilitud comparar(String textoA, String textoB, Traza traza) {
        long inicio = System.nanoTime();
        String[] a = PreparadorTexto.tokensContenido(textoA);
        String[] b = PreparadorTexto.tokensContenido(textoB);
        Alineamiento alineamiento = alinear(a, b, traza);
        int longitud = alineamiento.pares().getCantidad();
        double similitud = longitud == 0 ? 1.0 : (double) alineamiento.coincidencias() / longitud;
        long tiempo = System.nanoTime() - inicio;
        boolean conTraza = traza != null && traza.estaActiva();
        if (conTraza) {
            traza.registrar("puntaje F[" + a.length + "][" + b.length + "] = " + alineamiento.puntaje(),
                    a.length + " y " + b.length + " tokens de contenido");
            traza.registrar("similitud = " + similitud,
                    alineamiento.coincidencias() + " coincidencias / " + longitud + " posiciones alineadas");
        }
        return new ResultadoSimilitud(nombre(), similitud, alineamiento.puntaje(), tiempo,
                conTraza ? traza.pasos() : List.of());
    }

    public static Alineamiento alinear(String[] a, String[] b, Traza traza) {
        int n = a.length;
        int m = b.length;
        boolean conTraza = traza != null && traza.estaActiva();
        int[][] f = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            f[i][0] = i * HUECO;
        }
        for (int j = 0; j <= m; j++) {
            f[0][j] = j * HUECO;
        }
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int s = a[i - 1].equals(b[j - 1]) ? COINCIDENCIA : SUSTITUCION;
                int diagonal = f[i - 1][j - 1] + s;
                int arriba = f[i - 1][j] + HUECO;
                int izquierda = f[i][j - 1] + HUECO;
                f[i][j] = Math.max(diagonal, Math.max(arriba, izquierda));
                if (conTraza && i <= LIMITE_TRAZA && j <= LIMITE_TRAZA) {
                    traza.registrar("F[" + i + "][" + j + "] = " + f[i][j], a[i - 1] + " vs " + b[j - 1] + ": max("
                            + diagonal + ", " + arriba + ", " + izquierda + ")");
                }
            }
        }
        return reconstruir(a, b, f, traza);
    }

    private static Alineamiento reconstruir(String[] a, String[] b, int[][] f, Traza traza) {
        Pila<ParAlineado> pila = new Pila<>();
        int i = a.length;
        int j = b.length;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0
                    && f[i][j] == f[i - 1][j - 1] + (a[i - 1].equals(b[j - 1]) ? COINCIDENCIA : SUSTITUCION)) {
                pila.push(new ParAlineado(a[i - 1], b[j - 1]));
                i--;
                j--;
            } else if (i > 0 && f[i][j] == f[i - 1][j] + HUECO) {
                pila.push(new ParAlineado(a[i - 1], SIMBOLO_HUECO));
                i--;
            } else {
                pila.push(new ParAlineado(SIMBOLO_HUECO, b[j - 1]));
                j--;
            }
        }
        ListaEnlazada<ParAlineado> pares = new ListaEnlazada<>();
        int coincidencias = 0;
        while (!pila.estaVacia()) {
            ParAlineado par = pila.pop();
            pares.insertarFinal(par);
            if (par.coincide()) {
                coincidencias++;
                if (traza != null && traza.estaActiva()) {
                    traza.registrar("coincidencia " + coincidencias, par.a());
                }
            }
        }
        return new Alineamiento(f, f[a.length][b.length], pares, coincidencias);
    }
}
