package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.algoritmos.clustering.AverageLinkage;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.ClusteringJerarquico;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.CompleteLinkage;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.CriterioEnlace;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.Dendrograma;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.MatrizDistancias;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.NodoDendrograma;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.SingleLinkage;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.evaluacion.CorrelacionCofenetica;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.evaluacion.Silhouette;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import co.edu.uniquindio.bibliometria.api.dto.ClusteringRequest;
import co.edu.uniquindio.bibliometria.api.dto.ClusteringResponse;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Agrupa el corpus con los tres criterios y evalúa cada árbol. */
@Service
public class ClusteringService {

    static final String SIMILITUD_POR_DEFECTO = "CosenoTfIdf";

    private final SimilitudService similitud;
    private final RepositorioCorpus corpus;

    public ClusteringService(SimilitudService similitud, RepositorioCorpus corpus) {
        this.similitud = similitud;
        this.corpus = corpus;
    }

    public ClusteringResponse agrupar(ClusteringRequest solicitud) {
        SimilitudStrategy estrategia = elegirSimilitud(solicitud);

        List<Articulo> articulos = corpus.todos();
        if (articulos.size() < 3) {
            throw new IllegalArgumentException("Se necesitan al menos 3 artículos para agrupar");
        }
        List<String> ids = new ArrayList<>();
        List<String> textos = new ArrayList<>();
        for (Articulo articulo : articulos) {
            ids.add(articulo.id());
            textos.add(SimilitudService.textoPara(estrategia, articulo));
        }

        MatrizDistancias matriz = MatrizDistancias.desdeTextos(ids, textos, estrategia);
        double[][] distancias = matriz.valores();

        CriterioEnlace[] criterios = {new SingleLinkage(), new CompleteLinkage(), new AverageLinkage()};
        List<ClusteringResponse.Resultado> resultados = new ArrayList<>();
        for (CriterioEnlace criterio : criterios) {
            long inicio = System.nanoTime();
            Dendrograma dendrograma = new ClusteringJerarquico(criterio).agrupar(matriz);
            long tiempoNs = System.nanoTime() - inicio;

            double silhouette = Silhouette.mejorCorte(distancias, dendrograma).silhouette();
            double cofenetica = CorrelacionCofenetica.calcular(distancias, dendrograma.matrizCofenetica());
            resultados.add(new ClusteringResponse.Resultado(
                    criterio.nombre(), aDto(dendrograma.raiz()), silhouette, cofenetica, tiempoNs));
        }
        return new ClusteringResponse(resultados);
    }

    private SimilitudStrategy elegirSimilitud(ClusteringRequest solicitud) {
        String nombre = (solicitud == null || solicitud.algoritmoSimilitud() == null
                || solicitud.algoritmoSimilitud().isBlank())
                ? SIMILITUD_POR_DEFECTO
                : solicitud.algoritmoSimilitud();
        SimilitudStrategy estrategia = similitud.estrategia(nombre);
        if (estrategia == null) {
            throw new IllegalArgumentException("Algoritmo de similitud desconocido: " + nombre);
        }
        if (!similitud.algoritmos().contains(nombre)) {
            throw new IllegalArgumentException("El algoritmo " + nombre + " no está disponible (falta el modelo)");
        }
        return estrategia;
    }

    private ClusteringResponse.Nodo aDto(NodoDendrograma nodo) {
        if (nodo.esHoja()) {
            return new ClusteringResponse.Nodo(nodo.articuloId(), 0.0, null, null);
        }
        return new ClusteringResponse.Nodo(null, nodo.altura(), aDto(nodo.izquierda()), aDto(nodo.derecha()));
    }
}