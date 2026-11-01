package co.edu.uniquindio.bibliometria.api.dto;

/**
 * Entrada de POST /api/busqueda.
 *
 * @param patron   cadena a buscar (el servicio la normaliza)
 * @param conTraza si es true, cada resultado trae sus pasos intermedios
 */
public record BusquedaRequest(String patron, boolean conTraza) {
}
