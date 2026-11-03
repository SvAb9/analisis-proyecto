package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de Levenshtein con casos de resultado conocido.
 */
class LevenshteinTest {

    private final Levenshtein levenshtein = new Levenshtein();

    @Test
    void distanciaClasicaKittenSitting() {
        int[][] d = Levenshtein.matriz("kitten", "sitting", null);
        assertEquals(3, d[6][7]);
        assertEquals(3, Levenshtein.distanciaDosFilas("kitten", "sitting"));
    }

    @Test
    void casosBorde() {
        assertEquals(0, Levenshtein.distanciaDosFilas("", ""));
        assertEquals(5, Levenshtein.distanciaDosFilas("", "datos"));
        assertEquals(5, Levenshtein.distanciaDosFilas("datos", ""));
        assertEquals(1.0, levenshtein.comparar("", "").similitud());
    }

    @Test
    void similitudNormalizada() {
        ResultadoSimilitud r = levenshtein.comparar("kitten", "sitting");
        assertEquals(3, r.valorCrudo());
        assertEquals(1.0 - 3.0 / 7.0, r.similitud(), 1e-12);
    }

    @Test
    void normalizaMayusculasYSignos() {
        assertEquals(1.0, levenshtein.comparar("AI, in Education!", "ai in   education").similitud(), 1e-12);
    }

    @Test
    void ambasVersionesCoincidenYTrazaRegistraCeldas() {
        String a = "generative";
        String b = "emergence";
        ResultadoSimilitud conTraza = levenshtein.comparar(a, b, Traza.activada());
        ResultadoSimilitud sinTraza = levenshtein.comparar(a, b);
        assertEquals(sinTraza.valorCrudo(), conTraza.valorCrudo());
        assertEquals(6, conTraza.valorCrudo());
        assertEquals(a.length() * b.length() + 2, conTraza.pasos().size()); // celdas + distancia + similitud
        assertTrue(sinTraza.pasos().isEmpty());
    }

    @Test
    void trazaDeTextosLargosSeLimita() {
        String largo = "artificial intelligence in higher education ".repeat(20);
        ResultadoSimilitud r = levenshtein.comparar(largo, largo + "x", Traza.activada());
        assertTrue(r.pasos().size() <= Levenshtein.LIMITE_TRAZA * Levenshtein.LIMITE_TRAZA + 3, "" + r.pasos().size());
    }

    @Test
    void esSimetrica() {
        assertEquals(levenshtein.comparar("assessment", "assignment").similitud(),
                levenshtein.comparar("assignment", "assessment").similitud(), 1e-12);
        assertTrue(levenshtein.comparar("assessment", "assignment").similitud() < 1.0);
    }
}
