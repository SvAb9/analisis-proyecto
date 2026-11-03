package co.edu.uniquindio.bibliometria.util.estructuras;

/** Pila LIFO sobre lista enlazada. */
public class Pila<T> {

    private Nodo<T> tope;
    private int cantidad;

    public Pila() {
        tope = null;
        cantidad = 0;
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void push(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (!estaVacia()) {
            nuevo.setSiguiente(tope);
        }
        tope = nuevo;
        cantidad++;
    }

    public T pop() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía");
        }
        T dato = tope.getValor();
        tope = tope.getSiguiente();
        cantidad--;
        return dato;
    }

    public T top() {
        if (estaVacia()) {
            throw new IllegalStateException("La pila está vacía");
        }
        return tope.getValor();
    }
}
