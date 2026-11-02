package co.edu.uniquindio.bibliometria.api;

import co.edu.uniquindio.bibliometria.api.dto.SimilitudRequest;
import co.edu.uniquindio.bibliometria.api.dto.SimilitudResponse;
import co.edu.uniquindio.bibliometria.service.SimilitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * R1: recibe 2+ artículos y los algoritmos a aplicar; devuelve similitudes.
 *
 * <ul>
 *   <li>GET  /api/similitud/algoritmos: nombres de los algoritmos disponibles</li>
 *   <li>POST /api/similitud: compara todos los pares de los artículos pedidos (por id)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/similitud")
public class SimilitudController {

    private final SimilitudService servicio;

    public SimilitudController(SimilitudService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/algoritmos")
    public List<String> algoritmos() {
        return servicio.algoritmos();
    }

    @PostMapping
    public SimilitudResponse comparar(@RequestBody SimilitudRequest solicitud) {
        return servicio.comparar(solicitud);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> solicitudInvalida(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
