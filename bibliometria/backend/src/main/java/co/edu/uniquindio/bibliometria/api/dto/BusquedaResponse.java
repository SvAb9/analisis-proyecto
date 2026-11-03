package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/** Salida de POST /api/busqueda. */
public record BusquedaResponse(String patron, String articuloTraza, List<Resultado> resultados) {

    public record Resultado(String algoritmo, int totalOcurrencias, List<Coincidencia> porArticulo,
                            long comparaciones, long preprocesamientoNs, long busquedaNs, TrazaDTO traza) {
    }

    public record Coincidencia(String articuloId, List<Integer> posiciones) {
    }
}
