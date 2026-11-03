package co.edu.uniquindio.bibliometria.util.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Lista sencillamente enlazada con nodo cabecera y referencia al último nodo
 * (ver "Estructuras de Datos", sección 1.2.2).
 *
 * <p>Complejidad: insertarInicio O(1), insertarFinal O(1) gracias a la referencia
 * {@code ultimo}, obtenerValor(posicion) O(n), buscar O(n), recorrido O(n).</p>
 *
 * <p>Implementa {@link Iterable} solo para poder recorrerla con for-each; el recorrido
 * es el mismo que el del método listar() del curso.</p>
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private final Nodo<T> cabecera;
    private Nodo<T> ultimo;
    private int cantidad;

    public ListaEnlazada() {
        cabecera = new Nodo<>(null, null);
        ultimo = null;
        cantidad = 0;
    }

    /** O(1). */
    public boolean estaVacia() {
        return cabecera.getSiguiente() == null;
    }

    /** O(1). */
    public int getCantidad() {
        return cantidad;
    }

    public Nodo<T> getCabecera() {
        return cabecera;
    }

    /** Inserta un nodo a continuación de la cabecera. O(1). */
    public void insertarInicio(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor, null);
        if (estaVacia()) {
            cabecera.setSiguiente(nuevo);
            ultimo = nuevo;
        } else {
            nuevo.setSiguiente(cabecera.getSiguiente());
            cabecera.setSiguiente(nuevo);
        }
        cantidad++;
    }

    /** Inserta un nodo al final usando la referencia ultimo. O(1). */
    public void insertarFinal(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor, null);
        if (estaVacia()) {
            cabecera.setSiguiente(nuevo);
        } else {
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        cantidad++;
    }

    /** Valor del nodo en la posición indicada (0 = primer nodo después de la cabecera). O(n). */
    public T obtenerValor(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            throw new IndexOutOfBoundsException("Posición fuera del rango: " + posicion);
        }
        Nodo<T> actual = cabecera.getSiguiente();
        int contador = 0;
        while (contador < posicion) {
            actual = actual.getSiguiente();
            contador++;
        }
        return actual.getValor();
    }

    /** Valor del primer nodo. O(1). */
    public T primero() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista está vacía");
        }
        return cabecera.getSiguiente().getValor();
    }

    /** Búsqueda lineal; retorna al encontrar la primera coincidencia. O(n). */
    public boolean buscar(T valor) {
        Nodo<T> actual = cabecera.getSiguiente();
        boolean centinela = false;
        while (actual != null && !centinela) {
            T dato = actual.getValor();
            if (dato == null ? valor == null : dato.equals(valor)) {
                centinela = true;
            }
            actual = actual.getSiguiente();
        }
        return centinela;
    }

    /** Deja solo el nodo cabecera. O(1). */
    public void eliminarLista() {
        cabecera.setSiguiente(null);
        ultimo = null;
        cantidad = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabecera.getSiguiente();

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T valor = actual.getValor();
                actual = actual.getSiguiente();
                return valor;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder salida = new StringBuilder("[");
        Nodo<T> actual = cabecera.getSiguiente();
        while (actual != null) {
            salida.append(actual.getValor());
            if (actual.getSiguiente() != null) {
                salida.append(", ");
            }
            actual = actual.getSiguiente();
        }
        return salida.append("]").toString();
    }
}
