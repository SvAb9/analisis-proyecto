package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;

/** Contrato de los algoritmos de búsqueda de patrones. */
public interface StringMatcher {

    String nombre();

    ResultadoBusqueda buscar(String patron, String texto, Traza traza);

    default ResultadoBusqueda buscar(String patron, String texto) {
        return buscar(patron, texto, Traza.desactivada());
    }
}
