package co.edu.uniquindio.bibliometria.corpus;

import java.util.List;

/** Artículo del corpus con su abstract crudo y procesado. */
public record Articulo(String id, String titulo, List<String> autores, String abstractCrudo,
                       String abstractProcesado) {

    public String textoParaAlgoritmos() {
        return abstractProcesado != null && !abstractProcesado.isBlank() ? abstractProcesado : abstractCrudo;
    }
}
