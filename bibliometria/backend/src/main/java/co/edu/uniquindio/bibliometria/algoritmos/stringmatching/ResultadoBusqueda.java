package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Paso;
import java.util.List;

/** Resultado de buscar un patrón en un texto. */
public record ResultadoBusqueda(
        String algoritmo,
        List<Integer> posiciones,
        long comparaciones,
        long preprocesamientoNs,
        long busquedaNs,
        List<Paso> pasos) {
}
