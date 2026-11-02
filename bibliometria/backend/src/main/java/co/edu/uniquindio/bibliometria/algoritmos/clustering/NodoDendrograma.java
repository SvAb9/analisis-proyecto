package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import java.util.ArrayList;
import java.util.List;

/**
 * Nodo del dendrograma. Hoja: un artículo. Nodo interno: la fusión de dos clústeres,
 * con altura = distancia a la que se fusionaron.
 */
public final class NodoDendrograma {

    private final int indiceHoja;
    private final String articuloId;
    private final NodoDendrograma izquierda;
    private final NodoDendrograma derecha;
    private final double altura;
    private final List<Integer> hojas;

    private NodoDendrograma(int indiceHoja, String articuloId, NodoDendrograma izquierda,
                            NodoDendrograma derecha, double altura, List<Integer> hojas) {
        this.indiceHoja = indiceHoja;
        this.articuloId = articuloId;
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.altura = altura;
        this.hojas = hojas;
    }

    public static NodoDendrograma hoja(int indice, String articuloId) {
        return new NodoDendrograma(indice, articuloId, null, null, 0.0, List.of(indice));
    }

    public static NodoDendrograma fusion(NodoDendrograma a, NodoDendrograma b, double altura) {
        List<Integer> miembros = new ArrayList<>(a.hojas);
        miembros.addAll(b.hojas);
        return new NodoDendrograma(-1, null, a, b, altura, List.copyOf(miembros));
    }

    public boolean esHoja() {
        return izquierda == null;
    }

    /** Índice del artículo en la matriz de distancias; -1 si no es hoja. */
    public int indiceHoja() {
        return indiceHoja;
    }

    /** Id del artículo; null si no es hoja. */
    public String articuloId() {
        return articuloId;
    }

    public NodoDendrograma izquierda() {
        return izquierda;
    }

    public NodoDendrograma derecha() {
        return derecha;
    }

    /** 0 en las hojas; distancia de fusión en los nodos internos. */
    public double altura() {
        return altura;
    }

    /** Índices de todos los artículos bajo este nodo. */
    public List<Integer> hojas() {
        return hojas;
    }
}
