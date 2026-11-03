package co.edu.uniquindio.bibliometria.algoritmos.stringmatching;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Paso;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.corpus.AbstractParser;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.CorpusLoader;
import co.edu.uniquindio.bibliometria.corpus.PdfExtractor;
import co.edu.uniquindio.bibliometria.corpus.preprocess.PreprocesadorTexto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Genera el caso de estudio de R3 (sección 7.2 del informe) con los algoritmos reales.
 *
 * <p>Ejecutar con: {@code mvn test -Dtest=CasoEstudioR3 -Dcaso.estudio=true}. Escribe
 * {@code ../docs/casos-estudio/r3-busqueda.json}.</p>
 */
class CasoEstudioR3 {

    private static final String PATRON = "intelligence in";
    private static final String ARTICULO = "2";
    private static final String FRASE = "artificial intelligence in education";
    private static final Path SALIDA = Path.of("../docs/casos-estudio/r3-busqueda.json");

    @Test
    void generar() throws Exception {
        assumeTrue(Boolean.getBoolean("caso.estudio"), "Solo se ejecuta con -Dcaso.estudio=true");

        List<Articulo> corpus = new CorpusLoader(Path.of("data/corpus"), new PdfExtractor(), new AbstractParser(),
                new PreprocesadorTexto()).cargar();
        String abstractCompleto = corpus.stream().filter(a -> a.id().equals(ARTICULO)).findFirst().orElseThrow()
                .abstractProcesado();

        Map<String, Object> salida = new LinkedHashMap<>();
        salida.put("patron", PATRON);
        salida.put("largoPatron", PATRON.length());
        salida.put("lps", KMP.tablaLps(PATRON));
        salida.put("sufijoBueno", BoyerMoore.tablaSufijoBueno(PATRON));
        salida.put("articulo", ARTICULO);
        salida.put("abstract", abstractCompleto);

        // Fragmento corto: la frase real del abstract, para seguir la traza a mano.
        salida.put("frase", FRASE);
        salida.put("fraseKmp", ejecutar(new KMP(), PATRON, FRASE));
        salida.put("fraseBm", ejecutar(new BoyerMoore(), PATRON, FRASE));
        salida.put("fraseRk", ejecutar(new RabinKarp(), PATRON, FRASE));
        salida.put("fraseRkPrimoPequeno", ejecutar(new RabinKarp(256, 101), PATRON, FRASE));

        // Abstract completo: serie de desplazamientos para la gráfica.
        salida.put("abstractKmp", ejecutar(new KMP(), PATRON, abstractCompleto));
        salida.put("abstractBm", ejecutar(new BoyerMoore(), PATRON, abstractCompleto));
        salida.put("abstractRk", ejecutar(new RabinKarp(), PATRON, abstractCompleto));
        salida.put("abstractRkPrimoPequeno", ejecutar(new RabinKarp(256, 101), PATRON, abstractCompleto));

        // Casos extremos, solo comparaciones de caracteres (no dependen de la máquina).
        salida.put("extremos", extremos());

        ObjectMapper json = new ObjectMapper();
        Files.createDirectories(SALIDA.getParent());
        json.writerWithDefaultPrettyPrinter().writeValue(SALIDA.toFile(), salida);
    }

    private static Map<String, Object> ejecutar(StringMatcher algoritmo, String patron, String texto) {
        ResultadoBusqueda r = algoritmo.buscar(patron, texto, Traza.activada());
        List<Map<String, Object>> pasos = new ArrayList<>();
        int colisiones = 0;
        for (Paso p : r.pasos()) {
            Map<String, Object> paso = new LinkedHashMap<>();
            paso.put("numero", p.numero());
            paso.put("descripcion", p.descripcion());
            paso.put("datos", p.datos());
            pasos.add(paso);
            if (p.descripcion().startsWith("Colisión")) {
                colisiones++;
            }
        }
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("largoTexto", texto.length());
        m.put("posiciones", r.posiciones());
        m.put("comparaciones", r.comparaciones());
        m.put("colisiones", colisiones);
        m.put("pasos", pasos);
        return m;
    }

    private static List<Map<String, Object>> extremos() {
        int n = 10_000;
        String todoA = "a".repeat(n);
        List<Map<String, Object>> filas = new ArrayList<>();
        filas.add(fila("Texto repetitivo, patrón que aparece siempre", "a".repeat(100), todoA));
        filas.add(fila("Texto repetitivo, patrón que falla al final", "a".repeat(99) + "b", todoA));
        filas.add(fila("Texto repetitivo, patrón que falla al inicio", "b" + "a".repeat(99), todoA));
        filas.add(fila("Carácter ausente en el texto", "abcdefghij".repeat(10), "x".repeat(n)));
        return filas;
    }

    private static Map<String, Object> fila(String caso, String patron, String texto) {
        StringMatcher[] algoritmos = {new KMP(), new BoyerMoore(), new RabinKarp()};
        Map<String, Object> fila = new LinkedHashMap<>();
        fila.put("caso", caso);
        fila.put("n", texto.length());
        fila.put("m", patron.length());
        for (StringMatcher a : algoritmos) {
            ResultadoBusqueda r = a.buscar(patron, texto);
            fila.put(a.nombre(), r.comparaciones());
            fila.put("ocurrencias", r.posiciones().size());
        }
        return fila;
    }
}
