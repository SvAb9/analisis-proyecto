package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.algoritmos.stringmatching.BoyerMoore;
import co.edu.uniquindio.bibliometria.algoritmos.stringmatching.KMP;
import co.edu.uniquindio.bibliometria.algoritmos.stringmatching.RabinKarp;
import co.edu.uniquindio.bibliometria.algoritmos.stringmatching.ResultadoBusqueda;
import co.edu.uniquindio.bibliometria.algoritmos.stringmatching.StringMatcher;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.api.dto.BusquedaRequest;
import co.edu.uniquindio.bibliometria.api.dto.BusquedaResponse;
import co.edu.uniquindio.bibliometria.api.dto.TrazaDTO;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import co.edu.uniquindio.bibliometria.corpus.preprocess.Preprocesador;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Ejecuta los tres algoritmos de búsqueda sobre los abstracts. */
@Service
public class BusquedaService {

    static final List<StringMatcher> ALGORITMOS = List.of(new KMP(), new BoyerMoore(), new RabinKarp());

    private final RepositorioCorpus corpus;
    private final Preprocesador preprocesador;

    public BusquedaService(RepositorioCorpus corpus, Preprocesador preprocesador) {
        this.corpus = corpus;
        this.preprocesador = preprocesador;
    }

    public BusquedaResponse buscar(BusquedaRequest solicitud) {
        String patron = normalizarPatron(solicitud == null ? null : solicitud.patron(), preprocesador);
        List<Articulo> articulos = corpus.todos();
        String articuloTraza = solicitud.conTraza() ? elegirArticuloTraza(solicitud.articuloId(), patron, articulos) : null;

        List<BusquedaResponse.Resultado> resultados = new ArrayList<>();
        for (StringMatcher algoritmo : ALGORITMOS) {
            resultados.add(ejecutar(algoritmo, patron, articulos, articuloTraza));
        }
        return new BusquedaResponse(patron, articuloTraza, resultados);
    }

    static String normalizarPatron(String patron, Preprocesador preprocesador) {
        String normalizado = patron == null ? "" : preprocesador.normalizar(patron);
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException("El patrón debe tener al menos una letra o un dígito");
        }
        return normalizado;
    }

    private BusquedaResponse.Resultado ejecutar(StringMatcher algoritmo, String patron, List<Articulo> articulos,
                                                String articuloTraza) {
        List<BusquedaResponse.Coincidencia> porArticulo = new ArrayList<>();
        int total = 0;
        long comparaciones = 0;
        long preprocesamientoNs = 0;
        long busquedaNs = 0;
        TrazaDTO trazaDto = null;
        for (Articulo articulo : articulos) {
            boolean conTraza = articulo.id().equals(articuloTraza);
            Traza traza = conTraza ? Traza.activada() : Traza.desactivada();
            ResultadoBusqueda r = algoritmo.buscar(patron, articulo.textoParaAlgoritmos(), traza);
            total += r.posiciones().size();
            comparaciones += r.comparaciones();
            preprocesamientoNs += r.preprocesamientoNs();
            busquedaNs += r.busquedaNs();
            if (!r.posiciones().isEmpty()) {
                porArticulo.add(new BusquedaResponse.Coincidencia(articulo.id(), r.posiciones()));
            }
            if (conTraza) {
                trazaDto = new TrazaDTO(algoritmo.nombre(), r.pasos());
            }
        }
        return new BusquedaResponse.Resultado(algoritmo.nombre(), total, porArticulo, comparaciones,
                preprocesamientoNs, busquedaNs, trazaDto);
    }

    private String elegirArticuloTraza(String pedido, String patron, List<Articulo> articulos) {
        if (pedido != null && !pedido.isBlank()) {
            if (corpus.porId(pedido).isEmpty()) {
                throw new IllegalArgumentException("No existe el artículo " + pedido);
            }
            return pedido;
        }
        StringMatcher referencia = ALGORITMOS.get(0);
        for (Articulo articulo : articulos) {
            if (!referencia.buscar(patron, articulo.textoParaAlgoritmos()).posiciones().isEmpty()) {
                return articulo.id();
            }
        }
        return articulos.isEmpty() ? null : articulos.get(0).id();
    }
}
