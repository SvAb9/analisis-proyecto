package co.edu.uniquindio.bibliometria.api;

import co.edu.uniquindio.bibliometria.api.dto.ClusteringRequest;
import co.edu.uniquindio.bibliometria.api.dto.ClusteringResponse;
import co.edu.uniquindio.bibliometria.service.ClusteringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * R2: ejecuta los 3 enlaces y devuelve dendrogramas y métricas internas.
 *
 * <ul>
 *   <li>POST /api/clustering: agrupa los artículos del corpus con el algoritmo de similitud
 *       pedido (por defecto CosenoTfIdf) y devuelve un árbol por cada criterio de enlace</li>
 * </ul>
 *
 * <p>Los nombres de los algoritmos de similitud disponibles salen de GET /api/similitud/algoritmos.</p>
 */
@RestController
@RequestMapping("/api/clustering")
public class ClusteringController {

    private final ClusteringService servicio;

    public ClusteringController(ClusteringService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ClusteringResponse agrupar(@RequestBody(required = false) ClusteringRequest solicitud) {
        return servicio.agrupar(solicitud);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> solicitudInvalida(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}