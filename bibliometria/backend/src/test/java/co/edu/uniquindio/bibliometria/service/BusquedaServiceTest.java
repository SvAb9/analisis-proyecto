package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.api.dto.BusquedaRequest;
import co.edu.uniquindio.bibliometria.api.dto.BusquedaResponse;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.CorpusRepository;
import co.edu.uniquindio.bibliometria.corpus.preprocess.PreprocesadorTexto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas del servicio de R3 con un corpus armado a mano.
 */
class BusquedaServiceTest {

    private final BusquedaService servicio = new BusquedaService(new CorpusRepository(List.of(
            new Articulo("1", "Uno", List.of(), "Generative AI", "generative ai and generative models"),
            new Articulo("2", "Dos", List.of(), "Otro", "otro texto sin el termino"),
            new Articulo("3", "Tres", List.of(), "Generative", "generative"))), new PreprocesadorTexto());

    @Test
    void losTresAlgoritmosEncuentranLoMismoPorArticulo() {
        BusquedaResponse r = servicio.buscar(new BusquedaRequest("generative", false, null));
        assertEquals(3, r.resultados().size());
        assertEquals(List.of("KMP", "BoyerMoore", "RabinKarp"),
                r.resultados().stream().map(BusquedaResponse.Resultado::algoritmo).toList());
        for (BusquedaResponse.Resultado resultado : r.resultados()) {
            assertEquals(3, resultado.totalOcurrencias());
            assertEquals(List.of(new BusquedaResponse.Coincidencia("1", List.of(0, 18)),
                    new BusquedaResponse.Coincidencia("3", List.of(0))), resultado.porArticulo());
            assertNull(resultado.traza());
        }
        assertNull(r.articuloTraza());
    }

    @Test
    void normalizaElPatronIgualQueLosAbstracts() {
        BusquedaResponse r = servicio.buscar(new BusquedaRequest("  GENERATIVE-ÁI! ", false, null));
        assertEquals("generative ai", r.patron());
        assertEquals(1, r.resultados().get(0).totalOcurrencias());
    }

    @Test
    void laTrazaUsaElPrimerArticuloConOcurrenciasOElPedido() {
        BusquedaResponse automatico = servicio.buscar(new BusquedaRequest("termino", true, null));
        assertEquals("2", automatico.articuloTraza());
        BusquedaResponse pedido = servicio.buscar(new BusquedaRequest("generative", true, "3"));
        assertEquals("3", pedido.articuloTraza());
        for (BusquedaResponse.Resultado resultado : pedido.resultados()) {
            assertNotNull(resultado.traza());
            assertEquals(resultado.algoritmo(), resultado.traza().algoritmo());
            assertFalse(resultado.traza().pasos().isEmpty());
        }
    }

    @Test
    void rechazaPatronesVaciosYArticulosInexistentes() {
        assertThrows(IllegalArgumentException.class, () -> servicio.buscar(new BusquedaRequest("  ", false, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.buscar(new BusquedaRequest("¿?!", false, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.buscar(new BusquedaRequest(null, false, null)));
        assertThrows(IllegalArgumentException.class, () -> servicio.buscar(new BusquedaRequest("a", true, "99")));
    }
}
