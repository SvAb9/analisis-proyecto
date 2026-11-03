package co.edu.uniquindio.bibliometria.api.dto;

/** Entrada de POST /api/busqueda. */
public record BusquedaRequest(String patron, boolean conTraza, String articuloId) {
}
