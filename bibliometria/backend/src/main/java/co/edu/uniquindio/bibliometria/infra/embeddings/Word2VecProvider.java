package co.edu.uniquindio.bibliometria.infra.embeddings;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.PreparadorTexto;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.EmbeddingProvider;
import co.edu.uniquindio.bibliometria.util.estructuras.ArbolBinarioBusqueda;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Adaptador a vectores preentrenados.
 *
 * <p>Lee un archivo de texto con una palabra y sus componentes por línea (formato GloVe, o
 * word2vec de texto con la línea de encabezado "n d") y guarda el vocabulario en un
 * {@link ArbolBinarioBusqueda} palabra → vector. El vector del abstract es el promedio de los
 * vectores de sus palabras de contenido; las que no están en el vocabulario se ignoran y se
 * informan en la nota (cobertura).</p>
 *
 * <p>Carga O(V·(d + h)); vectorizar un texto de n tokens O(n·(h + d)).</p>
 */
public class Word2VecProvider implements EmbeddingProvider {

    private static final Logger log = LoggerFactory.getLogger(Word2VecProvider.class);

    private final String modelo;
    private final ArbolBinarioBusqueda<String, double[]> vocabulario = new ArbolBinarioBusqueda<>();
    private int dimension;

    public Word2VecProvider(Path archivo) {
        this.modelo = archivo.getFileName().toString();
        if (Files.isRegularFile(archivo)) {
            cargar(archivo);
        } else {
            log.warn("No se encontró el archivo de vectores {}; embeddings de palabras no disponibles", archivo);
        }
    }

    private void cargar(Path archivo) {
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            boolean primera = true;
            while ((linea = lector.readLine()) != null) {
                String[] partes = linea.trim().split(" ");
                if (primera && partes.length == 2) {
                    primera = false;
                    continue;
                }
                primera = false;
                double[] vector = new double[partes.length - 1];
                for (int i = 1; i < partes.length; i++) {
                    vector[i - 1] = Double.parseDouble(partes[i]);
                }
                if (dimension == 0) {
                    dimension = vector.length;
                }
                if (vector.length == dimension) {
                    vocabulario.insertar(partes[0], vector);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + archivo, e);
        }
        vocabulario.rebalancear();
        log.info("Vectores de palabras cargados: {} palabras, dimensión {}", vocabulario.getCantidad(), dimension);
    }

    @Override
    public String modelo() {
        return modelo;
    }

    /** 0 si el archivo de vectores no se encontró. */
    @Override
    public int dimension() {
        return disponible() ? dimension : 0;
    }

    public boolean disponible() {
        return vocabulario.getCantidad() > 0;
    }

    public int tamanoVocabulario() {
        return vocabulario.getCantidad();
    }

    /** Vector de una palabra o null si no está en el vocabulario. */
    public double[] vectorDe(String palabra) {
        return vocabulario.obtener(palabra);
    }

    /** Vector promedio y cobertura: cuántas palabras de contenido tenían vector. */
    public record Detalle(double[] vector, int palabrasConVector, int palabras) {}

    @Override
    public double[] vectorizar(String texto) {
        return vectorizarConDetalle(texto).vector();
    }

    public Detalle vectorizarConDetalle(String texto) {
        String[] tokens = PreparadorTexto.tokensContenido(texto);
        double[] suma = new double[dimension];
        int encontrados = 0;
        for (String token : tokens) {
            double[] vector = vocabulario.obtener(token);
            if (vector != null) {
                for (int i = 0; i < dimension; i++) {
                    suma[i] += vector[i];
                }
                encontrados++;
            }
        }
        if (encontrados > 0) {
            for (int i = 0; i < dimension; i++) {
                suma[i] /= encontrados;
            }
        }
        return new Detalle(suma, encontrados, tokens.length);
    }
}
