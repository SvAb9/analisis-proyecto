package co.edu.uniquindio.bibliometria.infra.embeddings;

import co.edu.uniquindio.bibliometria.util.VectorMath;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ModeloOracionProviderTest {

    private static final Path MODELO = Path.of("data/modelos/all-MiniLM-L6-v2");

    @Test
    void reproduceLosValoresDeReferencia() throws Exception {
        assumeTrue(Files.isRegularFile(MODELO.resolve("model.onnx")), "modelo no descargado");
        try (ModeloOracionProvider p = new ModeloOracionProvider(MODELO)) {
            double[] a = p.vectorizar("The weather is lovely today.");
            double[] b = p.vectorizar("It's so sunny outside!");
            double[] c = p.vectorizar("He drove to the stadium.");
            assertEquals(0.6660, VectorMath.coseno(a, b), 5e-4);
            assertEquals(0.1046, VectorMath.coseno(a, c), 5e-4);
            assertEquals(0.1411, VectorMath.coseno(b, c), 5e-4);
        }
    }
}
