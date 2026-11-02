package co.edu.uniquindio.bibliometria.util.estructuras;

/**
 * Pila (LIFO) basada en nodos con referencia {@code tope}
 * (ver "Estructuras de Datos", sección 1.3.1). push, pop y top son O(1).
 */
public class Pila<T> {

    private Nodo<T> tope;
    private int cantidad;

    public Pila() {
        tope = null;
        cantidad = 0;
    }

    /** O(1). */
    public boolean estaVacia() {
        return tope == null;
    }

    public int getCantidad() {
        return cantidad;
    }

    /** Inserta en el tope. O(1). */
    public void push(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (!estaVacia()) {
            nuevo.setSiguiente(tope);
        }
        tope = nuevo;
        cantidad++;
    }

    /** Elimina y retorna el elemento del tope. O(1). */
    public T pop() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía");
        }
        T dato = tope.getValor();
        tope = tope.getSiguiente();
        cantidad--;
        return dato;
    }

    /** Retorna el elemento del tope sin eliminarlo. O(1). */
    public T top() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía");
        }
        return tope.getValor();
    }
}
