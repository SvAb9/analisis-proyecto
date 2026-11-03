package co.edu.uniquindio.bibliometria.service;

import co.edu.uniquindio.bibliometria.algoritmos.stringmatching.ResultadoBusqueda;
import co.edu.uniquindio.bibliometria.algoritmos.stringmatching.StringMatcher;
import co.edu.uniquindio.bibliometria.api.dto.BenchmarkResponse;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import co.edu.uniquindio.bibliometria.corpus.preprocess.Preprocesador;
import co.edu.uniquindio.bibliometria.infra.medicion.Cronometro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** Repite las búsquedas y calcula la mediana de los tiempos. */
@Service
public class BenchmarkService {

    static final int REPETICIONES_POR_DEFECTO = 101;
    static final int REPETICIONES_MAXIMAS = 1001;
    private static final int CALENTAMIENTO = 300;

    private final RepositorioCorpus corpus;
    private final Preprocesador preprocesador;

    public BenchmarkService(RepositorioCorpus corpus, Preprocesador preprocesador) {
        this.corpus = corpus;
        this.preprocesador = preprocesador;
    }

    public BenchmarkResponse medir(String patronCrudo, Integer repeticionesPedidas) {
        String patron = BusquedaService.normalizarPatron(patronCrudo, preprocesador);
        int repeticiones = repeticionesPedidas == null ? REPETICIONES_POR_DEFECTO : repeticionesPedidas;
        if (repeticiones < 1 || repeticiones > REPETICIONES_MAXIMAS) {
            throw new IllegalArgumentException("Las repeticiones deben estar entre 1 y " + REPETICIONES_MAXIMAS);
        }

        List<StringMatcher> algoritmos = BusquedaService.ALGORITMOS;
        List<String> textos = new ArrayList<>();
        long caracteres = 0;
        for (Articulo articulo : corpus.todos()) {
            String texto = articulo.textoParaAlgoritmos();
            textos.add(texto);
            caracteres += texto.length();
        }

        for (int i = 0; i < CALENTAMIENTO; i++) {
            for (StringMatcher algoritmo : algoritmos) {
                pasada(algoritmo, patron, textos);
            }
        }

        int a = algoritmos.size();
        long[][] preprocesamiento = new long[a][repeticiones];
        long[][] busqueda = new long[a][repeticiones];
        Pasada[] ultima = new Pasada[a];
        for (int r = 0; r < repeticiones; r++) {
            for (int orden = 0; orden < a; orden++) {
                int i = (r + orden) % a;
                Pasada p = pasada(algoritmos.get(i), patron, textos);
                preprocesamiento[i][r] = p.preprocesamientoNs();
                busqueda[i][r] = p.busquedaNs();
                ultima[i] = p;
            }
        }

        List<BenchmarkResponse.Medicion> mediciones = new ArrayList<>();
        for (int i = 0; i < a; i++) {
            long minimo = Long.MAX_VALUE;
            long maximo = 0;
            for (int r = 0; r < repeticiones; r++) {
                long total = preprocesamiento[i][r] + busqueda[i][r];
                minimo = Math.min(minimo, total);
                maximo = Math.max(maximo, total);
            }
            mediciones.add(new BenchmarkResponse.Medicion(algoritmos.get(i).nombre(),
                    Cronometro.mediana(preprocesamiento[i]), Cronometro.mediana(busqueda[i]), minimo, maximo,
                    ultima[i].comparaciones(), ultima[i].ocurrencias()));
        }
        return new BenchmarkResponse(patron, patron.length(), textos.size(), caracteres, repeticiones, CALENTAMIENTO, mediciones);
    }

    private Pasada pasada(StringMatcher algoritmo, String patron, List<String> textos) {
        long preprocesamientoNs = 0;
        long busquedaNs = 0;
        long comparaciones = 0;
        int ocurrencias = 0;
        for (String texto : textos) {
            ResultadoBusqueda r = algoritmo.buscar(patron, texto);
            preprocesamientoNs += r.preprocesamientoNs();
            busquedaNs += r.busquedaNs();
            comparaciones += r.comparaciones();
            ocurrencias += r.posiciones().size();
        }
        return new Pasada(preprocesamientoNs, busquedaNs, comparaciones, ocurrencias);
    }

    private record Pasada(long preprocesamientoNs, long busquedaNs, long comparaciones, int ocurrencias) {
    }
}
