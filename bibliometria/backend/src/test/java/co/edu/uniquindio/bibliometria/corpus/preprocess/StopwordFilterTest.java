package co.edu.uniquindio.bibliometria.corpus.preprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class StopwordFilterTest {

    @Test
    void reconoceStopwordsEnInglesYEspanol() {
        StopwordFilter filtro = new StopwordFilter();
        assertTrue(filtro.esStopword("the"));
        assertTrue(filtro.esStopword("and"));
        assertTrue(filtro.esStopword("de"));
        assertTrue(filtro.esStopword("mas")); // "más" sin tilde, como queda tras normalizar
        assertFalse(filtro.esStopword("algorithm"));
    }

    @Test
    void filtraConservandoElOrden() {
        StopwordFilter filtro = new StopwordFilter();
        assertEquals(List.of("generative", "models"),
                filtro.filtrar(List.of("the", "generative", "of", "models")));
    }

    @Test
    void aceptaUnaListaPropia() {
        StopwordFilter filtro = new StopwordFilter(List.of("Foo", "Bár"));
        assertTrue(filtro.esStopword("foo"));
        assertTrue(filtro.esStopword("bar"));
        assertFalse(filtro.esStopword("the"));
    }
}
