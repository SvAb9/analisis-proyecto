package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.infra.medicion.Cronometro;

import java.util.Arrays;
import java.util.List;

/** Búsqueda exacta con tabla de prefijos (LPS), sin retroceder en el texto. */
public class KMP implements StringMatcher {

    @Override
    public String nombre() {
        return "KMP";
    }

    @Override
    public ResultadoBusqueda buscar(String patron, String texto, Traza traza) {
        int m = patron.length();
        int n = texto.length();
        if (m == 0 || m > n) {
            return new ResultadoBusqueda(nombre(), List.of(), 0, 0, 0, traza.pasos());
        }

        long inicio = Cronometro.ahora();
        int[] lps = tablaLps(patron);
        long preprocesamientoNs = Cronometro.ahora() - inicio;
        if (traza.estaActiva()) {
            traza.registrar("Tabla de prefijos (LPS)", "lps=" + Arrays.toString(lps));
        }

        Posiciones posiciones = new Posiciones();
        long comparaciones = 0;
        int i = 0;
        int j = 0;
        inicio = Cronometro.ahora();
        while (i < n) {
            comparaciones++;
            if (texto.charAt(i) == patron.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    posiciones.agregar(i - m);
                    if (traza.estaActiva()) {
                        traza.registrar("Ocurrencia en la posición " + (i - m),
                                "alineacion=" + (i - m) + ", desplazamiento=" + (m - lps[m - 1]) + ", j=" + lps[m - 1]);
                    }
                    j = lps[m - 1];
                }
            } else if (j > 0) {
                if (traza.estaActiva()) {
                    traza.registrar("Fallo: texto[" + i + "]='" + texto.charAt(i) + "', patrón[" + j + "]='"
                                    + patron.charAt(j) + "'; el texto no retrocede",
                            "alineacion=" + (i - j) + ", desplazamiento=" + (j - lps[j - 1]) + ", j=" + lps[j - 1]);
                }
                j = lps[j - 1];
            } else {
                if (traza.estaActiva()) {
                    traza.registrar("Fallo al inicio del patrón en texto[" + i + "]='" + texto.charAt(i) + "'",
                            "alineacion=" + i + ", desplazamiento=1, j=0");
                }
                i++;
            }
        }
        long busquedaNs = Cronometro.ahora() - inicio;
        return new ResultadoBusqueda(nombre(), posiciones.comoLista(), comparaciones, preprocesamientoNs, busquedaNs, traza.pasos());
    }

    static int[] tablaLps(String patron) {
        int m = patron.length();
        int[] lps = new int[m];
        int largo = 0;
        int i = 1;
        while (i < m) {
            if (patron.charAt(i) == patron.charAt(largo)) {
                largo++;
                lps[i] = largo;
                i++;
            } else if (largo > 0) {
                largo = lps[largo - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }
}
