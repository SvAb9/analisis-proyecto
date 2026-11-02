package co.edu.uniquindio.bibliometria.infra.embeddings;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.EmbeddingProvider;
import co.edu.uniquindio.bibliometria.util.estructuras.ArbolBinarioBusqueda;

/**
 * Guarda/lee vectores para no recalcular.
 *
 * <p>Decorador de un {@link EmbeddingProvider}: la primera vez que se pide el vector de un texto
 * se calcula y se guarda en un {@link ArbolBinarioBusqueda} texto → vector; las siguientes se
 * leen del árbol en O(h). Importa porque al comparar k artículos cada abstract aparece en k − 1
 * pares, y una pasada por el transformer cuesta mucho más que comparar vectores. La caché vive
 * en memoria: el corpus es fijo (20 abstracts), así que nunca crece más allá de 20 entradas por modelo.</p>
 */
public class EmbeddingCache implements EmbeddingProvider {

    private final EmbeddingProvider proveedor;
    private final ArbolBinarioBusqueda<String, double[]> cache = new ArbolBinarioBusqueda<>();
    private int aciertos;
    private int fallos;

    public EmbeddingCache(EmbeddingProvider proveedor) {
        this.proveedor = proveedor;
    }

    public EmbeddingProvider getProveedor() {
        return proveedor;
    }

    @Override
    public synchronized double[] vectorizar(String texto) {
        double[] guardado = cache.obtener(texto);
        if (guardado != null) {
            aciertos++;
            return guardado;
        }
        fallos++;
        double[] nuevo = proveedor.vectorizar(texto);
        cache.insertar(texto, nuevo);
        return nuevo;
    }

    @Override
    public String modelo() {
        return proveedor.modelo();
    }

    @Override
    public int dimension() {
        return proveedor.dimension();
    }

    public synchronized int getAciertos() {
        return aciertos;
    }

    public synchronized int getFallos() {
        return fallos;
    }
}
