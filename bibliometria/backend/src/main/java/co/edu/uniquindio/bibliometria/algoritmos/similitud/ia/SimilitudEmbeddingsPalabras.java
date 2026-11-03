package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

/** Similitud con el promedio de embeddings de palabras (GloVe). */
public class SimilitudEmbeddingsPalabras extends SimilitudEmbeddings {

    public SimilitudEmbeddingsPalabras(EmbeddingProvider proveedor) {
        super(proveedor);
    }

    @Override
    public String nombre() {
        return "EmbeddingsPalabras";
    }
}
