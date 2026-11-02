package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

/**
 * IA #1: embedding de oración (modelo tipo MiniLM) + coseno/euclidiana.
 *
 * <p>Un transformer (all-MiniLM-L6-v2) produce un vector contextual por token y el abstract se
 * representa con el promedio de esos vectores (mean pooling) normalizado. A diferencia del
 * promedio de palabras, cada vector depende de las palabras vecinas, así que captura el orden y
 * el sentido de la frase.</p>
 */
public class SimilitudEmbeddingsOracion extends SimilitudEmbeddings {

    public SimilitudEmbeddingsOracion(EmbeddingProvider proveedor) {
        super(proveedor);
    }

    @Override
    public String nombre() {
        return "EmbeddingsOracion";
    }
}
