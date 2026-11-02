package co.edu.uniquindio.bibliometria.corpus.preprocess;

import java.text.Normalizer;

/**
 * Normalización de texto: minúsculas, sin tildes ni signos, con espacios simples.
 * Se recorre carácter por carácter (sin expresiones regulares), O(n).
 *
 * <p>Ejemplo: {@code "Inteligencia Artificial (IA): ¡Evaluación!"} se vuelve
 * {@code "inteligencia artificial ia evaluacion"}.</p>
 */
public class Normalizador {

    /** Devuelve texto corrido en minúsculas, solo letras y dígitos separados por un espacio. */
    public String normalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
        // NFD separa la letra de su tilde ("é" -> "e" + tilde) para poder descartar la tilde.
        String descompuesto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        StringBuilder salida = new StringBuilder(descompuesto.length());
        boolean ultimoFueEspacio = true;
        for (int i = 0; i < descompuesto.length(); i++) {
            char c = descompuesto.charAt(i);
            if (Character.getType(c) == Character.NON_SPACING_MARK) {
                continue;
            }
            c = Character.toLowerCase(c);
            if (Character.isLetterOrDigit(c)) {
                salida.append(c);
                ultimoFueEspacio = false;
            } else if (!ultimoFueEspacio) {
                salida.append(' ');
                ultimoFueEspacio = true;
            }
        }
        int fin = salida.length();
        if (fin > 0 && salida.charAt(fin - 1) == ' ') {
            salida.setLength(fin - 1);
        }
        return salida.toString();
    }
}
