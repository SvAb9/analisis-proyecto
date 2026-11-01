package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Paso;
import java.util.List;

/**
 * Resultado de buscar un patrón en un texto con un algoritmo de string matching.
 *
 * @param algoritmo         nombre del algoritmo (igual a StringMatcher.nombre())
 * @param posiciones        índices (base 0) donde empieza cada ocurrencia exacta, en orden
 * @param comparaciones     número de comparaciones de caracteres realizadas
 * @param preprocesamientoNs tiempo de construir las estructuras auxiliares (tabla LPS, tablas de Boyer-Moore, hash del patrón)
 * @param busquedaNs        tiempo de recorrer el texto
 * @param pasos             pasos intermedios; vacío si la traza estaba desactivada
 */
public record ResultadoBusqueda(
        String algoritmo,
        List<Integer> posiciones,
        long comparaciones,
        long preprocesamientoNs,
        long busquedaNs,
        List<Paso> pasos) {
}
