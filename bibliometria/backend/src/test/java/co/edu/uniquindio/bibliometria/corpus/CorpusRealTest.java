package co.edu.uniquindio.bibliometria.corpus;

import co.edu.uniquindio.bibliometria.corpus.preprocess.PreprocesadorTexto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Carga los 20 PDF reales de data/corpus. Los inicios y finales esperados se revisaron contra cada PDF.
 */
class CorpusRealTest {

    private static List<Articulo> articulos;

    private static CorpusLoader cargador(Path carpeta) {
        return new CorpusLoader(carpeta, new PdfExtractor(), new AbstractParser(), new PreprocesadorTexto());
    }

    @BeforeAll
    static void cargar() {
        articulos = cargador(Path.of("data/corpus")).cargar();
    }

    private static Articulo articulo(int numero) {
        return articulos.get(numero - 1);
    }

    @Test
    void cargaLosVeinteEnOrden() {
        assertEquals(20, articulos.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(String.valueOf(i + 1), articulos.get(i).id());
        }
    }

    @Test
    void todosTienenTituloAutoresYAbstract() {
        for (Articulo a : articulos) {
            assertFalse(a.titulo().isBlank(), a.id());
            assertFalse(a.autores().isEmpty(), a.id());
            assertTrue(a.abstractCrudo().split("\\s+").length >= 100, a.id());
            assertFalse(a.abstractProcesado().isBlank(), a.id());
        }
    }

    @Test
    void elTotalDePalabrasEsElVerificado() {
        int total = 0;
        for (Articulo a : articulos) {
            total += a.abstractCrudo().split("\\s+").length;
        }
        assertEquals(3999, total);
    }

    @Test
    void conservaFrasesQueDependenDeLaUnionDeLineas() {
        // 5: la última frase, después de una enumeración larga
        assertTrue(articulo(5).abstractCrudo().endsWith("application of AI technologies with educational theories."));
        assertTrue(articulo(8).abstractCrudo().contains("instance-based"));
        assertTrue(articulo(14).abstractCrudo().contains("well-defined"));
        assertTrue(articulo(17).abstractCrudo().contains("AI-driven"));
        assertTrue(articulo(3).abstractCrudo().contains("pre- and misconceptions"));
    }

    @Test
    void abstractsConMaquetasDificiles() {
        // 7: palabras clave antes del abstract
        assertTrue(articulo(7).abstractCrudo().startsWith("The rapid expansion of Learning Analytics"));
        assertTrue(articulo(7).abstractCrudo().endsWith("future human-centred LA/AIED systems."));
        // 9: sin título de introducción
        assertTrue(articulo(9).abstractCrudo().endsWith("research and development efforts."));
        // 13: abstract estructurado, sin rótulos
        assertTrue(articulo(13).abstractCrudo().startsWith("The purpose of this study"));
        assertFalse(articulo(13).abstractCrudo().contains("Findings –"));
        // 14: columna lateral de Wiley
        assertTrue(articulo(14).abstractCrudo().startsWith("Recent literature underscores"));
        assertTrue(articulo(14).abstractCrudo().endsWith("in their teaching practices."));
    }

    @Test
    void usaLosMetadatosVerificados() {
        assertEquals("Artificial intelligence for education and its emphasis on assessment and adversity quotient: a review",
                articulo(13).titulo());
        assertEquals(List.of("Jyoti Mudkanna Gavhane", "Reena Pagare"), articulo(13).autores());
        assertEquals(4, articulo(1).autores().size());
    }

    @Test
    void elProcesadoEstaNormalizado() {
        assertEquals(new PreprocesadorTexto().normalizar(articulo(1).abstractCrudo()), articulo(1).abstractProcesado());
        assertTrue(articulo(1).abstractProcesado().startsWith("since artificial intelligence ai is finding"));
    }

    @Test
    void elRepositorioBuscaPorId() {
        CorpusRepository repositorio = new CorpusRepository(articulos);
        assertEquals(20, repositorio.todos().size());
        assertEquals(articulo(5), repositorio.porId("5").orElseThrow());
        assertTrue(repositorio.porId("99").isEmpty());
    }

    @Test
    void fallaSiNoExisteLaCarpeta() {
        assertThrows(IllegalStateException.class, () -> cargador(Path.of("no-existe")).cargar());
    }
}
