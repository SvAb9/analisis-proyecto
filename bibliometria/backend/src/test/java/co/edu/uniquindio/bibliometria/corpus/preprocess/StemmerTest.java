package co.edu.uniquindio.bibliometria.corpus.preprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Casos de los ejemplos del artículo de Porter y palabras del corpus; coinciden con NLTK (modo original). */
class StemmerTest {

    private final Stemmer stemmer = new Stemmer();

    private void verificar(String palabra, String esperado) {
        assertEquals(esperado, stemmer.stem(palabra), palabra);
    }

    @Test
    void paso1PluralesYTerminacionesVerbales() {
        verificar("caresses", "caress");
        verificar("ponies", "poni");
        verificar("cats", "cat");
        verificar("agreed", "agre");
        verificar("plastered", "plaster");
        verificar("motoring", "motor");
        verificar("conflated", "conflat");
        verificar("hopping", "hop");
        verificar("falling", "fall");
        verificar("filing", "file");
        verificar("happy", "happi");
        verificar("sky", "sky");
    }

    @Test
    void pasos2Y3SufijosDerivativos() {
        verificar("relational", "relat");
        verificar("conditional", "condit");
        verificar("vietnamization", "vietnam");
        verificar("predication", "predic");
        verificar("operator", "oper");
        verificar("hopefulness", "hope");
        verificar("electrical", "electr");
        verificar("goodness", "good");
    }

    @Test
    void pasos4Y5SufijosFinales() {
        verificar("replacement", "replac");
        verificar("adjustment", "adjust");
        verificar("adoption", "adopt");
        verificar("effective", "effect");
        verificar("probate", "probat");
        verificar("rate", "rate");
        verificar("controll", "control");
        verificar("roll", "roll");
    }

    @Test
    void palabrasDelCorpus() {
        verificar("generative", "gener");
        verificar("generalization", "gener");
        verificar("models", "model");
        verificar("assessing", "assess");
        verificar("learning", "learn");
        verificar("intelligence", "intellig");
        verificar("artificial", "artifici");
    }

    @Test
    void palabrasMuyCortasNoCambian() {
        verificar("ai", "ai");
        verificar("is", "is");
    }

    @Test
    void laMedidaCuentaSecuenciasVocalConsonante() {
        assertEquals(0, stemmer.medida("tr"));
        assertEquals(0, stemmer.medida("ee"));
        assertEquals(1, stemmer.medida("trouble"));
        assertEquals(1, stemmer.medida("oats"));
        assertEquals(2, stemmer.medida("troubles"));
        assertEquals(2, stemmer.medida("private"));
    }
}
