package co.edu.uniquindio.bibliometria.corpus.preprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class PreprocesadorTextoTest {

    private final PreprocesadorTexto preprocesador = new PreprocesadorTexto();

    @Test
    void normalizarNoAplicaStemmingNiQuitaStopwords() {
        assertEquals("the generative models", preprocesador.normalizar("The Generative Models!"));
    }

    @Test
    void tokenizarQuitaStopwordsNumerosYPalabrasDeUnaLetraYAplicaStemming() {
        assertEquals(List.of("gener", "model", "assess", "learn", "tool"),
                preprocesador.tokenizar("The generative models are assessing 3 learning tools, a"));
    }

    @Test
    void tokenizarQuitaStopwordsEnEspanol() {
        List<String> tokens = preprocesador.tokenizar("El análisis de los modelos");
        assertFalse(tokens.contains("el"));
        assertFalse(tokens.contains("de"));
        assertFalse(tokens.contains("los"));
        assertEquals(2, tokens.size());
    }

    @Test
    void variantesDeUnaMismaPalabraDanLaMismaRaiz() {
        assertEquals(preprocesador.tokenizar("assessment"), preprocesador.tokenizar("assessments"));
        assertEquals(preprocesador.tokenizar("learning"), preprocesador.tokenizar("learned"));
    }

    @Test
    void textoNuloODeSoloStopwordsDaListaVacia() {
        assertTrue(preprocesador.tokenizar(null).isEmpty());
        assertTrue(preprocesador.tokenizar("the of and").isEmpty());
    }

    @Test
    void cumpleElContratoPreprocesador() {
        Preprocesador contrato = preprocesador;
        assertEquals("ia generativa", contrato.normalizar("IA Generativa"));
    }
}
