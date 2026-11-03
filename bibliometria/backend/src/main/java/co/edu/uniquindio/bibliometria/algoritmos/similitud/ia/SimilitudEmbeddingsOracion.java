package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

/** Similitud con embeddings de oración (MiniLM). */
public class SimilitudEmbeddingsOracion extends SimilitudEmbeddings {

    public SimilitudEmbeddingsOracion(EmbeddingProvider proveedor) {
        super(proveedor);
    }

    @Override
    public String nombre() {
        return "EmbeddingsOracion";
    }
}
