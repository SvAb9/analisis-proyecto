package co.edu.uniquindio.bibliometria.algoritmos.traza;

/**
 * Un paso intermedio de un algoritmo, para los casos de estudio del informe.
 *
 * @param numero      posición del paso (1, 2, 3...)
 * @param descripcion qué hizo el algoritmo (ej. "Celda [3][4] = min(...)")
 * @param datos       valores asociados (ej. "hash=1042, ventana='algo'")
 */
public record Paso(int numero, String descripcion, String datos) {
}
