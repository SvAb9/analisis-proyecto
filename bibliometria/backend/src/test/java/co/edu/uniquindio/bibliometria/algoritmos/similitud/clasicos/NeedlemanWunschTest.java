package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NeedlemanWunschTest {

    private final NeedlemanWunsch nw = new NeedlemanWunsch();

    @Test
    void sustitucionEnMedio() {
        String[] a = {"generative", "ai", "assessment"};
        String[] b = {"generative", "ai", "evaluation"};
        NeedlemanWunsch.Alineamiento al = NeedlemanWunsch.alinear(a, b, null);
        assertEquals(1, al.puntaje()); // +1 +1 -1
        assertEquals(2, al.coincidencias());
        assertEquals(3, al.pares().getCantidad());
    }

    @Test
    void huecoCuandoSobraUnaPalabra() {
        String[] a = {"higher", "education", "assessment"};
        String[] b = {"higher", "assessment"};
        NeedlemanWunsch.Alineamiento al = NeedlemanWunsch.alinear(a, b, null);
        assertEquals(1, al.puntaje()); // +1 -1 +1
        assertEquals(2, al.coincidencias());
        NeedlemanWunsch.ParAlineado medio = al.pares().obtenerValor(1);
        assertEquals("education", medio.a());
        assertEquals(NeedlemanWunsch.SIMBOLO_HUECO, medio.b());
        assertEquals("higher", al.pares().obtenerValor(0).a());
        assertEquals("assessment", al.pares().obtenerValor(2).a());
    }

    @Test
    void matrizInicializadaConHuecos() {
        int[][] f = NeedlemanWunsch.alinear(new String[] {"a1", "b2"}, new String[] {"c3"}, null).matriz();
        assertEquals(0, f[0][0]);
        assertEquals(-1, f[1][0]);
        assertEquals(-2, f[2][0]);
        assertEquals(-1, f[0][1]);
    }

    @Test
    void textosIgualesYDistintos() {
        ResultadoSimilitud iguales = nw.comparar("learning analytics systems", "learning analytics systems");
        assertEquals(1.0, iguales.similitud(), 1e-12);
        assertEquals(3, iguales.valorCrudo());
        assertEquals(0.0, nw.comparar("learning analytics", "chatbot plagiarism").similitud(), 1e-12);
    }
}
