package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.infra.embeddings.ModeloOracionProvider;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Pruebas de la similitud con embeddings de oración: la matemática con vectores fijos y,
 * si el modelo está descargado, el comportamiento con MiniLM real.
 */
class SimilitudEmbeddingsOracionTest {

    private static final Path MODELO = Path.of("data/modelos/all-MiniLM-L6-v2");

    @Test
    void cosenoYEuclidianaConVectoresFijos() {
        ProveedorFijo fijo = new ProveedorFijo(Map.of("a", new double[] {3, 4}, "b", new double[] {4, 3}));
        ResultadoSimilitud r = new SimilitudEmbeddingsOracion(fijo).comparar("a", "b");
        assertEquals(24.0 / 25.0, r.similitud(), 1e-12);
        assertEquals(Math.sqrt(2), r.valorCrudo(), 1e-12);
    }

    @Test
    void cosenoNegativoSeRecortaACero() {
        ProveedorFijo fijo = new ProveedorFijo(Map.of("a", new double[] {1, 0}, "b", new double[] {-1, 0}));
        assertEquals(0.0, new SimilitudEmbeddingsOracion(fijo).comparar("a", "b").similitud(), 1e-12);
    }

    @Test
    void sinModeloNoEstaDisponible() {
        ModeloOracionProvider proveedor = new ModeloOracionProvider(Path.of("no-existe/modelo"));
        SimilitudEmbeddingsOracion similitud = new SimilitudEmbeddingsOracion(proveedor);
        assertFalse(similitud.disponible());
        assertThrows(IllegalStateException.class, () -> similitud.comparar("a", "b"));
    }

    @Test
    void conMiniLMLasParafrasisSeParecenMas() throws Exception {
        assumeTrue(Files.isRegularFile(MODELO.resolve("model.onnx")), "modelo no descargado");
        try (ModeloOracionProvider proveedor = new ModeloOracionProvider(MODELO)) {
            assertEquals(384, proveedor.dimension());
            SimilitudEmbeddingsOracion s = new SimilitudEmbeddingsOracion(proveedor);
            double parafrasis = s.comparar("Generative AI is changing how students are assessed.",
                    "ChatGPT transforms evaluation in universities.").similitud();
            double distinto = s.comparar("Generative AI is changing how students are assessed.",
                    "The recipe needs two cups of flour.").similitud();
            assertTrue(parafrasis > distinto, parafrasis + " vs " + distinto);
            assertEquals(1.0, s.comparar("same text", "same text").similitud(), 1e-6);
            double norma = 0;
            for (double x : proveedor.vectorizar("assessment")) {
                norma += x * x;
            }
            assertEquals(1.0, norma, 1e-6);
        }
    }

    @Test
    void textoLargoSeParteEnVentanas() throws Exception {
        assumeTrue(Files.isRegularFile(MODELO.resolve("model.onnx")), "modelo no descargado");
        try (ModeloOracionProvider proveedor = new ModeloOracionProvider(MODELO)) {
            String largo = "artificial intelligence in higher education assessment ".repeat(80);
            assertEquals(2, proveedor.vectorizarConDetalle(largo).ventanas());
        }
    }
}
