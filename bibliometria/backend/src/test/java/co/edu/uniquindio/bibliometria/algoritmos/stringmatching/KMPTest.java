package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de KMP con casos de resultado conocido.
 */
class KMPTest extends ContratoStringMatcherTest {

    @Override
    StringMatcher algoritmo() {
        return new KMP();
    }

    @Override
    String nombreEsperado() {
        return "KMP";
    }

    @Test
    void tablaDePrefijos() {
        assertArrayEquals(new int[] {0, 0, 1, 2, 0}, KMP.tablaLps("ababd"));
        assertArrayEquals(new int[] {0, 1, 2, 3}, KMP.tablaLps("aaaa"));
        assertArrayEquals(new int[] {0, 1, 0, 1, 2, 3, 4}, KMP.tablaLps("aabaaba"));
    }

    @Test
    void elTextoNuncaRetrocede() {
        // Sin retroceso: como máximo 2n comparaciones, incluso en el caso repetitivo.
        String texto = "a".repeat(1000);
        assertTrue(new KMP().buscar("aaab", texto).comparaciones() <= 2L * texto.length());
    }
}
