package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.infra.medicion.Cronometro;

import java.util.List;

/** Búsqueda exacta con hash rodante y verificación de colisiones. */
public class RabinKarp implements StringMatcher {

    private static final long BASE_POR_DEFECTO = 256;
    private static final long PRIMO_POR_DEFECTO = 1_000_000_007L;
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
        long potencia = 1;
        for (int i = 1; i < m; i++) {
            potencia = (potencia * base) % primo;
        }
        long preprocesamientoNs = Cronometro.ahora() - inicio;
        if (traza.estaActiva()) {
            registrarHashPatron(patron, traza);
            traza.registrar("Hash del patrón", "hashPatron=" + hashPatron + ", base=" + base + ", primo=" + primo + ", potencia=" + potencia);
        }

        Posiciones posiciones = new Posiciones();
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
                    posiciones.agregar(s);
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
        return new ResultadoBusqueda(nombre(), posiciones.comoLista(), comparaciones, preprocesamientoNs, busquedaNs, traza.pasos());
    }

    private long hash(String cadena, int m) {
        long valor = 0;
        for (int i = 0; i < m; i++) {
            valor = (valor * base + cadena.charAt(i)) % primo;
        }
        return valor;
    }

    private void registrarHashPatron(String patron, Traza traza) {
        long valor = 0;
        for (int i = 0; i < patron.length(); i++) {
            long anterior = valor;
            valor = (valor * base + patron.charAt(i)) % primo;
            traza.registrar("Hash del patrón, carácter " + i,
                    "caracter=" + patron.charAt(i) + ", codigo=" + (int) patron.charAt(i) + ", anterior=" + anterior + ", hash=" + valor);
        }
    }
}
