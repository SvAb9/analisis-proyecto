package co.edu.uniquindio.bibliometria.infra.medicion;

import java.util.Arrays;

/**
 * Medición de tiempos (ns) alrededor de un algoritmo.
 */
public final class Cronometro {

    private Cronometro() {
    }

    /** Instante actual en nanosegundos; solo sirve para restar dos lecturas. */
    public static long ahora() {
        return System.nanoTime();
    }

    /** Mediana de las mediciones; no la alteran las pausas aisladas de la JVM o del sistema operativo. */
    public static long mediana(long[] mediciones) {
        if (mediciones.length == 0) {
            throw new IllegalArgumentException("No hay mediciones");
        }
        long[] ordenadas = mediciones.clone();
        Arrays.sort(ordenadas);
        int mitad = ordenadas.length / 2;
        return ordenadas.length % 2 == 1 ? ordenadas[mitad] : (ordenadas[mitad - 1] + ordenadas[mitad]) / 2;
    }
}
