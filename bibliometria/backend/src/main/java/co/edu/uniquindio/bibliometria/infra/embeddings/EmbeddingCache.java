package co.edu.uniquindio.bibliometria.infra.embeddings;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.EmbeddingProvider;
import co.edu.uniquindio.bibliometria.util.estructuras.ArbolBinarioBusqueda;

/** Guarda en memoria los vectores ya calculados. */
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
