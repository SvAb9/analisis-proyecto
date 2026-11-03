package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Acceso al corpus ya procesado.
 */
@Service
public class CorpusService {

    private final RepositorioCorpus corpus;

    public CorpusService(RepositorioCorpus corpus) {
        this.corpus = corpus;
    }

    public List<Articulo> listar() {
        return corpus.todos();
    }

    public Optional<Articulo> buscar(String id) {
        return corpus.porId(id);
    }
}
