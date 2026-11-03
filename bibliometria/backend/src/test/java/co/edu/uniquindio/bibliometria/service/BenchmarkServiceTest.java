package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.api.dto.BenchmarkResponse;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.CorpusRepository;
import co.edu.uniquindio.bibliometria.corpus.preprocess.PreprocesadorTexto;
import co.edu.uniquindio.bibliometria.infra.medicion.Cronometro;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas de BenchmarkService. */
class BenchmarkServiceTest {

    private final BenchmarkService servicio = new BenchmarkService(new CorpusRepository(List.of(
            new Articulo("1", "Uno", List.of(), "x", "generative ai and generative models"),
            new Articulo("2", "Dos", List.of(), "y", "otro texto"))), new PreprocesadorTexto());

    @Test
    void mideLosTresAlgoritmos() {
        BenchmarkResponse r = servicio.medir("Generative", 5);
        assertEquals("generative", r.patron());
        assertEquals(10, r.largoPatron());
        assertEquals(2, r.articulos());
        assertEquals(45, r.caracteres());
        assertEquals(5, r.repeticiones());
        assertEquals(List.of("KMP", "BoyerMoore", "RabinKarp"),
                r.mediciones().stream().map(BenchmarkResponse.Medicion::algoritmo).toList());
        for (BenchmarkResponse.Medicion m : r.mediciones()) {
            assertEquals(2, m.ocurrencias());
            assertTrue(m.comparaciones() > 0);
            assertTrue(m.minimoNs() <= m.maximoNs());
        }
    }

    @Test
    void validaLaEntrada() {
        assertThrows(IllegalArgumentException.class, () -> servicio.medir(" ", 5));
        assertThrows(IllegalArgumentException.class, () -> servicio.medir("a", 0));
        assertThrows(IllegalArgumentException.class, () -> servicio.medir("a", 1002));
    }

    @Test
    void medianaDelCronometro() {
        assertEquals(3, Cronometro.mediana(new long[] {9, 1, 3}));
        assertEquals(2, Cronometro.mediana(new long[] {1, 3}));
        assertThrows(IllegalArgumentException.class, () -> Cronometro.mediana(new long[0]));
    }
}
