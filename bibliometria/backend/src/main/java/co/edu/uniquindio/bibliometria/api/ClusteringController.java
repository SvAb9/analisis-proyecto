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

/** Endpoint de agrupamiento (R2). */
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