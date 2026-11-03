package co.edu.uniquindio.bibliometria.algoritmos.similitud;

import co.edu.uniquindio.bibliometria.corpus.AbstractParser;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.CorpusLoader;
import co.edu.uniquindio.bibliometria.corpus.PdfExtractor;
import co.edu.uniquindio.bibliometria.corpus.preprocess.PreprocesadorTexto;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Genera scripts/vocabulario-corpus.txt: mvn test -Dtest=VocabularioGlove -Dcaso.estudio=true */
class VocabularioGlove {

    @Test
    void generar() throws Exception {
        assumeTrue(Boolean.getBoolean("caso.estudio"), "Solo se ejecuta con -Dcaso.estudio=true");
        TreeSet<String> palabras = new TreeSet<>();
        for (Articulo articulo : new CorpusLoader(Path.of("data/corpus"), new PdfExtractor(), new AbstractParser(),
                new PreprocesadorTexto()).cargar()) {
            String texto = PreparadorTexto.normalizar(articulo.titulo() + " " + articulo.abstractCrudo());
            for (String palabra : texto.split(" ")) {
                palabras.add(palabra);
            }
        }
        Files.write(Path.of("scripts/vocabulario-corpus.txt"), palabras);
    }
}
