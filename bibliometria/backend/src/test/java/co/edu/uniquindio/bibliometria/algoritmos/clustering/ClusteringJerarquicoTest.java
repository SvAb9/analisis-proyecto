package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;

/**
 * Caso de prueba conocido: cinco puntos en una recta en las posiciones 0, 1, 5, 6 y 20.
 * Las alturas de fusión esperadas se calcularon a mano para cada criterio.
 */
class ClusteringJerarquicoTest {

    private static MatrizDistancias matrizEnRecta() {
        double[] pos = {0, 1, 5, 6, 20};
        double[][] d = new double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                d[i][j] = Math.abs(pos[i] - pos[j]);
            }
        }
        return new MatrizDistancias(List.of("a", "b", "c", "d", "e"), d);
    }

    private static void verificar(CriterioEnlace criterio, double[] alturas) {
        Dendrograma dendrograma = new ClusteringJerarquico(criterio).agrupar(matrizEnRecta());
        assertEquals(4, dendrograma.fusiones().size());
        for (int i = 0; i < alturas.length; i++) {
            assertEquals(alturas[i], dendrograma.fusiones().get(i).distancia(), 1e-9);
        }
        assertEquals(5, dendrograma.raiz().hojas().size());
    }

    @Test
    void singleLinkageFusionaALasAlturasEsperadas() {
        verificar(new SingleLinkage(), new double[] {1, 1, 4, 14});
    }

    @Test
    void completeLinkageFusionaALasAlturasEsperadas() {
        verificar(new CompleteLinkage(), new double[] {1, 1, 6, 20});
    }

    @Test
    void averageLinkageFusionaALasAlturasEsperadas() {
        verificar(new AverageLinkage(), new double[] {1, 1, 5, 17});
    }

    @Test
    void cortarEnTresClusteresSeparaLosGruposNaturales() {
        Dendrograma dendrograma = new ClusteringJerarquico(new AverageLinkage()).agrupar(matrizEnRecta());
        assertEquals(List.of(List.of(0, 1), List.of(2, 3), List.of(4)), dendrograma.cortar(3));
    }

    @Test
    void laTrazaRegistraUnPasoPorFusion() {
        Traza traza = Traza.activada();
        new ClusteringJerarquico(new SingleLinkage()).agrupar(matrizEnRecta(), traza);
        assertEquals(4, traza.pasos().size());
    }

    @Test
    void rechazaMenosDeDosArticulos() {
        MatrizDistancias uno = new MatrizDistancias(List.of("a"), new double[][] {{0}});
        assertThrows(IllegalArgumentException.class,
                () -> new ClusteringJerarquico(new SingleLinkage()).agrupar(uno));
    }

    @Test
    void rechazaMatrizAsimetrica() {
        assertThrows(IllegalArgumentException.class,
                () -> new MatrizDistancias(List.of("a", "b"), new double[][] {{0, 1}, {2, 0}}));
    }

    @Test
    void desdeSimilitudNoGeneraDistanciasNegativas() {
        double[][] similitud = {{1, 1.0000000000000002}, {1.0000000000000002, 1}};
        MatrizDistancias matriz = MatrizDistancias.desdeSimilitud(List.of("a", "b"), similitud);
        assertEquals(0.0, matriz.distancia(0, 1), 1e-12);
    }
}
