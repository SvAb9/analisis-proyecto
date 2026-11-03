package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.infra.medicion.Cronometro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tablas de carácter malo y sufijo bueno.
 *
 * <p>Compara cada ventana de derecha a izquierda. Al fallar, avanza el mayor de los dos desplazamientos:
 * el del carácter malo (alinear la última aparición en el patrón del carácter del texto que falló) y el
 * del sufijo bueno (volver a alinear el sufijo que ya coincidió). Preprocesamiento O(m + σ); búsqueda
 * O(n + m) con ambas reglas en la versión sin ocurrencias, O(n·m) en el peor caso al reportar todas las
 * ocurrencias de un patrón muy repetitivo, y cerca de O(n/m) en texto corriente. Espacio O(m + σ).</p>
 *
 * <p>Las últimas apariciones se guardan en un arreglo para ASCII y en un mapa para el resto, porque los
 * abstracts normalizados son casi solo ASCII. {@code comparaciones} cuenta solo las de la búsqueda.</p>
 */
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
        int[] ultimaAscii = new int[ASCII];
        Map<Character, Integer> ultimaOtros = new HashMap<>();
        tablaCaracterMalo(patron, ultimaAscii, ultimaOtros);
        int[] sufijoBueno = tablaSufijoBueno(patron);
        long preprocesamientoNs = Cronometro.ahora() - inicio;
        if (traza.estaActiva()) {
            traza.registrar("Tabla de carácter malo (última aparición)", describirCaracterMalo(patron, ultimaAscii, ultimaOtros));
            traza.registrar("Tabla de sufijo bueno", "desplazamiento=" + Arrays.toString(sufijoBueno));
        }

        List<Integer> posiciones = new ArrayList<>();
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
                posiciones.add(s);
                desplazamiento = sufijoBueno[0];
                if (traza.estaActiva()) {
                    traza.registrar("Ocurrencia en la posición " + s, "alineacion=" + s + ", desplazamiento=" + desplazamiento);
                }
            } else {
                char malo = texto.charAt(s + j);
                int porCaracterMalo = j - ultimaAparicion(malo, ultimaAscii, ultimaOtros);
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
        return new ResultadoBusqueda(nombre(), posiciones, comparaciones, preprocesamientoNs, busquedaNs, traza.pasos());
    }

    private static void tablaCaracterMalo(String patron, int[] ultimaAscii, Map<Character, Integer> ultimaOtros) {
        Arrays.fill(ultimaAscii, -1);
        for (int i = 0; i < patron.length(); i++) {
            char c = patron.charAt(i);
            if (c < ASCII) {
                ultimaAscii[c] = i;
            } else {
                ultimaOtros.put(c, i);
            }
        }
    }

    private static int ultimaAparicion(char c, int[] ultimaAscii, Map<Character, Integer> ultimaOtros) {
        return c < ASCII ? ultimaAscii[c] : ultimaOtros.getOrDefault(c, -1);
    }

    /**
     * Tabla de sufijo bueno en O(m) (versión fuerte). desplazamiento[j + 1] es cuánto avanzar si falló la
     * posición j; desplazamiento[0] es cuánto avanzar después de una ocurrencia completa.
     */
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

    private static String describirCaracterMalo(String patron, int[] ultimaAscii, Map<Character, Integer> ultimaOtros) {
        StringBuilder sb = new StringBuilder();
        boolean[] vistoAscii = new boolean[ASCII];
        Map<Character, Boolean> vistoOtros = new HashMap<>();
        for (int i = 0; i < patron.length(); i++) {
            char c = patron.charAt(i);
            boolean repetido = c < ASCII ? vistoAscii[c] : vistoOtros.containsKey(c);
            if (!repetido) {
                if (c < ASCII) {
                    vistoAscii[c] = true;
                } else {
                    vistoOtros.put(c, true);
                }
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append('\'').append(c).append("'=").append(ultimaAparicion(c, ultimaAscii, ultimaOtros));
            }
        }
        return sb.toString();
    }
}
