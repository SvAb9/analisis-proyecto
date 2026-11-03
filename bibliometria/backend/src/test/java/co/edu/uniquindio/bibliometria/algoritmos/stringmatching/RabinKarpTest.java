package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de RabinKarp con casos de resultado conocido.
 */
class RabinKarpTest extends ContratoStringMatcherTest {

    @Override
    StringMatcher algoritmo() {
        return new RabinKarp();
    }

    @Override
    String nombreEsperado() {
        return "RabinKarp";
    }

    @Test
    void lasColisionesSeVerificanYNoDanFalsasOcurrencias() {
        // Con módulo 3 varias ventanas distintas del patrón dan el mismo hash: son colisiones que la verificación descarta.
        ResultadoBusqueda r = new RabinKarp(256, 3).buscar("abc", "abcxbcaabd", Traza.activada());
        assertEquals(List.of(0), r.posiciones());
        assertTrue(r.pasos().stream().anyMatch(p -> p.descripcion().startsWith("Colisión")));
    }

    @Test
    void sinColisionesSoloComparaAlVerificarLaOcurrencia() {
        ResultadoBusqueda r = new RabinKarp().buscar("abra", "abracadabra");
        assertEquals(8, r.comparaciones());
    }

    @Test
    void elHashRodanteFuncionaConCaracteresGrandes() {
        assertEquals(List.of(2), new RabinKarp().buscar("￿￾", "ab￿￾cd").posiciones());
    }

    @Test
    void rechazaParametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new RabinKarp(1, 7));
        assertThrows(IllegalArgumentException.class, () -> new RabinKarp(256, 1));
    }
}
