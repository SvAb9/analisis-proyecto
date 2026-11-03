package co.edu.uniquindio.bibliometria.infra.embeddings;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.EmbeddingProvider;
import co.edu.uniquindio.bibliometria.util.VectorMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Ejecuta MiniLM en ONNX y promedia los vectores de sus tokens. */
public class ModeloOracionProvider implements EmbeddingProvider, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ModeloOracionProvider.class);

    static final int MAXIMO_TOKENS = 256;
    private static final long ID_CLS = 101;
    private static final long ID_SEP = 102;

    private final String modelo;
    private OrtEnvironment entorno;
    private OrtSession sesion;
    private HuggingFaceTokenizer tokenizador;
    private int dimension;

    public ModeloOracionProvider(Path directorio) {
        this.modelo = directorio.getFileName().toString();
        Path onnx = directorio.resolve("model.onnx");
        Path tokenizer = directorio.resolve("tokenizer.json");
        if (!Files.isRegularFile(onnx) || !Files.isRegularFile(tokenizer)) {
            log.warn("No se encontró {} o {}; embeddings de oración no disponibles", onnx, tokenizer);
            return;
        }
        try {
            entorno = OrtEnvironment.getEnvironment();
            sesion = entorno.createSession(onnx.toString(), new OrtSession.SessionOptions());
            tokenizador = HuggingFaceTokenizer.newInstance(tokenizer,
                    Map.of("addSpecialTokens", "false", "truncation", "false", "padding", "false"));
            dimension = vectorizarIds(new long[] {ID_CLS, ID_SEP}).length;
            log.info("Modelo de oraciones {} cargado, dimensión {}", modelo, dimension);
        } catch (OrtException | IOException | RuntimeException e) {
            log.warn("No se pudo cargar el modelo de oraciones: {}", e.getMessage());
            sesion = null;
        }
    }

    @Override
    public String modelo() {
        return modelo;
    }

    @Override
    public int dimension() {
        return sesion != null ? dimension : 0;
    }

    public boolean disponible() {
        return sesion != null;
    }

    public record Detalle(double[] vector, int tokens, int ventanas) {}

    @Override
    public double[] vectorizar(String texto) {
        return vectorizarConDetalle(texto).vector();
    }

    public synchronized Detalle vectorizarConDetalle(String texto) {
        Encoding codificacion = tokenizador.encode(texto);
        long[] ids = codificacion.getIds();
        int porVentana = MAXIMO_TOKENS - 2;
        int ventanas = Math.max(1, (ids.length + porVentana - 1) / porVentana);
        double[] acumulado = new double[dimension];
        for (int v = 0; v < ventanas; v++) {
            int desde = v * porVentana;
            int hasta = Math.min(ids.length, desde + porVentana);
            long[] ventana = new long[hasta - desde + 2];
            ventana[0] = ID_CLS;
            for (int i = desde; i < hasta; i++) {
                ventana[i - desde + 1] = ids[i];
            }
            ventana[ventana.length - 1] = ID_SEP;
            double[] vector = vectorizarIds(ventana);
            for (int i = 0; i < dimension; i++) {
                acumulado[i] += vector[i] * ventana.length;
            }
        }
        return new Detalle(VectorMath.normalizar(acumulado), ids.length, ventanas);
    }

    private double[] vectorizarIds(long[] ids) {
        long[][] entradaIds = {ids};
        long[][] mascara = new long[1][ids.length];
        long[][] tipos = new long[1][ids.length];
        for (int i = 0; i < ids.length; i++) {
            mascara[0][i] = 1;
        }
        try (OnnxTensor tIds = OnnxTensor.createTensor(entorno, entradaIds);
             OnnxTensor tMascara = OnnxTensor.createTensor(entorno, mascara);
             OnnxTensor tTipos = OnnxTensor.createTensor(entorno, tipos);
             OrtSession.Result resultado = sesion.run(Map.of(
                     "input_ids", tIds, "attention_mask", tMascara, "token_type_ids", tTipos))) {
            float[][][] estados = (float[][][]) resultado.get(0).getValue();
            int d = estados[0][0].length;
            double[] promedio = new double[d];
            for (float[] token : estados[0]) {
                for (int i = 0; i < d; i++) {
                    promedio[i] += token[i];
                }
            }
            for (int i = 0; i < d; i++) {
                promedio[i] /= ids.length;
            }
            return promedio;
        } catch (OrtException e) {
            throw new IllegalStateException("Error ejecutando el modelo ONNX", e);
        }
    }

    @Override
    public void close() throws OrtException {
        if (sesion != null) {
            sesion.close();
        }
        if (tokenizador != null) {
            tokenizador.close();
        }
    }
}
