package co.edu.uniquindio.bibliometria.util.estructuras;

/**
 * Nodo de árbol binario de búsqueda: clave, valor asociado y referencias
 * izquierdo/derecho (ver "Estructuras de Datos", sección 2.2.1).
 * Se agrega {@code valor} para usar el árbol como diccionario (término → frecuencia, palabra → vector).
 */
public class NodoArbol<K extends Comparable<K>, V> {

    private final K clave;
    private V valor;
    private NodoArbol<K, V> izquierdo;
    private NodoArbol<K, V> derecho;

    public NodoArbol(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
        this.izquierdo = null;
        this.derecho = null;
    }

    public K getClave() {
        return clave;
    }

    public V getValor() {
        return valor;
    }

    public void setValor(V valor) {
        this.valor = valor;
    }

    public NodoArbol<K, V> getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoArbol<K, V> izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoArbol<K, V> getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoArbol<K, V> derecho) {
        this.derecho = derecho;
    }

    public boolean esHoja() {
        return izquierdo == null && derecho == null;
    }
}
