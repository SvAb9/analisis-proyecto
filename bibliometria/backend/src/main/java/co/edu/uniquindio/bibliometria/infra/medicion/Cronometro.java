package co.edu.uniquindio.bibliometria.infra.medicion;

import java.util.Arrays;

/** Toma tiempos y calcula su mediana. */
public final class Cronometro {

    private Cronometro() {
    }

    public static long ahora() {
        return System.nanoTime();
    }

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
