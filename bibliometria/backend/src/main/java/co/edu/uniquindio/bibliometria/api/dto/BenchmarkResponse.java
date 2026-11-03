package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/**
 * Salida de GET /api/benchmark: tiempos de preprocesamiento y búsqueda sobre los 20 abstracts.
 *
 * @param patron       patrón ya normalizado
 * @param largoPatron  m, largo del patrón
 * @param articulos    número de abstracts recorridos en cada repetición
 * @param caracteres   n, total de caracteres de los abstracts
 * @param repeticiones repeticiones medidas por algoritmo
 * @param calentamiento repeticiones descartadas por algoritmo, para que el JIT compile el código
 */
public record BenchmarkResponse(String patron, int largoPatron, int articulos, long caracteres,
                                int repeticiones, int calentamiento, List<Medicion> mediciones) {

    /**
     * Tiempos de una pasada completa por los 20 abstracts.
     *
     * @param algoritmo          "KMP", "BoyerMoore" o "RabinKarp"
     * @param preprocesamientoNs mediana del tiempo de preprocesamiento
     * @param busquedaNs         mediana del tiempo de búsqueda
     * @param minimoNs           menor tiempo total (preprocesamiento + búsqueda) observado
     * @param maximoNs           mayor tiempo total observado
     * @param comparaciones      comparaciones de caracteres de una pasada
     * @param ocurrencias        ocurrencias encontradas en una pasada
     */
    public record Medicion(String algoritmo, long preprocesamientoNs, long busquedaNs, long minimoNs, long maximoNs,
                           long comparaciones, int ocurrencias) {
    }
}
