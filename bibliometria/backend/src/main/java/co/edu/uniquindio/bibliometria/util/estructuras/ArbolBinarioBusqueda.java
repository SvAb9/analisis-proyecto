package co.edu.uniquindio.bibliometria.util.estructuras;

/**
 * Árbol binario de búsqueda usado como conjunto ordenado y como diccionario
 * (ver "Estructuras de Datos", capítulo 2).
 *
 * <p>Inserción y búsqueda: O(h), con h la altura del árbol; O(log n) en promedio y O(n)
 * en el peor caso (claves insertadas ya ordenadas). El recorrido inorden entrega las claves
 * ordenadas en O(n), lo que permite intersecar dos conjuntos o hacer el producto punto de dos
 * vectores dispersos con un recorrido de mezcla en O(n + m).</p>
 *
 * <p>Inserción y búsqueda son iterativas para no desbordar la pila de llamadas cuando el árbol
 * se degenera; el recorrido inorden usa una {@link Pila} explícita por la misma razón.</p>
 */
public class ArbolBinarioBusqueda<K extends Comparable<K>, V> {

    private NodoArbol<K, V> raiz;
    private int cantidad;

    public ArbolBinarioBusqueda() {
        raiz = null;
        cantidad = 0;
    }

    public NodoArbol<K, V> getRaiz() {
        return raiz;
    }

    public int getCantidad() {
        return cantidad;
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    /**
     * Inserta la clave con su valor; si la clave ya existe reemplaza el valor.
     * @return true si la clave era nueva
     */
    public boolean insertar(K clave, V valor) {
        if (raiz == null) {
            raiz = new NodoArbol<>(clave, valor);
            cantidad++;
            return true;
        }
        NodoArbol<K, V> auxiliar = raiz;
        while (true) {
            int comparacion = clave.compareTo(auxiliar.getClave());
            if (comparacion == 0) {
                auxiliar.setValor(valor);
                return false;
            }
            if (comparacion > 0) {
                if (auxiliar.getDerecho() == null) {
                    auxiliar.setDerecho(new NodoArbol<>(clave, valor));
                    cantidad++;
                    return true;
                }
                auxiliar = auxiliar.getDerecho();
            } else {
                if (auxiliar.getIzquierdo() == null) {
                    auxiliar.setIzquierdo(new NodoArbol<>(clave, valor));
                    cantidad++;
                    return true;
                }
                auxiliar = auxiliar.getIzquierdo();
            }
        }
    }

    /** Inserta la clave sin valor asociado (uso como conjunto). */
    public boolean insertar(K clave) {
        return insertar(clave, null);
    }

    /** Nodo con la clave dada o null. O(h). */
    public NodoArbol<K, V> buscarNodo(K clave) {
        NodoArbol<K, V> auxiliar = raiz;
        while (auxiliar != null) {
            int comparacion = clave.compareTo(auxiliar.getClave());
            if (comparacion == 0) {
                return auxiliar;
            }
            auxiliar = comparacion > 0 ? auxiliar.getDerecho() : auxiliar.getIzquierdo();
        }
        return null;
    }

    public boolean buscar(K clave) {
        return buscarNodo(clave) != null;
    }

    /** Valor asociado a la clave o null si no existe. O(h). */
    public V obtener(K clave) {
        NodoArbol<K, V> nodo = buscarNodo(clave);
        return nodo == null ? null : nodo.getValor();
    }

    /** Altura del árbol: -1 si está vacío (misma convención del método altura del curso). */
    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoArbol<K, V> nodo) {
        if (nodo == null) {
            return -1;
        }
        return 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

    /**
     * Reconstruye el árbol con altura mínima: toma los nodos en inorden y vuelve a enlazarlos
     * insertando primero la mediana de cada rango. Evita el peor caso O(n) de búsqueda cuando
     * las claves llegaron ordenadas (p. ej. un archivo de stopwords alfabético). O(n).
     */
    @SuppressWarnings("unchecked")
    public void rebalancear() {
        NodoArbol<K, V>[] nodos = new NodoArbol[cantidad];
        int i = 0;
        for (NodoArbol<K, V> nodo : inorden()) {
            nodos[i++] = nodo;
        }
        raiz = enlazarMediana(nodos, 0, cantidad - 1);
    }

    private NodoArbol<K, V> enlazarMediana(NodoArbol<K, V>[] nodos, int desde, int hasta) {
        if (desde > hasta) {
            return null;
        }
        int medio = (desde + hasta) >>> 1;
        NodoArbol<K, V> nodo = nodos[medio];
        nodo.setIzquierdo(enlazarMediana(nodos, desde, medio - 1));
        nodo.setDerecho(enlazarMediana(nodos, medio + 1, hasta));
        return nodo;
    }

    /** Recorrido inorden (claves ordenadas) en una lista enlazada de nodos. O(n). */
    public ListaEnlazada<NodoArbol<K, V>> inorden() {
        ListaEnlazada<NodoArbol<K, V>> salida = new ListaEnlazada<>();
        Pila<NodoArbol<K, V>> pila = new Pila<>();
        NodoArbol<K, V> actual = raiz;
        while (actual != null || !pila.estaVacia()) {
            while (actual != null) {
                pila.push(actual);
                actual = actual.getIzquierdo();
            }
            actual = pila.pop();
            salida.insertarFinal(actual);
            actual = actual.getDerecho();
        }
        return salida;
    }

    /** Solo las claves, en orden. O(n). */
    public ListaEnlazada<K> clavesOrdenadas() {
        ListaEnlazada<K> claves = new ListaEnlazada<>();
        for (NodoArbol<K, V> nodo : inorden()) {
            claves.insertarFinal(nodo.getClave());
        }
        return claves;
    }
}
