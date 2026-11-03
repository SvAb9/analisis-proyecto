package co.edu.uniquindio.bibliometria.algoritmos.similitud;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Paso;
import java.util.List;

/** Resultado de comparar dos textos con un algoritmo de similitud. */
public record ResultadoSimilitud(
        String algoritmo,
        double similitud,
        double valorCrudo,
        long tiempoNs,
        List<Paso> pasos) {
}
