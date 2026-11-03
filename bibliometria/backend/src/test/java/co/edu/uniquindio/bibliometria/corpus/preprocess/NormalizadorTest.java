package co.edu.uniquindio.bibliometria.corpus.preprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class NormalizadorTest {

    private final Normalizador normalizador = new Normalizador();

    @Test
    void quitaMayusculasTildesYSignos() {
        assertEquals("inteligencia artificial ia evaluacion",
                normalizador.normalizar("Inteligencia Artificial (IA): ¡Evaluación!"));
    }

    @Test
    void colapsaEspaciosYSeparadores() {
        assertEquals("a b c", normalizador.normalizar("  A--B \t\n C  "));
    }

    @Test
    void conservaLosNumeros() {
        assertEquals("gpt 4 en 2024", normalizador.normalizar("GPT-4 en 2024."));
    }

    @Test
    void textoVacioONuloDaVacio() {
        assertEquals("", normalizador.normalizar(null));
        assertEquals("", normalizador.normalizar(""));
        assertEquals("", normalizador.normalizar("¡¿?!"));
    }
}
