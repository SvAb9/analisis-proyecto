package co.edu.uniquindio.bibliometria.corpus;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso al corpus ya procesado.
 * Implementa: CorpusRepository (Zuleta). Lo consumen todos los servicios.
 * Mientras no exista la implementación real, cada quien puede probar con una lista fija de Articulo.
 */
public interface RepositorioCorpus {

    /** Los 20 artículos, en orden estable. */
    List<Articulo> todos();

    /** Un artículo por su id; vacío si no existe. */
    Optional<Articulo> porId(String id);
}
