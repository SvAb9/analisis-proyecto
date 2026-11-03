package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.CosenoTfIdf;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.JaccardSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.Levenshtein;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.NeedlemanWunsch;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.EmbeddingProvider;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.SimilitudEmbeddings;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.SimilitudEmbeddingsOracion;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.SimilitudEmbeddingsPalabras;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.api.dto.SimilitudRequest;
import co.edu.uniquindio.bibliometria.api.dto.SimilitudResponse;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import co.edu.uniquindio.bibliometria.infra.embeddings.EmbeddingCache;
import co.edu.uniquindio.bibliometria.infra.embeddings.ModeloOracionProvider;
import co.edu.uniquindio.bibliometria.infra.embeddings.Word2VecProvider;
import co.edu.uniquindio.bibliometria.util.estructuras.Cola;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Ejecuta los algoritmos de similitud sobre los pares de artículos. */
@Service
public class SimilitudService {

    private final RepositorioCorpus corpus;
    private final ListaEnlazada<SimilitudStrategy> estrategias = new ListaEnlazada<>();

    @Autowired
    public SimilitudService(@Value("${app.modelos.oracion:data/modelos/all-MiniLM-L6-v2}") String rutaOracion,
                            @Value("${app.modelos.palabras:data/modelos/glove/glove.6B.100d.corpus.txt}") String rutaPalabras,
                            RepositorioCorpus corpus) {
        this(new EmbeddingCache(new ModeloOracionProvider(Path.of(rutaOracion))),
                new EmbeddingCache(new Word2VecProvider(Path.of(rutaPalabras))),
                corpus);
    }

    public SimilitudService(EmbeddingProvider proveedorOracion, EmbeddingProvider proveedorPalabras,
                            RepositorioCorpus corpus) {
        this.corpus = corpus;
        estrategias.insertarFinal(new Levenshtein());
        estrategias.insertarFinal(new NeedlemanWunsch());
        estrategias.insertarFinal(new JaccardSimilitud());
        estrategias.insertarFinal(new CosenoTfIdf(corpus));
        estrategias.insertarFinal(new SimilitudEmbeddingsOracion(proveedorOracion));
        estrategias.insertarFinal(new SimilitudEmbeddingsPalabras(proveedorPalabras));
    }

    public List<String> algoritmos() {
        List<String> nombres = new ArrayList<>();
        for (SimilitudStrategy estrategia : estrategias) {
            if (disponible(estrategia)) {
                nombres.add(estrategia.nombre());
            }
        }
        return nombres;
    }

    public SimilitudStrategy estrategia(String nombre) {
        for (SimilitudStrategy estrategia : estrategias) {
            if (estrategia.nombre().equals(nombre)) {
                return estrategia;
            }
        }
        return null;
    }

    public static String textoPara(SimilitudStrategy estrategia, Articulo articulo) {
        return estrategia instanceof SimilitudEmbeddingsOracion ? articulo.abstractCrudo() : articulo.textoParaAlgoritmos();
    }

    public static boolean esIA(SimilitudStrategy estrategia) {
        return estrategia instanceof SimilitudEmbeddings;
    }

    private static boolean disponible(SimilitudStrategy estrategia) {
        return !(estrategia instanceof SimilitudEmbeddings e) || e.disponible();
    }

    private record Tarea(Articulo a, Articulo b) {}

    public SimilitudResponse comparar(SimilitudRequest solicitud) {
        List<Articulo> articulos = buscarArticulos(solicitud);
        ListaEnlazada<SimilitudStrategy> elegidas = elegirEstrategias(solicitud.algoritmos());

        Cola<Tarea> cola = new Cola<>();
        for (int i = 0; i < articulos.size(); i++) {
            for (int j = i + 1; j < articulos.size(); j++) {
                cola.enqueue(new Tarea(articulos.get(i), articulos.get(j)));
            }
        }
        List<SimilitudResponse.Par> pares = new ArrayList<>();
        while (!cola.estaVacia()) {
            Tarea tarea = cola.dequeue();
            List<ResultadoSimilitud> resultados = new ArrayList<>();
            for (SimilitudStrategy estrategia : elegidas) {
                Traza traza = solicitud.conTraza() ? Traza.activada() : Traza.desactivada();
                resultados.add(estrategia.comparar(textoPara(estrategia, tarea.a()), textoPara(estrategia, tarea.b()), traza));
            }
            pares.add(new SimilitudResponse.Par(tarea.a().id(), tarea.b().id(), resultados));
        }
        return new SimilitudResponse(pares);
    }

    private List<Articulo> buscarArticulos(SimilitudRequest solicitud) {
        if (solicitud == null || solicitud.ids() == null || solicitud.ids().size() < 2) {
            throw new IllegalArgumentException("Se necesitan al menos dos artículos para comparar");
        }
        List<Articulo> articulos = new ArrayList<>();
        for (String id : solicitud.ids()) {
            articulos.add(corpus.porId(id)
                    .orElseThrow(() -> new IllegalArgumentException("No existe el artículo con id " + id)));
        }
        return articulos;
    }

    private ListaEnlazada<SimilitudStrategy> elegirEstrategias(List<String> nombres) {
        ListaEnlazada<SimilitudStrategy> elegidas = new ListaEnlazada<>();
        if (nombres == null || nombres.isEmpty()) {
            for (SimilitudStrategy estrategia : estrategias) {
                if (disponible(estrategia)) {
                    elegidas.insertarFinal(estrategia);
                }
            }
            return elegidas;
        }
        for (String nombre : nombres) {
            SimilitudStrategy estrategia = estrategia(nombre);
            if (estrategia == null) {
                throw new IllegalArgumentException("Algoritmo desconocido: " + nombre);
            }
            if (!disponible(estrategia)) {
                throw new IllegalArgumentException("El algoritmo " + nombre + " no está disponible (falta el modelo)");
            }
            elegidas.insertarFinal(estrategia);
        }
        return elegidas;
    }
}
