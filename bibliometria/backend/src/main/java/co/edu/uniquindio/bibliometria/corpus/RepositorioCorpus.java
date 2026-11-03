package co.edu.uniquindio.bibliometria.corpus;

import java.util.List;
import java.util.Optional;

/** Contrato de acceso a los artículos del corpus. */
public interface RepositorioCorpus {

    List<Articulo> todos();

    Optional<Articulo> porId(String id);
}
