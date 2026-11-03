package co.edu.uniquindio.bibliometria.corpus.preprocess;

import java.text.Normalizer;

/** Limpia y normaliza el texto. */
public class Normalizador {

    public String normalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return "";
        }
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
