package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas comunes a los tres algoritmos de búsqueda. */
abstract class ContratoStringMatcherTest {

    abstract StringMatcher algoritmo();

    abstract String nombreEsperado();

    private List<Integer> posiciones(String patron, String texto) {
        return algoritmo().buscar(patron, texto).posiciones();
    }

    private static List<Integer> fuerzaBruta(String patron, String texto) {
        List<Integer> resultado = new ArrayList<>();
        for (int s = 0; s + patron.length() <= texto.length(); s++) {
            int j = 0;
            while (j < patron.length() && texto.charAt(s + j) == patron.charAt(j)) {
                j++;
            }
            if (j == patron.length()) {
                resultado.add(s);
            }
        }
        return resultado;
    }

    @Test
    void nombreEstable() {
        assertEquals(nombreEsperado(), algoritmo().nombre());
        assertEquals(nombreEsperado(), algoritmo().buscar("a", "a").algoritmo());
    }

    @Test
    void ocurrenciasConocidas() {
        assertEquals(List.of(0, 7), posiciones("abra", "abracadabra"));
        assertEquals(List.of(10), posiciones("ababd", "ababcabcabababd"));
        assertEquals(List.of(4), posiciones("needle", "the needle"));
    }

    @Test
    void ocurrenciasSolapadas() {
        assertEquals(List.of(0, 1, 2), posiciones("aa", "aaaa"));
        assertEquals(List.of(0, 2, 4), posiciones("aba", "abababa"));
    }

    @Test
    void alInicioAlFinalYTextoIgualAlPatron() {
        assertEquals(List.of(0), posiciones("ab", "abc"));
        assertEquals(List.of(1), posiciones("bc", "abc"));
        assertEquals(List.of(0), posiciones("abc", "abc"));
    }

    @Test
    void sinOcurrencias() {
        assertEquals(List.of(), posiciones("xyz", "abcdef"));
        assertEquals(List.of(), posiciones("abcdefg", "abc"));
        assertEquals(List.of(), posiciones("a", ""));
        assertEquals(List.of(), posiciones("", "abc"));
    }

    @Test
    void caracteresFueraDeAscii() {
        assertEquals(List.of(1, 5), posiciones("ñπ", "añπbcñπ"));
        assertEquals(List.of(), posiciones("ñπ", "abcdef"));
        assertEquals(List.of(3), posiciones("cd", "ñπ cd"));
    }

    @Test
    void igualQueLaFuerzaBrutaEnEntradasAleatorias() {
        Random azar = new Random(42);
        for (int prueba = 0; prueba < 2000; prueba++) {
            int alfabeto = 1 + azar.nextInt(4);
            String texto = aleatorio(azar, azar.nextInt(200), alfabeto);
            String patron = azar.nextBoolean() || texto.length() < 2
                    ? aleatorio(azar, 1 + azar.nextInt(8), alfabeto)
                    : tomarDe(azar, texto);
            assertEquals(fuerzaBruta(patron, texto), posiciones(patron, texto),
                    "patrón=" + patron + " texto=" + texto);
        }
    }

    @Test
    void cuentaComparaciones() {
        assertTrue(algoritmo().buscar("abra", "abracadabra").comparaciones() > 0);
    }

    @Test
    void trazaDesactivadaNoGuardaPasos() {
        assertTrue(algoritmo().buscar("abra", "abracadabra").pasos().isEmpty());
    }

    @Test
    void trazaActivaRegistraPasosSinCambiarElResultado() {
        ResultadoBusqueda r = algoritmo().buscar("abra", "abracadabra", Traza.activada());
        assertEquals(List.of(0, 7), r.posiciones());
        assertFalse(r.pasos().isEmpty());
        assertTrue(r.pasos().stream().anyMatch(p -> p.descripcion().startsWith("Ocurrencia en la posición 7")));
    }

    private static String aleatorio(Random azar, int largo, int alfabeto) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < largo; i++) {
            sb.append((char) ('a' + azar.nextInt(alfabeto)));
        }
        return sb.toString();
    }

    private static String tomarDe(Random azar, String texto) {
        int inicio = azar.nextInt(texto.length() - 1);
        int fin = inicio + 1 + azar.nextInt(Math.min(8, texto.length() - inicio));
        return texto.substring(inicio, Math.min(fin, texto.length()));
    }
}
