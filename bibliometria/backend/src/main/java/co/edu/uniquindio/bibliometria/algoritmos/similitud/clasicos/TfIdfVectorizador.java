package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.PreparadorTexto;
import co.edu.uniquindio.bibliometria.util.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import co.edu.uniquindio.bibliometria.util.estructuras.NodoArbol;

/** Calcula los pesos TF-IDF de un documento sobre el corpus. */
public class TfIdfVectorizador {

    private final ArbolBinarioBusqueda<String, Integer> frecuenciaDocumental = new ArbolBinarioBusqueda<>();
    private int numeroDocumentos;

    public record Peso(String termino, int frecuencia, double tf, double idf, double peso) {}

    public static TfIdfVectorizador ajustar(Iterable<String> textos) {
        TfIdfVectorizador vectorizador = new TfIdfVectorizador();
        for (String texto : textos) {
            vectorizador.agregarDocumento(PreparadorTexto.tokensContenido(texto));
        }
        return vectorizador;
    }

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
