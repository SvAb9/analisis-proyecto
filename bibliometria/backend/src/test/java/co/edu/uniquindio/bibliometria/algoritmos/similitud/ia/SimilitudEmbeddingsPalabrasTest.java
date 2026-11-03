package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.infra.embeddings.Word2VecProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class SimilitudEmbeddingsPalabrasTest {

    @TempDir
    Path tmp;

    private Word2VecProvider proveedor(String contenido) throws IOException {
        Path archivo = tmp.resolve("vectores.txt");
        Files.writeString(archivo, contenido);
        return new Word2VecProvider(archivo);
    }

    @Test
    void promedioDeLosVectoresConocidos() throws IOException {
        Word2VecProvider p = proveedor("assessment 1 0\nevaluation 0.8 0.6\nchatbot 0 1\n");
        assertEquals(2, p.dimension());
        Word2VecProvider.Detalle e = p.vectorizarConDetalle("assessment and chatbot and unknownword");
        assertArrayEquals(new double[] {0.5, 0.5}, e.vector(), 1e-12);
        assertEquals(2, e.palabrasConVector());
        assertEquals(3, e.palabras());
        assertNull(p.vectorDe("unknownword"));
    }

    @Test
    void aceptaEncabezadoWord2Vec() throws IOException {
        Word2VecProvider p = proveedor("2 2\nassessment 1 0\nchatbot 0 1\n");
        assertEquals(2, p.tamanoVocabulario());
    }

    @Test
    void sinonimosSeParecenAunqueNoCompartanPalabras() throws IOException {
        Word2VecProvider p = proveedor("assessment 1 0\nevaluation 0.8 0.6\nchatbot 0 1\n");
        ResultadoSimilitud r = new SimilitudEmbeddingsPalabras(p).comparar("assessment", "evaluation");
        assertEquals(0.8, r.similitud(), 1e-12);
    }

    @Test
    void conGloveDelCorpus() {
        Path glove = Path.of("data/modelos/glove/glove.6B.100d.corpus.txt");
        assumeTrue(Files.isRegularFile(glove), "vectores no disponibles");
        Word2VecProvider p = new Word2VecProvider(glove);
        assertEquals(100, p.dimension());
        SimilitudEmbeddingsPalabras s = new SimilitudEmbeddingsPalabras(p);
        double cercanas = s.comparar("assessment", "evaluation").similitud();
        double lejanas = s.comparar("assessment", "geographic").similitud();
        assertTrue(cercanas > lejanas, cercanas + " vs " + lejanas);
    }
}
