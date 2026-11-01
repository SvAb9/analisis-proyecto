package co.edu.uniquindio.bibliometria.corpus.preprocess;

import java.util.List;

/**
 * Contrato del preprocesamiento de texto.
 * Implementa: PreprocesadorTexto (Sara), encadenando Normalizador, Tokenizador,
 * StopwordFilter y Stemmer. Lo usa CorpusLoader (Zuleta) al cargar los abstracts
 * y los servicios para normalizar el patrón de búsqueda.
 */
public interface Preprocesador {

    /** Minúsculas, sin tildes ni signos, espacios simples. Devuelve texto corrido (no tokens). */
    String normalizar(String texto);

    /** Normaliza, divide en tokens, quita stopwords y aplica stemming. */
    List<String> tokenizar(String texto);
}
