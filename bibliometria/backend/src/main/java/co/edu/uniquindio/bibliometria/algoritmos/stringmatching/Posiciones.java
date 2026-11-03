package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import java.util.List;

/** Guarda en un arreglo que crece las posiciones de las ocurrencias encontradas. */
final class Posiciones {

    private int[] valores = new int[8];
    private int cantidad = 0;

    void agregar(int posicion) {
        if (cantidad == valores.length) {
            int[] mayor = new int[valores.length * 2];
            for (int i = 0; i < cantidad; i++) {
                mayor[i] = valores[i];
            }
            valores = mayor;
        }
        valores[cantidad] = posicion;
        cantidad++;
    }

    List<Integer> comoLista() {
        Integer[] copia = new Integer[cantidad];
        for (int i = 0; i < cantidad; i++) {
            copia[i] = valores[i];
        }
        return List.of(copia);
    }
}
