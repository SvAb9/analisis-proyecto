package co.edu.uniquindio.bibliometria.algoritmos.clustering.evaluacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.AverageLinkage;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.ClusteringJerarquico;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.Dendrograma;
import co.edu.uniquindio.bibliometria.algoritmos.clustering.MatrizDistancias;

class SilhouetteTest {

    private static final double[] POS = {0, 1, 5, 6, 20};

    private static double[][] distancias() {
        double[][] d = new double[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                d[i][j] = Math.abs(POS[i] - POS[j]);
            }
        }
        return d;
    }

    @Test
    void siluetaDeUnaParticionConocida() {
        double esperado = (4.5 / 5.5 + 3.5 / 4.5 + 3.5 / 4.5 + 4.5 / 5.5) / 5;
        double real = Silhouette.calcular(distancias(),
                new int[][] {{0, 1}, {2, 3}, {4}});
        assertEquals(esperado, real, 1e-9);
    }

    @Test
    void elMejorCorteEsElDeTresClusteres() {
        MatrizDistancias m = new MatrizDistancias(List.of("a", "b", "c", "d", "e"), distancias());
        Dendrograma dendrograma = new ClusteringJerarquico(new AverageLinkage()).agrupar(m);
        assertEquals(3, Silhouette.mejorCorte(distancias(), dendrograma).k());
    }

    @Test
    void exigeAlMenosDosClusteres() {
        assertThrows(IllegalArgumentException.class,
                () -> Silhouette.calcular(distancias(), new int[][] {{0, 1, 2, 3, 4}}));
    }
}
