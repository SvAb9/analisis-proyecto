package co.edu.uniquindio.bibliometria.util.estructuras;

/** Árbol binario de búsqueda clave-valor. */
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

    public boolean insertar(K clave) {
        return insertar(clave, null);
    }

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

    public V obtener(K clave) {
        NodoArbol<K, V> nodo = buscarNodo(clave);
        return nodo == null ? null : nodo.getValor();
    }

    public int altura() {
        return altura(raiz);
    }

    private int altura(NodoArbol<K, V> nodo) {
        if (nodo == null) {
            return -1;
        }
        return 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }

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

    public ListaEnlazada<K> clavesOrdenadas() {
        ListaEnlazada<K> claves = new ListaEnlazada<>();
        for (NodoArbol<K, V> nodo : inorden()) {
            claves.insertarFinal(nodo.getClave());
        }
        return claves;
    }
}
