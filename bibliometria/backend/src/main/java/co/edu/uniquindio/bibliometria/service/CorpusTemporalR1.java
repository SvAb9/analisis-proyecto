package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Respaldo temporal de R1 mientras no exista CorpusRepository (Zuleta): los 20 abstracts ya
 * extraídos y limpiados de los PDF, empaquetados en {@code similitud/abstracts-corpus.json}.
 * No es un bean: SimilitudService solo lo usa si Spring no tiene ningún RepositorioCorpus.
 */
public class CorpusTemporalR1 implements RepositorioCorpus {

    private final List<Articulo> articulos = new ArrayList<>();

    public CorpusTemporalR1() {
        try (InputStream in = getClass().getResourceAsStream("/similitud/abstracts-corpus.json")) {
            if (in == null) {
                return;
            }
            for (JsonNode n : new ObjectMapper().readTree(in)) {
                articulos.add(new Articulo(n.get("id").asText(), n.get("titulo").asText(), List.of(),
                        n.get("abstract").asText(), null));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el corpus temporal de R1", e);
        }
    }

    @Override
    public List<Articulo> todos() {
        return List.copyOf(articulos);
    }

    @Override
    public Optional<Articulo> porId(String id) {
        for (Articulo articulo : articulos) {
            if (articulo.id().equals(id)) {
                return Optional.of(articulo);
            }
        }
        return Optional.empty();
    }
}
