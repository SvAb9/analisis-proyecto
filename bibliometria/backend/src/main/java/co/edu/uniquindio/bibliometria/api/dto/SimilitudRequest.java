package co.edu.uniquindio.bibliometria.api.dto;

import java.util.List;

/** Entrada de POST /api/similitud. */
public record SimilitudRequest(List<String> ids, List<String> algoritmos, boolean conTraza) {
}
