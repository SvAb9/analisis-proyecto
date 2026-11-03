package co.edu.uniquindio.bibliometria.api;

import co.edu.uniquindio.bibliometria.api.dto.BusquedaRequest;
import co.edu.uniquindio.bibliometria.api.dto.BusquedaResponse;
import co.edu.uniquindio.bibliometria.service.BusquedaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * R3: recibe el patrón y devuelve ocurrencias por algoritmo.
 */
@RestController
@RequestMapping("/api/busqueda")
public class BusquedaController {

    private final BusquedaService servicio;

    public BusquedaController(BusquedaService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public BusquedaResponse buscar(@RequestBody BusquedaRequest solicitud) {
        return servicio.buscar(solicitud);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> solicitudInvalida(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
