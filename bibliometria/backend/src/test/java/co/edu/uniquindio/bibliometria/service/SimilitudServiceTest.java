package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.api.dto.SimilitudRequest;
import co.edu.uniquindio.bibliometria.api.dto.SimilitudResponse;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import co.edu.uniquindio.bibliometria.infra.embeddings.ModeloOracionProvider;
import co.edu.uniquindio.bibliometria.infra.embeddings.Word2VecProvider;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de R1 sin modelos de IA (solo los cuatro clásicos), con un corpus armado a mano.
 */
class SimilitudServiceTest {

    private static final List<Articulo> ARTICULOS = List.of(
            new Articulo("1", "Uno", List.of(), "generative artificial intelligence assessment", null),
            new Articulo("2", "Dos", List.of(), "generative artificial intelligence evaluation", null),
            new Articulo("3", "Tres", List.of(), "simulation virtual agents", null));

    private static final RepositorioCorpus CORPUS = new RepositorioCorpus() {
        public List<Articulo> todos() {
            return ARTICULOS;
        }

        public Optional<Articulo> porId(String id) {
            return ARTICULOS.stream().filter(a -> a.id().equals(id)).findFirst();
        }
    };

    private final SimilitudService servicio = new SimilitudService(
            new ModeloOracionProvider(Path.of("no-existe")), new Word2VecProvider(Path.of("no-existe.txt")), CORPUS);

    @Test
    void sinModelosSoloListaLosClasicos() {
        assertEquals(List.of("Levenshtein", "NeedlemanWunsch", "Jaccard", "CosenoTfIdf"), servicio.algoritmos());
    }

    @Test
    void comparaTodosLosPares() {
        SimilitudResponse r = servicio.comparar(new SimilitudRequest(List.of("1", "2", "3"), null, false));
        assertEquals(3, r.pares().size());
        SimilitudResponse.Par par12 = r.pares().get(0);
        assertEquals("1", par12.idA());
        assertEquals("2", par12.idB());
        assertEquals(4, par12.resultados().size());
        ResultadoSimilitud jaccard = par12.resultados().get(2);
        assertEquals("Jaccard", jaccard.algoritmo());
        assertEquals(3.0 / 5.0, jaccard.similitud(), 1e-12);
        assertTrue(jaccard.pasos().isEmpty());
    }

    @Test
    void conTrazaCadaResultadoTraePasos() {
        SimilitudResponse r = servicio.comparar(new SimilitudRequest(List.of("1", "2"), List.of("CosenoTfIdf"), true));
        assertEquals(1, r.pares().get(0).resultados().size());
        assertFalse(r.pares().get(0).resultados().get(0).pasos().isEmpty());
    }

    @Test
    void validaEntrada() {
        assertThrows(IllegalArgumentException.class, () -> servicio.comparar(new SimilitudRequest(List.of("1"), null, false)));
        assertThrows(IllegalArgumentException.class, () -> servicio.comparar(new SimilitudRequest(List.of("1", "99"), null, false)));
        assertThrows(IllegalArgumentException.class, () -> servicio.comparar(
                new SimilitudRequest(List.of("1", "2"), List.of("NoExiste"), false)));
        assertThrows(IllegalArgumentException.class, () -> servicio.comparar(
                new SimilitudRequest(List.of("1", "2"), List.of("EmbeddingsOracion"), false)));
    }

    @Test
    void corpusTemporalTraeLosVeinteAbstracts() {
        CorpusTemporalR1 temporal = new CorpusTemporalR1();
        assertEquals(20, temporal.todos().size());
        assertTrue(temporal.porId("11").orElseThrow().abstractCrudo().startsWith("Generative artificial intelligence"));
    }
}
