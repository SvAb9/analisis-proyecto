package co.edu.uniquindio.bibliometria.util.estructuras;

/**
 * Nodo de lista sencillamente enlazada: un valor y la referencia al siguiente nodo.
 * Base de ListaEnlazada, Pila y Cola (ver "Estructuras de Datos", sección 1.2.1).
 */
public class Nodo<T> {

    private T valor;
    private Nodo<T> siguiente;

    public Nodo() {
        this.siguiente = null;
    }

    public Nodo(T valor) {
        this.valor = valor;
        this.siguiente = null;
    }

    public Nodo(T valor, Nodo<T> siguiente) {
        this.valor = valor;
        this.siguiente = siguiente;
    }

    public T getValor() {
        return valor;
    }

    public void setValor(T valor) {
        this.valor = valor;
    }

    public Nodo<T> getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return "" + valor;
    }
}
