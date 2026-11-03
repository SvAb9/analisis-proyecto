package co.edu.uniquindio.bibliometria.corpus.preprocess;

import java.util.ArrayList;
import java.util.List;

/** Separa el texto en palabras. */
public class Tokenizador {

    public List<String> tokenizar(String texto) {
        List<String> tokens = new ArrayList<>();
        if (texto == null) {
            return tokens;
        }
        StringBuilder actual = new StringBuilder();
        for (int i = 0; i <= texto.length(); i++) {
            char c = i < texto.length() ? Character.toLowerCase(texto.charAt(i)) : ' ';
            if (Character.isLetterOrDigit(c)) {
                actual.append(c);
            } else if (actual.length() > 0) {
                tokens.add(actual.toString());
                actual.setLength(0);
            }
        }
        return tokens;
    }
}
