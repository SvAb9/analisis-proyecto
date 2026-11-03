package co.edu.uniquindio.bibliometria.corpus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Guarda los artículos del corpus en memoria. */
@Repository
public class CorpusRepository implements RepositorioCorpus {

    private final Map<String, Articulo> articulos = new LinkedHashMap<>();

    @Autowired
    public CorpusRepository(CorpusLoader cargador) {
        this(cargador.cargar());
    }

    public CorpusRepository(List<Articulo> articulos) {
        for (Articulo articulo : articulos) {
            this.articulos.put(articulo.id(), articulo);
        }
    }

    @Override
    public List<Articulo> todos() {
        return List.copyOf(articulos.values());
    }

    @Override
    public Optional<Articulo> porId(String id) {
        return Optional.ofNullable(articulos.get(id));
    }
}
