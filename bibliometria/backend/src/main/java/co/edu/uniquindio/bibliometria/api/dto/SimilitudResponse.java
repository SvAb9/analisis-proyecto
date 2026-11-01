package co.edu.uniquindio.bibliometria.api.dto;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import java.util.List;

/** Salida de POST /api/similitud: un elemento por cada par de artículos. */
public record SimilitudResponse(List<Par> pares) {

    /** Comparación de dos artículos con todos los algoritmos pedidos. */
    public record Par(String idA, String idB, List<ResultadoSimilitud> resultados) {
    }
}
