package co.edu.uniquindio.bibliometria.api.dto;

/**
 * Entrada de POST /api/busqueda.
 *
 * @param patron      cadena a buscar (el servicio la normaliza)
 * @param conTraza    si es true, cada resultado trae sus pasos intermedios en un artículo
 * @param articuloId  artículo del que se quiere la traza; si es null, el primero con ocurrencias
 */
public record BusquedaRequest(String patron, boolean conTraza, String articuloId) {
}
