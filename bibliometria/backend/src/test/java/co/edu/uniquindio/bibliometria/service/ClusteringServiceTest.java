package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.api.dto.ClusteringRequest;
import co.edu.uniquindio.bibliometria.api.dto.ClusteringResponse;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import co.edu.uniquindio.bibliometria.infra.embeddings.ModeloOracionProvider;
import co.edu.uniquindio.bibliometria.infra.embeddings.Word2VecProvider;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas del servicio de R2 sin modelos de IA, con un corpus armado a mano: dos artículos
 * sobre IA generativa (1 y 2) y dos sobre simulación (3 y 4).
 */
class ClusteringServiceTest {

    private static RepositorioCorpus corpus(List<Articulo> articulos) {
        return new RepositorioCorpus() {
            public List<Articulo> todos() {
                return articulos;
            }

            public Optional<Articulo> porId(String id) {
                return articulos.stream().filter(a -> a.id().equals(id)).findFirst();
            }
        };
    }

    private static final List<Articulo> ARTICULOS = List.of(
            new Articulo("1", "Uno", List.of(), "generative artificial intelligence assessment tools", null),
            new Articulo("2", "Dos", List.of(), "generative artificial intelligence evaluation tools", null),
            new Articulo("3", "Tres", List.of(), "simulation virtual agents training", null),
            new Articulo("4", "Cuatro", List.of(), "simulation virtual agents learning", null));

    private static ClusteringService servicio(List<Articulo> articulos) {
        RepositorioCorpus corpus = corpus(articulos);
        SimilitudService similitud = new SimilitudService(
                new ModeloOracionProvider(Path.of("no-existe")), new Word2VecProvider(Path.of("no-existe.txt")), corpus);
        return new ClusteringService(similitud, corpus);
    }

    private static Set<String> hojas(ClusteringResponse.Nodo nodo) {
        Set<String> ids = new HashSet<>();
        if (nodo.izquierda() == null) {
            ids.add(nodo.articuloId());
        } else {
            ids.addAll(hojas(nodo.izquierda()));
            ids.addAll(hojas(nodo.derecha()));
        }
        return ids;
    }

    @Test
    void devuelveUnArbolPorCadaCriterio() {
        ClusteringResponse r = servicio(ARTICULOS).agrupar(new ClusteringRequest("Jaccard"));
        assertEquals(3, r.resultados().size());
        assertEquals("Single", r.resultados().get(0).criterio());
        assertEquals("Complete", r.resultados().get(1).criterio());
        assertEquals("Average", r.resultados().get(2).criterio());
        for (ClusteringResponse.Resultado resultado : r.resultados()) {
            assertNotNull(resultado.arbol());
            assertEquals(Set.of("1", "2", "3", "4"), hojas(resultado.arbol()));
        }
    }

    @Test
    void separaLosDosGruposTematicos() {
        ClusteringResponse.Nodo raiz = servicio(ARTICULOS).agrupar(new ClusteringRequest("Jaccard"))
                .resultados().get(0).arbol();
        assertEquals(Set.of(hojas(raiz.izquierda()), hojas(raiz.derecha())),
                Set.of(Set.of("1", "2"), Set.of("3", "4")));
    }

    @Test
    void lasMetricasEstanEnRango() {
        for (ClusteringResponse.Resultado r : servicio(ARTICULOS).agrupar(new ClusteringRequest("Jaccard")).resultados()) {
            assertTrue(r.silhouette() >= -1.0 && r.silhouette() <= 1.0);
            assertTrue(r.correlacionCofenetica() >= -1.0 && r.correlacionCofenetica() <= 1.0);
            assertTrue(r.tiempoNs() >= 0);
        }
    }

    @Test
    void sinAlgoritmoUsaCosenoTfIdf() {
        ClusteringResponse r = servicio(ARTICULOS).agrupar(null);
        assertEquals(3, r.resultados().size());
    }

    @Test
    void validaEntrada() {
        ClusteringService servicio = servicio(ARTICULOS);
        assertThrows(IllegalArgumentException.class, () -> servicio.agrupar(new ClusteringRequest("NoExiste")));
        assertThrows(IllegalArgumentException.class, () -> servicio.agrupar(new ClusteringRequest("EmbeddingsOracion")));
        assertThrows(IllegalArgumentException.class, () -> servicio(ARTICULOS.subList(0, 2)).agrupar(null));
    }
}