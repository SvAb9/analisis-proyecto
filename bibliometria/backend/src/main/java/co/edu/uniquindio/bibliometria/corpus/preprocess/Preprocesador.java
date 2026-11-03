package co.edu.uniquindio.bibliometria.corpus.preprocess;

import java.util.List;

/** Contrato del preprocesamiento de texto. */
public interface Preprocesador {

    String normalizar(String texto);

    List<String> tokenizar(String texto);
}
