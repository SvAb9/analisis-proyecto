package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.PreparadorTexto;
import co.edu.uniquindio.bibliometria.util.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import co.edu.uniquindio.bibliometria.util.estructuras.NodoArbol;

/**
 * Calcula TF, IDF y pesos sobre el corpus.
 *
 * <ul>
 *   <li>tf(t, d) = frecuencia de t en d / número de tokens de d</li>
 *   <li>df(t) = número de documentos del corpus que contienen t</li>
 *   <li>idf(t) = ln((1 + N) / (1 + df(t))) + 1 (IDF suavizado: nunca vale 0 ni divide por 0)</li>
 *   <li>w(t, d) = tf(t, d) · idf(t)</li>
 * </ul>
 *
 * <p>El IDF suavizado es necesario porque al comparar solo dos abstracts sin corpus, con el IDF
 * clásico ln(N/df) todo término compartido tendría idf = ln(2/2) = 0 y el coseno siempre daría 0.</p>
 *
 * <p>La frecuencia documental se guarda en un {@link ArbolBinarioBusqueda} término → df.
 * Ajustar cuesta O(T·h) con T el total de tokens del corpus; vectorizar un documento O(n·h).
 * El vector resultante es disperso: solo contiene los términos del documento, en orden alfabético.</p>
 */
public class TfIdfVectorizador {

    private final ArbolBinarioBusqueda<String, Integer> frecuenciaDocumental = new ArbolBinarioBusqueda<>();
    private int numeroDocumentos;

    /** Peso de un término dentro del vector de un documento (con sus factores, para la traza). */
    public record Peso(String termino, int frecuencia, double tf, double idf, double peso) {}

    /** Construye el vectorizador a partir de los textos del corpus. */
    public static TfIdfVectorizador ajustar(Iterable<String> textos) {
        TfIdfVectorizador vectorizador = new TfIdfVectorizador();
        for (String texto : textos) {
            vectorizador.agregarDocumento(PreparadorTexto.tokensContenido(texto));
        }
        return vectorizador;
    }

    /** Suma 1 al df de cada término distinto del documento. */
    public void agregarDocumento(String[] tokens) {
        ArbolBinarioBusqueda<String, Boolean> distintos = new ArbolBinarioBusqueda<>();
        for (String token : tokens) {
            distintos.insertar(token);
        }
        for (NodoArbol<String, Boolean> nodo : distintos.inorden()) {
            Integer df = frecuenciaDocumental.obtener(nodo.getClave());
            frecuenciaDocumental.insertar(nodo.getClave(), df == null ? 1 : df + 1);
        }
        numeroDocumentos++;
    }

    public int getNumeroDocumentos() {
        return numeroDocumentos;
    }

    public int frecuenciaDocumental(String termino) {
        Integer df = frecuenciaDocumental.obtener(termino);
        return df == null ? 0 : df;
    }

    public double idf(String termino) {
        return Math.log((1.0 + numeroDocumentos) / (1.0 + frecuenciaDocumental(termino))) + 1.0;
    }

    /** Vector TF-IDF disperso del documento, ordenado alfabéticamente por término. */
    public ListaEnlazada<Peso> vectorizar(String[] tokens) {
        ArbolBinarioBusqueda<String, Integer> conteo = new ArbolBinarioBusqueda<>();
        for (String token : tokens) {
            Integer actual = conteo.obtener(token);
            conteo.insertar(token, actual == null ? 1 : actual + 1);
        }
        ListaEnlazada<Peso> vector = new ListaEnlazada<>();
        for (NodoArbol<String, Integer> nodo : conteo.inorden()) {
            double tf = (double) nodo.getValor() / tokens.length;
            double idf = idf(nodo.getClave());
            vector.insertarFinal(new Peso(nodo.getClave(), nodo.getValor(), tf, idf, tf * idf));
        }
        return vector;
    }
}
