package co.edu.uniquindio.bibliometria.algoritmos.similitud;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;

/** Contrato de los algoritmos de similitud. */
public interface SimilitudStrategy {

    String nombre();

    ResultadoSimilitud comparar(String textoA, String textoB, Traza traza);

    default ResultadoSimilitud comparar(String textoA, String textoB) {
        return comparar(textoA, textoB, Traza.desactivada());
    }
}
