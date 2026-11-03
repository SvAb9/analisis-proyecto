package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas unitarias de Jaccard con casos de resultado conocido.
 */
class JaccardSimilitudTest {

    private final JaccardSimilitud jaccard = new JaccardSimilitud();

    @Test
    void mitadDeTerminosEnComun() {
        // A = {artificial, intelligence, education}, B = {artificial, intelligence, assessment}
        ResultadoSimilitud r = jaccard.comparar("artificial intelligence in education",
                "artificial intelligence for assessment");
        assertEquals(2, r.valorCrudo());
        assertEquals(2.0 / 4.0, r.similitud(), 1e-12);
    }

    @Test
    void repetidosNoCuentanDosVeces() {
        assertEquals(1.0, jaccard.comparar("learning learning learning", "learning").similitud(), 1e-12);
    }

    @Test
    void conjuntoOrdenadoEInterseccionPorMezcla() {
        ListaEnlazada<String> a = JaccardSimilitud.conjunto(new String[] {"zeta", "alfa", "beta", "alfa"});
        assertEquals("[alfa, beta, zeta]", a.toString());
        ListaEnlazada<String> b = JaccardSimilitud.conjunto(new String[] {"beta", "gamma", "zeta"});
        assertEquals("[beta, zeta]", JaccardSimilitud.interseccion(a, b).toString());
    }

    @Test
    void disjuntosYVacios() {
        assertEquals(0.0, jaccard.comparar("chatbot plagiarism", "simulation agents").similitud(), 1e-12);
        assertEquals(1.0, jaccard.comparar("the of and", "in on").similitud(), 1e-12);
    }
}
