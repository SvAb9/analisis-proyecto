package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/**
 * Salida de POST /api/busqueda: un resultado por algoritmo, sumando los 20 abstracts.
 *
 * @param patron         patrón ya normalizado, que es el que se buscó
 * @param articuloTraza  artículo al que corresponden las trazas; null si no se pidieron
 */
public record BusquedaResponse(String patron, String articuloTraza, List<Resultado> resultados) {

    /**
     * @param algoritmo          "KMP", "BoyerMoore" o "RabinKarp"
     * @param totalOcurrencias   suma de ocurrencias en todos los artículos
     * @param porArticulo        ocurrencias de cada artículo que tenga al menos una
     * @param comparaciones      comparaciones de caracteres acumuladas
     * @param preprocesamientoNs tiempo acumulado de preprocesamiento
     * @param busquedaNs         tiempo acumulado de búsqueda
     * @param traza              pasos intermedios en el artículo de la traza; null si no se pidieron
     */
    public record Resultado(String algoritmo, int totalOcurrencias, List<Coincidencia> porArticulo,
                            long comparaciones, long preprocesamientoNs, long busquedaNs, TrazaDTO traza) {
    }

    /** Posiciones (base 0) donde aparece el patrón en el abstract procesado de un artículo. */
    public record Coincidencia(String articuloId, List<Integer> posiciones) {
    }
}
