package co.edu.uniquindio.bibliometria.algoritmos.similitud.ia;

/**
 * IA #2: Word2Vec/FastText promediado + coseno/euclidiana.
 *
 * <p>Cada palabra de contenido se reemplaza por su vector preentrenado (GloVe, formato de texto
 * word2vec) y el abstract se representa con el promedio. Palabras distintas pero cercanas en
 * significado ("assessment" y "evaluation") tienen vectores parecidos, algo que ni Jaccard ni
 * TF-IDF pueden ver; a cambio se pierde el orden de las palabras.</p>
 */
public class SimilitudEmbeddingsPalabras extends SimilitudEmbeddings {

    public SimilitudEmbeddingsPalabras(EmbeddingProvider proveedor) {
        super(proveedor);
    }

    @Override
    public String nombre() {
        return "EmbeddingsPalabras";
    }
}
