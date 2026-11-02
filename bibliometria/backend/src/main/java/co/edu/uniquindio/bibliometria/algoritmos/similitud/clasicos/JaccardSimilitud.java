package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.PreparadorTexto;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.util.estructuras.ArbolBinarioBusqueda;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import co.edu.uniquindio.bibliometria.util.estructuras.Nodo;

import java.util.List;

/**
 * Intersección sobre unión de conjuntos de tokens.
 *
 * <p>J(A, B) = |A ∩ B| / |A ∪ B| = |A ∩ B| / (|A| + |B| − |A ∩ B|).</p>
 *
 * <p>Cada conjunto se construye insertando los tokens de contenido en un
 * {@link ArbolBinarioBusqueda}, que descarta los repetidos: O(n·h). Su recorrido inorden da los
 * términos ordenados, y la intersección se cuenta con un recorrido de mezcla de las dos listas
 * ordenadas en O(|A| + |B|). Total O(n log n) en promedio.</p>
 */
public class JaccardSimilitud implements SimilitudStrategy {

    @Override
    public String nombre() {
        return "Jaccard";
    }

    @Override
    public ResultadoSimilitud comparar(String textoA, String textoB, Traza traza) {
        long inicio = System.nanoTime();
        ListaEnlazada<String> conjuntoA = conjunto(PreparadorTexto.tokensContenido(textoA));
        ListaEnlazada<String> conjuntoB = conjunto(PreparadorTexto.tokensContenido(textoB));
        ListaEnlazada<String> interseccion = interseccion(conjuntoA, conjuntoB);
        int tamA = conjuntoA.getCantidad();
        int tamB = conjuntoB.getCantidad();
        int tamInterseccion = interseccion.getCantidad();
        int tamUnion = tamA + tamB - tamInterseccion;
        double similitud = tamUnion == 0 ? 1.0 : (double) tamInterseccion / tamUnion;
        long tiempo = System.nanoTime() - inicio;
        boolean conTraza = traza != null && traza.estaActiva();
        if (conTraza) {
            traza.registrar("|A| = " + tamA, "términos únicos de A: " + conjuntoA);
            traza.registrar("|B| = " + tamB, "términos únicos de B: " + conjuntoB);
            traza.registrar("|A ∩ B| = " + tamInterseccion, "términos comunes: " + interseccion);
            traza.registrar("|A ∪ B| = " + tamUnion, tamA + " + " + tamB + " - " + tamInterseccion);
            traza.registrar("similitud = " + similitud, tamInterseccion + " / " + tamUnion);
        }
        return new ResultadoSimilitud(nombre(), similitud, tamInterseccion, tiempo,
                conTraza ? traza.pasos() : List.of());
    }

    /** Conjunto ordenado de términos sin repetir. */
    public static ListaEnlazada<String> conjunto(String[] tokens) {
        ArbolBinarioBusqueda<String, Boolean> arbol = new ArbolBinarioBusqueda<>();
        for (String token : tokens) {
            arbol.insertar(token);
        }
        return arbol.clavesOrdenadas();
    }

    /** Mezcla de dos listas ordenadas: avanza en la que tenga la clave menor. O(|A| + |B|). */
    public static ListaEnlazada<String> interseccion(ListaEnlazada<String> a, ListaEnlazada<String> b) {
        ListaEnlazada<String> comunes = new ListaEnlazada<>();
        Nodo<String> p = a.getCabecera().getSiguiente();
        Nodo<String> q = b.getCabecera().getSiguiente();
        while (p != null && q != null) {
            int comparacion = p.getValor().compareTo(q.getValor());
            if (comparacion == 0) {
                comunes.insertarFinal(p.getValor());
                p = p.getSiguiente();
                q = q.getSiguiente();
            } else if (comparacion < 0) {
                p = p.getSiguiente();
            } else {
                q = q.getSiguiente();
            }
        }
        return comunes;
    }
}
