package co.edu.uniquindio.bibliometria.api;

import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.service.CorpusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lista artículos y devuelve título, autores y abstract.
 *
 * <ul>
 *   <li>GET /api/corpus: los 20 artículos, en orden</li>
 *   <li>GET /api/corpus/{id}: un artículo, o 404 si no existe</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/corpus")
public class CorpusController {

    private final CorpusService servicio;

    public CorpusController(CorpusService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Articulo> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Articulo> buscar(@PathVariable String id) {
        return servicio.buscar(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
