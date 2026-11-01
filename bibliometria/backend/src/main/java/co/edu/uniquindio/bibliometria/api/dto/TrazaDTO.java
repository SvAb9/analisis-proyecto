package co.edu.uniquindio.bibliometria.api.dto;

import co.edu.uniquindio.bibliometria.algoritmos.traza.Paso;
import java.util.List;

/** Pasos intermedios de un algoritmo, para mostrar en pantalla o copiar al informe. */
public record TrazaDTO(String algoritmo, List<Paso> pasos) {
}
