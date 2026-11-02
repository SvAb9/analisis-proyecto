package co.edu.uniquindio.bibliometria.corpus.preprocess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Elimina palabras vacías (artículos, preposiciones, conectores...) que no aportan significado.
 * Las listas están en {@code resources/stopwords/en.txt} y {@code es.txt}; se guardan en un
 * conjunto hash, así consultar una palabra cuesta O(1) en promedio.
 */
public class StopwordFilter {

    private final Set<String> stopwords = new HashSet<>();

    /** Carga las listas de inglés y español. */
    public StopwordFilter() {
        this(unir(leerRecurso("/stopwords/en.txt"), leerRecurso("/stopwords/es.txt")));
    }

    /** Filtro con una lista propia (útil en pruebas). */
    public StopwordFilter(Collection<String> palabras) {
        agregar(palabras);
    }

    public boolean esStopword(String token) {
        return stopwords.contains(token);
    }

    /** Devuelve los tokens que no son stopwords, en el mismo orden. */
    public List<String> filtrar(List<String> tokens) {
        List<String> resultado = new ArrayList<>();
        for (String token : tokens) {
            if (!esStopword(token)) {
                resultado.add(token);
            }
        }
        return resultado;
    }

    /** Las palabras se normalizan igual que el texto (sin tildes ni mayúsculas) antes de guardarse. */
    private void agregar(Collection<String> palabras) {
        Normalizador normalizador = new Normalizador();
        Tokenizador tokenizador = new Tokenizador();
        for (String palabra : palabras) {
            stopwords.addAll(tokenizador.tokenizar(normalizador.normalizar(palabra)));
        }
    }

    private static List<String> unir(List<String> a, List<String> b) {
        List<String> todas = new ArrayList<>(a);
        todas.addAll(b);
        return todas;
    }

    private static List<String> leerRecurso(String ruta) {
        List<String> palabras = new ArrayList<>();
        try (InputStream in = StopwordFilter.class.getResourceAsStream(ruta)) {
            if (in == null) {
                return palabras;
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                    palabras.add(linea);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudieron leer las stopwords " + ruta, e);
        }
        return palabras;
    }
}
