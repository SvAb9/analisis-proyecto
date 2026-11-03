package co.edu.uniquindio.bibliometria.api.dto;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import java.util.List;

/** Salida de POST /api/similitud. */
public record SimilitudResponse(List<Par> pares) {

    public record Par(String idA, String idB, List<ResultadoSimilitud> resultados) {
    }
}
