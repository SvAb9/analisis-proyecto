package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.infra.medicion.Cronometro;

import java.util.ArrayList;
import java.util.List;

/**
 * Hash rodante, módulo primo y verificación de colisiones.
 *
 * <p>Compara el hash del patrón con el de cada ventana del texto; la siguiente ventana se obtiene en O(1)
 * quitando el primer carácter y agregando el nuevo. Dos cadenas distintas pueden dar el mismo hash
 * (colisión), así que cada coincidencia de hash se verifica carácter por carácter. Preprocesamiento O(m);
 * búsqueda O(n + m) esperado y O(n·m) en el peor caso, con muchas colisiones. Espacio adicional O(1).</p>
 *
 * <p>{@code comparaciones} cuenta las comparaciones de caracteres al verificar; la comparación de hashes
 * no se cuenta. Las colisiones se registran en la traza; para provocarlas en un caso de estudio se usa
 * un módulo pequeño con {@link #RabinKarp(long, long)}.</p>
 */
public class RabinKarp implements StringMatcher {

    private static final long BASE_POR_DEFECTO = 256;
    private static final long PRIMO_POR_DEFECTO = 1_000_000_007L;
    /** Mayor valor de la base y del primo para que base * primo y char * primo quepan en un long. */
    private static final long LIMITE = 1L << 31;

    private final long base;
    private final long primo;

    public RabinKarp() {
        this(BASE_POR_DEFECTO, PRIMO_POR_DEFECTO);
    }

    public RabinKarp(long base, long primo) {
        if (base < 2 || base >= LIMITE || primo < 2 || primo >= LIMITE) {
            throw new IllegalArgumentException("La base y el primo deben estar entre 2 y 2^31 - 1");
        }
        this.base = base;
        this.primo = primo;
    }

    @Override
    public String nombre() {
        return "RabinKarp";
    }

    @Override
    public ResultadoBusqueda buscar(String patron, String texto, Traza traza) {
        int m = patron.length();
        int n = texto.length();
        if (m == 0 || m > n) {
            return new ResultadoBusqueda(nombre(), List.of(), 0, 0, 0, traza.pasos());
        }

        long inicio = Cronometro.ahora();
        long hashPatron = hash(patron, m);
        long potencia = 1; // base^(m - 1) mod primo: peso del primer carácter de la ventana
        for (int i = 1; i < m; i++) {
            potencia = (potencia * base) % primo;
        }
        long preprocesamientoNs = Cronometro.ahora() - inicio;
        if (traza.estaActiva()) {
            traza.registrar("Hash del patrón", "hashPatron=" + hashPatron + ", base=" + base + ", primo=" + primo + ", potencia=" + potencia);
        }

        List<Integer> posiciones = new ArrayList<>();
        long comparaciones = 0;
        inicio = Cronometro.ahora();
        long hashVentana = hash(texto, m);
        for (int s = 0; s <= n - m; s++) {
            if (hashVentana == hashPatron) {
                int j = 0;
                while (j < m) {
                    comparaciones++;
                    if (texto.charAt(s + j) != patron.charAt(j)) {
                        break;
                    }
                    j++;
                }
                if (j == m) {
                    posiciones.add(s);
                }
                if (traza.estaActiva()) {
                    traza.registrar(j == m ? "Ocurrencia en la posición " + s : "Colisión en la posición " + s,
                            "alineacion=" + s + ", desplazamiento=1, hashVentana=" + hashVentana);
                }
            } else if (traza.estaActiva()) {
                traza.registrar("Hash distinto en la posición " + s, "alineacion=" + s + ", desplazamiento=1, hashVentana=" + hashVentana);
            }
            if (s < n - m) {
                long sinPrimero = (hashVentana + primo - (texto.charAt(s) % primo * potencia) % primo) % primo;
                hashVentana = (sinPrimero * base + texto.charAt(s + m)) % primo;
            }
        }
        long busquedaNs = Cronometro.ahora() - inicio;
        return new ResultadoBusqueda(nombre(), posiciones, comparaciones, preprocesamientoNs, busquedaNs, traza.pasos());
    }

    /** Hash polinómico de los primeros m caracteres, por el método de Horner. */
    private long hash(String cadena, int m) {
        long valor = 0;
        for (int i = 0; i < m; i++) {
            valor = (valor * base + cadena.charAt(i)) % primo;
        }
        return valor;
    }
}
