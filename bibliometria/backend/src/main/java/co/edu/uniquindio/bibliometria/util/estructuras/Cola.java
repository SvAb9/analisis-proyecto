package co.edu.uniquindio.bibliometria.util.estructuras;

/** Cola FIFO sobre lista enlazada. */
public class Cola<T> {

    private Nodo<T> inicio;
    private Nodo<T> fin;
    private int cantidad;

    public Cola() {
        inicio = null;
        fin = null;
        cantidad = 0;
    }

    public boolean estaVacia() {
        return inicio == null;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void enqueue(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            inicio = nuevo;
        } else {
            fin.setSiguiente(nuevo);
        }
        fin = nuevo;
        cantidad++;
    }

    public T dequeue() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía");
        }
        T dato = inicio.getValor();
        inicio = inicio.getSiguiente();
        if (inicio == null) {
            fin = null;
        }
        cantidad--;
        return dato;
    }

    public T top() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return inicio.getValor();
    }
}
