package co.edu.uniquindio.bibliometria.algoritmos.similitud;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Paso;
import java.util.List;

/**
 * Resultado de comparar dos textos con un algoritmo de similitud.
 *
 * @param algoritmo  nombre del algoritmo (igual a SimilitudStrategy.nombre())
 * @param similitud  valor normalizado en [0, 1]; 1 = textos idénticos
 * @param valorCrudo valor natural del algoritmo antes de normalizar
 *                   (distancia de edición, puntaje de alineamiento, ángulo, distancia euclidiana...)
 * @param tiempoNs   tiempo de ejecución en nanosegundos
 * @param pasos      pasos intermedios; vacío si la traza estaba desactivada
 */
public record ResultadoSimilitud(
        String algoritmo,
        double similitud,
        double valorCrudo,
        long tiempoNs,
        List<Paso> pasos) {
}
