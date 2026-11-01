package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;

/**
 * Contrato común de los 3 algoritmos de búsqueda exacta de R3.
 * Implementan: KMP, BoyerMoore y RabinKarp (Zuleta).
 *
 * Reglas:
 *  - Prohibido usar indexOf, contains, find, regex o cualquier función nativa de búsqueda.
 *  - El patrón y el texto llegan ya normalizados por el servicio (Preprocesador.normalizar).
 *  - Se devuelven TODAS las ocurrencias, incluidas las que se solapan.
 *  - preprocesamientoNs y busquedaNs se miden por separado (lo exige el análisis de R3).
 */
public interface StringMatcher {

    /** Nombre único y estable, usado por la API (ej. "KMP", "BoyerMoore", "RabinKarp"). */
    String nombre();

    /** Busca todas las ocurrencias exactas de patron en texto. */
    ResultadoBusqueda buscar(String patron, String texto, Traza traza);

    /** Búsqueda normal, sin traza. */
    default ResultadoBusqueda buscar(String patron, String texto) {
        return buscar(patron, texto, Traza.desactivada());
    }
}
