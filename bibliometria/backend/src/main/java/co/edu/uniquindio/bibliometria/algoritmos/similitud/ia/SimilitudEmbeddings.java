package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.util.VectorMath;

import java.util.List;
import java.util.Locale;

/**
 * Parte común de los dos algoritmos con IA: obtener el embedding de cada abstract con el
 * proveedor y comparar los vectores con coseno (valor principal) y distancia euclidiana
 * (valor crudo). Una vez se tienen los vectores, la comparación es O(d).
 */
public abstract class SimilitudEmbeddings implements SimilitudStrategy {

    private static final int COMPONENTES_EN_TRAZA = 8;

    private final EmbeddingProvider proveedor;

    protected SimilitudEmbeddings(EmbeddingProvider proveedor) {
        this.proveedor = proveedor;
    }

    public EmbeddingProvider getProveedor() {
        return proveedor;
    }

    /** Un proveedor sin modelo cargado informa dimensión 0. */
    public boolean disponible() {
        return proveedor.dimension() > 0;
    }

    @Override
    public ResultadoSimilitud comparar(String textoA, String textoB, Traza traza) {
        if (!disponible()) {
            throw new IllegalStateException("El modelo " + proveedor.modelo() + " no está cargado");
        }
        long inicio = System.nanoTime();
        double[] a = proveedor.vectorizar(textoA);
        double[] b = proveedor.vectorizar(textoB);
        double productoPunto = VectorMath.productoPunto(a, b);
        double normaA = VectorMath.norma(a);
        double normaB = VectorMath.norma(b);
        double coseno = normaA == 0 || normaB == 0 ? 0.0 : productoPunto / (normaA * normaB);
        double euclidiana = VectorMath.distanciaEuclidiana(a, b);
        long tiempo = System.nanoTime() - inicio;

        boolean conTraza = traza != null && traza.estaActiva();
        if (conTraza) {
            traza.registrar("modelo " + proveedor.modelo(), "dimensión " + a.length);
            traza.registrar("vector A", "primeras componentes " + primeras(a));
            traza.registrar("vector B", "primeras componentes " + primeras(b));
            traza.registrar("A·B = " + formato(productoPunto), "suma de a_i·b_i sobre " + a.length + " componentes");
            traza.registrar("||A|| = " + formato(normaA), "raíz de la suma de a_i²");
            traza.registrar("||B|| = " + formato(normaB), "raíz de la suma de b_i²");
            traza.registrar("cos θ = " + formato(coseno),
                    formato(productoPunto) + " / (" + formato(normaA) + " · " + formato(normaB) + ")");
            traza.registrar("θ = " + String.format(Locale.ROOT, "%.2f", VectorMath.anguloGrados(coseno)) + "°",
                    "arccos(cos θ)");
            traza.registrar("d(A, B) = " + formato(euclidiana), "raíz de la suma de (a_i - b_i)²");
        }
        return new ResultadoSimilitud(nombre(), VectorMath.cosenoASimilitud(coseno), euclidiana, tiempo,
                conTraza ? traza.pasos() : List.of());
    }

    public static String primeras(double[] v) {
        StringBuilder salida = new StringBuilder("[");
        int limite = Math.min(COMPONENTES_EN_TRAZA, v.length);
        for (int i = 0; i < limite; i++) {
            salida.append(formato(v[i]));
            if (i < limite - 1) {
                salida.append(", ");
            }
        }
        return salida.append(v.length > limite ? ", ...]" : "]").toString();
    }

    private static String formato(double valor) {
        return String.format(Locale.ROOT, "%.4f", valor);
    }
}
