package co.edu.uniquindio.bibliometria.corpus.preprocess;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Pipeline completo de preprocesamiento: Normalizador, Tokenizador, StopwordFilter y Stemmer.
 *
 * <ul>
 *   <li>{@link #normalizar(String)}: texto corrido limpio (sin stemming). Es lo que se guarda como
 *       {@code abstractProcesado}, porque los modelos de embeddings necesitan palabras completas.</li>
 *   <li>{@link #tokenizar(String)}: lista de raíces sin stopwords, para los algoritmos que trabajan
 *       con términos (TF-IDF, Jaccard, clustering).</li>
 * </ul>
 */
@Component
public class PreprocesadorTexto implements Preprocesador {

    private static final int LARGO_MINIMO = 2;

    private final Normalizador normalizador;
    private final Tokenizador tokenizador;
    private final StopwordFilter filtro;
    private final Stemmer stemmer;

    public PreprocesadorTexto() {
        this(new Normalizador(), new Tokenizador(), new StopwordFilter(), new Stemmer());
    }

    public PreprocesadorTexto(Normalizador normalizador, Tokenizador tokenizador,
                              StopwordFilter filtro, Stemmer stemmer) {
        this.normalizador = normalizador;
        this.tokenizador = tokenizador;
        this.filtro = filtro;
        this.stemmer = stemmer;
    }

    @Override
    public String normalizar(String texto) {
        return normalizador.normalizar(texto);
    }

    /** Normaliza, separa en tokens, descarta stopwords, números y palabras de una letra, y aplica stemming. */
    @Override
    public List<String> tokenizar(String texto) {
        List<String> resultado = new ArrayList<>();
        for (String token : tokenizador.tokenizar(normalizador.normalizar(texto))) {
            if (token.length() < LARGO_MINIMO || esNumero(token) || filtro.esStopword(token)) {
                continue;
            }
            resultado.add(stemmer.stem(token));
        }
        return resultado;
    }

    private boolean esNumero(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
