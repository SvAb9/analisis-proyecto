package co.edu.uniquindio.bibliometria.algoritmos.similitud;

import co.edu.uniquindio.bibliometria.util.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

/**
 * Preparación mínima de texto que necesitan los algoritmos de R1: normalización y tokenización
 * escritas carácter por carácter (sin expresiones regulares) y filtro de stopwords.
 *
 * <p>Es independiente de {@code corpus/preprocess} para que R1 se pueda probar solo. En la
 * integración puede reemplazarse por PreprocesadorTexto si se quiere un único pipeline.</p>
 *
 * <p>Las stopwords se guardan en un {@link ArbolBinarioBusqueda}: consultar si un token es
 * stopword cuesta O(log s) en promedio (s ≈ 170 palabras).</p>
 */
public final class PreparadorTexto {

    private static final ArbolBinarioBusqueda<String, Boolean> STOPWORDS = cargarStopwords();

    private PreparadorTexto() {}

    /**
     * Minúsculas, solo letras, dígitos y espacios simples. Es la entrada de Levenshtein,
     * que trabaja a nivel de carácter. O(n).
     */
    public static String normalizar(String texto) {
        StringBuilder salida = new StringBuilder(texto.length());
        boolean ultimoFueEspacio = true;
        for (int i = 0; i < texto.length(); i++) {
            char c = Character.toLowerCase(texto.charAt(i));
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

    /**
     * Tokens de contenido: palabras en minúscula de al menos 2 letras, sin números puros
     * y sin stopwords. Se devuelven en un arreglo porque los algoritmos de programación dinámica
     * necesitan acceso por índice en O(1). O(n).
     */
    public static String[] tokensContenido(String texto) {
        ListaEnlazada<String> tokens = new ListaEnlazada<>();
        StringBuilder actual = new StringBuilder();
        for (int i = 0; i <= texto.length(); i++) {
            char c = i < texto.length() ? Character.toLowerCase(texto.charAt(i)) : ' ';
            if (Character.isLetterOrDigit(c)) {
                actual.append(c);
            } else if (actual.length() > 0) {
                agregarSiEsContenido(actual.toString(), tokens);
                actual.setLength(0);
            }
        }
        String[] arreglo = new String[tokens.getCantidad()];
        int i = 0;
        for (String token : tokens) {
            arreglo[i++] = token;
        }
        return arreglo;
    }

    public static boolean esStopword(String token) {
        return STOPWORDS.buscar(token);
    }

    private static void agregarSiEsContenido(String token, ListaEnlazada<String> tokens) {
        if (token.length() < 2 || esNumero(token) || esStopword(token)) {
            return;
        }
        tokens.insertarFinal(token);
    }

    private static boolean esNumero(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static ArbolBinarioBusqueda<String, Boolean> cargarStopwords() {
        ArbolBinarioBusqueda<String, Boolean> arbol = new ArbolBinarioBusqueda<>();
        try (InputStream in = PreparadorTexto.class.getResourceAsStream("/similitud/stopwords-en.txt")) {
            if (in == null) {
                return arbol;
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty() && !linea.startsWith("#")) {
                    arbol.insertar(linea, Boolean.TRUE);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudieron leer las stopwords", e);
        }
        // El archivo está en orden alfabético: sin rebalancear el árbol sería una lista de altura s.
        arbol.rebalancear();
        return arbol;
    }
}
