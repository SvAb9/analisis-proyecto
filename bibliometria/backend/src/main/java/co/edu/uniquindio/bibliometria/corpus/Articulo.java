package co.edu.uniquindio.bibliometria.corpus;

import java.util.List;

/**
 * Modelo: id, título, autores, abstract crudo y abstract procesado.
 *
 * @param abstractCrudo     abstract tal como se extrajo del PDF
 * @param abstractProcesado abstract después de Preprocesador (puede ser null si aún no se procesó)
 */
public record Articulo(String id, String titulo, List<String> autores, String abstractCrudo,
                       String abstractProcesado) {

    /** Texto que reciben los algoritmos: el procesado si existe, si no el crudo. */
    public String textoParaAlgoritmos() {
        return abstractProcesado != null && !abstractProcesado.isBlank() ? abstractProcesado : abstractCrudo;
    }
}
