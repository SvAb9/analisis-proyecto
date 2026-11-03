package co.edu.uniquindio.bibliometria.algoritmos.clustering;

/** Nodo del dendrograma: un artículo o la fusión de dos clústeres. */
public final class NodoDendrograma {

    private final int indiceHoja;
    private final String articuloId;
    private final NodoDendrograma izquierda;
    private final NodoDendrograma derecha;
    private final double altura;
    private final int[] hojas;

    private NodoDendrograma(int indiceHoja, String articuloId, NodoDendrograma izquierda,
                            NodoDendrograma derecha, double altura, int[] hojas) {
        this.indiceHoja = indiceHoja;
        this.articuloId = articuloId;
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.altura = altura;
        this.hojas = hojas;
    }

    public static NodoDendrograma hoja(int indice, String articuloId) {
        return new NodoDendrograma(indice, articuloId, null, null, 0.0, new int[] {indice});
    }

    public static NodoDendrograma fusion(NodoDendrograma a, NodoDendrograma b, double altura) {
        return new NodoDendrograma(-1, null, a, b, altura, unir(a.hojas, b.hojas));
    }

    static int[] unir(int[] a, int[] b) {
        int[] unido = new int[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            unido[i] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            unido[a.length + i] = b[i];
        }
        return unido;
    }

    public boolean esHoja() {
        return izquierda == null;
    }

    public int indiceHoja() {
        return indiceHoja;
    }

    public String articuloId() {
        return articuloId;
    }

    public NodoDendrograma izquierda() {
        return izquierda;
    }

    public NodoDendrograma derecha() {
        return derecha;
    }

    public double altura() {
        return altura;
    }

    public int[] hojas() {
        return hojas;
    }
}
