package co.edu.uniquindio.bibliometria.util.estructuras;

/**
 * Cola (FIFO) basada en nodos con referencias {@code inicio} y {@code fin}
 * (ver "Estructuras de Datos", sección 1.4.1). enqueue y dequeue son O(1).
 */
public class Cola<T> {

    private Nodo<T> inicio;
    private Nodo<T> fin;
    private int cantidad;

    public Cola() {
        inicio = null;
        fin = null;
        cantidad = 0;
    }

    /** O(1). */
    public boolean estaVacia() {
        return inicio == null;
    }

    public int getCantidad() {
        return cantidad;
    }

    /** Agrega al final usando la referencia fin. O(1). */
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

    /** Elimina y retorna el elemento del inicio. O(1). */
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

    /** Elemento del frente sin eliminarlo. O(1). */
    public T top() {
        if (estaVacia()) {
            throw new IllegalStateException("La cola está vacía");
        }
        return inicio.getValor();
    }
}
