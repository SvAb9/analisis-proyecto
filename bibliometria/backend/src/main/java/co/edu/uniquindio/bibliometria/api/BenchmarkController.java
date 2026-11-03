package co.edu.uniquindio.bibliometria.api;

import co.edu.uniquindio.bibliometria.api.dto.BenchmarkResponse;
import co.edu.uniquindio.bibliometria.service.BenchmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * R3: expone tiempos de preprocesamiento vs búsqueda.
 */
@RestController
@RequestMapping("/api/benchmark")
public class BenchmarkController {

    private final BenchmarkService servicio;

    public BenchmarkController(BenchmarkService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public BenchmarkResponse medir(@RequestParam String patron, @RequestParam(required = false) Integer repeticiones) {
        return servicio.medir(patron, repeticiones);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> solicitudInvalida(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
