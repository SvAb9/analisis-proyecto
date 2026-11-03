package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de BoyerMoore con casos de resultado conocido.
 */
class BoyerMooreTest extends ContratoStringMatcherTest {

    @Override
    StringMatcher algoritmo() {
        return new BoyerMoore();
    }

    @Override
    String nombreEsperado() {
        return "BoyerMoore";
    }

    @Test
    void tablaDeSufijoBueno() {
        // Ejemplo clásico: "abbabab" (desplazamiento[j + 1] al fallar en j).
        assertArrayEquals(new int[] {5, 5, 5, 5, 2, 5, 4, 1}, BoyerMoore.tablaSufijoBueno("abbabab"));
    }

    @Test
    void conPatronLargoYCaracteresAusentesSaltaSinCompararTodo() {
        String texto = "x".repeat(10_000);
        long comparaciones = new BoyerMoore().buscar("abcdefghij", texto).comparaciones();
        assertEquals(1000, comparaciones);
    }

    @Test
    void trazaMuestraLasDosTablasYElDesplazamiento() {
        var pasos = new BoyerMoore().buscar("abra", "xxabraxx", Traza.activada()).pasos();
        assertTrue(pasos.get(0).descripcion().startsWith("Tabla de carácter malo"));
        assertTrue(pasos.get(1).descripcion().startsWith("Tabla de sufijo bueno"));
        assertTrue(pasos.stream().anyMatch(p -> p.datos().contains("caracterMalo=")));
    }
}
