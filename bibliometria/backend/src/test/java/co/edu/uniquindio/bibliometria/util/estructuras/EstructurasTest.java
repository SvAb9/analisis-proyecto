package co.edu.uniquindio.bibliometria.util.estructuras;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de las estructuras de datos basadas en nodos.
 */
class EstructurasTest {

    @Test
    void listaEnlazada() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        assertTrue(lista.estaVacia());
        lista.insertarFinal("b");
        lista.insertarFinal("c");
        lista.insertarInicio("a");
        assertEquals(3, lista.getCantidad());
        assertEquals("[a, b, c]", lista.toString());
        assertEquals("c", lista.obtenerValor(2));
        assertTrue(lista.buscar("b"));
        assertFalse(lista.buscar("z"));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtenerValor(3));
        lista.eliminarLista();
        assertTrue(lista.estaVacia());
    }

    @Test
    void pilaEsLifo() {
        Pila<Integer> pila = new Pila<>();
        pila.push(1);
        pila.push(2);
        pila.push(3);
        assertEquals(3, pila.top());
        assertEquals(3, pila.pop());
        assertEquals(2, pila.pop());
        assertEquals(1, pila.pop());
        assertTrue(pila.estaVacia());
        assertThrows(IllegalStateException.class, pila::pop);
    }

    @Test
    void colaEsFifo() {
        Cola<Integer> cola = new Cola<>();
        cola.enqueue(1);
        cola.enqueue(2);
        assertEquals(1, cola.dequeue());
        cola.enqueue(3);
        assertEquals(2, cola.dequeue());
        assertEquals(3, cola.dequeue());
        assertTrue(cola.estaVacia());
        cola.enqueue(4);
        assertEquals(4, cola.top());
    }

    @Test
    void arbolComoDiccionarioOrdenado() {
        ArbolBinarioBusqueda<String, Integer> arbol = new ArbolBinarioBusqueda<>();
        assertTrue(arbol.insertar("learning", 1));
        assertTrue(arbol.insertar("ai", 2));
        assertTrue(arbol.insertar("review", 3));
        assertFalse(arbol.insertar("ai", 5));
        assertEquals(3, arbol.getCantidad());
        assertEquals(5, arbol.obtener("ai"));
        assertNull(arbol.obtener("chatbot"));
        assertEquals("[ai, learning, review]", arbol.clavesOrdenadas().toString());
    }

    @Test
    void rebalancearCorrigeElPeorCaso() {
        ArbolBinarioBusqueda<Integer, Boolean> arbol = new ArbolBinarioBusqueda<>();
        for (int i = 1; i <= 1023; i++) {
            arbol.insertar(i);
        }
        assertEquals(1022, arbol.altura());
        arbol.rebalancear();
        assertEquals(9, arbol.altura());
        assertTrue(arbol.buscar(1) && arbol.buscar(512) && arbol.buscar(1023));
        assertEquals(1023, arbol.clavesOrdenadas().getCantidad());
    }
}
