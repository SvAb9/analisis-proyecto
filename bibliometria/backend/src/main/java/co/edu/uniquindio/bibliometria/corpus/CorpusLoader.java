package co.edu.uniquindio.bibliometria.corpus;

import co.edu.uniquindio.bibliometria.corpus.preprocess.Preprocesador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Carga y procesa todo el corpus una sola vez al iniciar.
 *
 * <p>El id de cada artículo es el número inicial del nombre del PDF ("7. Human-centred....pdf" da "7").
 * Título y autores salen de {@code corpus/metadatos_articulos.tsv}, transcritos de la primera página y
 * verificados a mano, porque los metadatos internos de los PDF están incompletos; si un artículo no
 * figura ahí, se usan los del PDF. El abstract procesado es el abstract normalizado, sin stemming,
 * porque los modelos de embeddings necesitan las palabras completas.</p>
 *
 * <p>Si falta la carpeta o un PDF no se puede leer, falla al arrancar en lugar de servir un corpus incompleto.</p>
 */
@Component
public class CorpusLoader {

    private static final Logger log = LoggerFactory.getLogger(CorpusLoader.class);
    private static final String METADATOS = "/corpus/metadatos_articulos.tsv";

    private final Path carpeta;
    private final PdfExtractor extractor;
    private final AbstractParser parser;
    private final Preprocesador preprocesador;

    @Autowired
    public CorpusLoader(@Value("${app.corpus-path}") String carpeta, PdfExtractor extractor,
                        AbstractParser parser, Preprocesador preprocesador) {
        this(Path.of(carpeta), extractor, parser, preprocesador);
    }

    public CorpusLoader(Path carpeta, PdfExtractor extractor, AbstractParser parser, Preprocesador preprocesador) {
        this.carpeta = carpeta;
        this.extractor = extractor;
        this.parser = parser;
        this.preprocesador = preprocesador;
    }

    /** Lee los PDF de la carpeta y devuelve los artículos ordenados por número. */
    public List<Articulo> cargar() {
        Map<String, String[]> verificados = leerMetadatos();
        List<Articulo> articulos = new ArrayList<>();
        for (Path pdf : listarPdf()) {
            articulos.add(cargarArticulo(pdf, verificados));
        }
        log.info("Corpus cargado: {} artículos desde {}", articulos.size(), carpeta.toAbsolutePath());
        return articulos;
    }

    private Articulo cargarArticulo(Path pdf, Map<String, String[]> verificados) {
        String archivo = pdf.getFileName().toString();
        try {
            PdfExtractor.ContenidoPdf contenido = extractor.extraer(pdf);
            String resumen = parser.extraerAbstract(contenido.texto());
            if (resumen.isEmpty()) {
                throw new IllegalStateException("No se encontró el abstract en " + archivo);
            }
            String id = String.valueOf(numero(archivo));
            String[] meta = verificados.get(id);
            String titulo = meta != null ? meta[0] : parser.titulo(contenido.tituloInterno(), archivo);
            List<String> autores = parser.autores(meta != null ? meta[1] : contenido.autoresInternos());
            return new Articulo(id, titulo, List.copyOf(autores), resumen, preprocesador.normalizar(resumen));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + archivo, e);
        }
    }

    private List<Path> listarPdf() {
        if (!Files.isDirectory(carpeta)) {
            throw new IllegalStateException("No existe la carpeta del corpus: " + carpeta.toAbsolutePath());
        }
        List<Path> pdfs = new ArrayList<>();
        try (Stream<Path> archivos = Files.list(carpeta)) {
            archivos.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".pdf")).forEach(pdfs::add);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo listar " + carpeta.toAbsolutePath(), e);
        }
        if (pdfs.isEmpty()) {
            throw new IllegalStateException("No hay PDF en la carpeta del corpus: " + carpeta.toAbsolutePath());
        }
        pdfs.sort(Comparator.comparingInt(p -> numero(p.getFileName().toString())));
        return pdfs;
    }

    /** Número inicial del nombre del archivo. */
    private int numero(String archivo) {
        int punto = archivo.indexOf('.');
        try {
            return Integer.parseInt(archivo.substring(0, punto).trim());
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new IllegalStateException("El PDF debe empezar por su número (\"7. Título.pdf\"): " + archivo, e);
        }
    }

    /** Lee "id \t título \t autores"; ignora comentarios y el encabezado. */
    private Map<String, String[]> leerMetadatos() {
        Map<String, String[]> metadatos = new HashMap<>();
        try (InputStream in = getClass().getResourceAsStream(METADATOS)) {
            if (in == null) {
                log.warn("No se encontró {}; se usarán los metadatos de los PDF", METADATOS);
                return metadatos;
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] campos = linea.split("\t");
                if (!linea.startsWith("#") && campos.length >= 3 && campos[0].trim().matches("\\d+")) {
                    metadatos.put(campos[0].trim(), new String[] {campos[1].trim(), campos[2].trim()});
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer " + METADATOS, e);
        }
        return metadatos;
    }
}
