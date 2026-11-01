package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/**
 * Entrada de POST /api/similitud.
 *
 * @param ids        ids de los artículos a comparar (2 o más); se comparan todos los pares
 * @param algoritmos nombres de los algoritmos a aplicar; vacío = todos
 * @param conTraza   si es true, cada resultado trae sus pasos intermedios
 */
public record SimilitudRequest(List<String> ids, List<String> algoritmos, boolean conTraza) {
}
