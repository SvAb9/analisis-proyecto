package co.edu.uniquindio.bibliometria.corpus.preprocess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TokenizadorTest {

    private final Tokenizador tokenizador = new Tokenizador();

    @Test
    void separaPorCualquierCaracterQueNoSeaLetraODigito() {
        assertEquals(List.of("hello", "world", "2024"), tokenizador.tokenizar("Hello, world 2024!"));
    }

    @Test
    void textoNuloOVacioDaListaVacia() {
        assertTrue(tokenizador.tokenizar(null).isEmpty());
        assertTrue(tokenizador.tokenizar("   ").isEmpty());
    }
}
