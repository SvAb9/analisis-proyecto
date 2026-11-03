package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.infra.medicion.Cronometro;

import java.util.Arrays;
import java.util.List;

/** Búsqueda exacta de derecha a izquierda con las reglas de carácter malo y sufijo bueno. */
public class BoyerMoore implements StringMatcher {

    private static final int ASCII = 128;

    @Override
    public String nombre() {
        return "BoyerMoore";
    }

    @Override
    public ResultadoBusqueda buscar(String patron, String texto, Traza traza) {
        int m = patron.length();
        int n = texto.length();
        if (m == 0 || m > n) {
            return new ResultadoBusqueda(nombre(), List.of(), 0, 0, 0, traza.pasos());
        }

        long inicio = Cronometro.ahora();
        int[] ultimaAscii = tablaCaracterMalo(patron);
        int[] sufijoBueno = tablaSufijoBueno(patron);
        long preprocesamientoNs = Cronometro.ahora() - inicio;
        if (traza.estaActiva()) {
            traza.registrar("Tabla de carácter malo (última aparición)", describirCaracterMalo(patron));
            traza.registrar("Tabla de sufijo bueno", "desplazamiento=" + Arrays.toString(sufijoBueno));
        }

        Posiciones posiciones = new Posiciones();
        long comparaciones = 0;
        int s = 0;
        inicio = Cronometro.ahora();
        while (s <= n - m) {
            int j = m - 1;
            while (j >= 0) {
                comparaciones++;
                if (patron.charAt(j) != texto.charAt(s + j)) {
                    break;
                }
                j--;
            }
            int desplazamiento;
            if (j < 0) {
                posiciones.agregar(s);
                desplazamiento = sufijoBueno[0];
                if (traza.estaActiva()) {
                    traza.registrar("Ocurrencia en la posición " + s, "alineacion=" + s + ", desplazamiento=" + desplazamiento);
                }
            } else {
                char malo = texto.charAt(s + j);
                int porCaracterMalo = j - ultimaAparicion(malo, patron, ultimaAscii);
                desplazamiento = Math.max(sufijoBueno[j + 1], porCaracterMalo);
                if (traza.estaActiva()) {
                    traza.registrar("Fallo: texto[" + (s + j) + "]='" + malo + "', patrón[" + j + "]='" + patron.charAt(j) + "'",
                            "alineacion=" + s + ", desplazamiento=" + desplazamiento + ", caracterMalo=" + porCaracterMalo
                                    + ", sufijoBueno=" + sufijoBueno[j + 1]);
                }
            }
            s += desplazamiento;
        }
        long busquedaNs = Cronometro.ahora() - inicio;
        return new ResultadoBusqueda(nombre(), posiciones.comoLista(), comparaciones, preprocesamientoNs, busquedaNs, traza.pasos());
    }

    private static int[] tablaCaracterMalo(String patron) {
        int[] ultima = new int[ASCII];
        Arrays.fill(ultima, -1);
        for (int i = 0; i < patron.length(); i++) {
            char c = patron.charAt(i);
            if (c < ASCII) {
                ultima[c] = i;
            }
        }
        return ultima;
    }

    private static int ultimaAparicion(char c, String patron, int[] ultimaAscii) {
        if (c < ASCII) {
            return ultimaAscii[c];
        }
        for (int i = patron.length() - 1; i >= 0; i--) {
            if (patron.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    static int[] tablaSufijoBueno(String patron) {
        int m = patron.length();
        int[] desplazamiento = new int[m + 1];
        int[] borde = new int[m + 1];
        int i = m;
        int j = m + 1;
        borde[i] = j;
        while (i > 0) {
            while (j <= m && patron.charAt(i - 1) != patron.charAt(j - 1)) {
                if (desplazamiento[j] == 0) {
                    desplazamiento[j] = j - i;
                }
                j = borde[j];
            }
            i--;
            j--;
            borde[i] = j;
        }
        j = borde[0];
        for (i = 0; i <= m; i++) {
            if (desplazamiento[i] == 0) {
                desplazamiento[i] = j;
            }
            if (i == j) {
                j = borde[j];
            }
        }
        return desplazamiento;
    }

    private static String describirCaracterMalo(String patron) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < patron.length(); i++) {
            char c = patron.charAt(i);
            boolean repetido = false;
            for (int k = 0; k < i; k++) {
                if (patron.charAt(k) == c) {
                    repetido = true;
                }
            }
            if (!repetido) {
                int ultima = i;
                for (int k = i + 1; k < patron.length(); k++) {
                    if (patron.charAt(k) == c) {
                        ultima = k;
                    }
                }
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append('\'').append(c).append("'=").append(ultima);
            }
        }
        return sb.toString();
    }
}
