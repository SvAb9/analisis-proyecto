package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

import java.util.Map;

/** Proveedor de prueba: devuelve vectores fijos por texto, para verificar la matemática sin modelo. */
class ProveedorFijo implements EmbeddingProvider {

    private final Map<String, double[]> vectores;

    ProveedorFijo(Map<String, double[]> vectores) {
        this.vectores = vectores;
    }

    @Override
    public String modelo() {
        return "fijo";
    }

    @Override
    public int dimension() {
        return 2;
    }

    @Override
    public double[] vectorizar(String texto) {
        return vectores.get(texto);
    }
}
