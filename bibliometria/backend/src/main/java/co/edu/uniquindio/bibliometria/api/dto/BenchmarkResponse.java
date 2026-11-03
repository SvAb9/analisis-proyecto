package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/** Salida de GET /api/benchmark. */
public record BenchmarkResponse(String patron, int largoPatron, int articulos, long caracteres,
                                int repeticiones, int calentamiento, List<Medicion> mediciones) {

    public record Medicion(String algoritmo, long preprocesamientoNs, long busquedaNs, long minimoNs, long maximoNs,
                           long comparaciones, int ocurrencias) {
    }
}
