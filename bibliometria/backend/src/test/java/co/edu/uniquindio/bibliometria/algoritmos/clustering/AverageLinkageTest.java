package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Distancia entre clústeres A = {0, 1} y B = {2, 3} sobre puntos en 0, 1, 5 y 6. */
class AverageLinkageTest {

    @Test
    void calculaLaDistanciaEntreDosClusteres() {
        double[][] d = {{0, 1, 5, 6}, {1, 0, 4, 5}, {5, 4, 0, 1}, {6, 5, 1, 0}};
        AverageLinkage criterio = new AverageLinkage();
        assertEquals("Average", criterio.nombre());
        assertEquals(5.0, criterio.distancia(new int[] {0, 1}, new int[] {2, 3}, d), 1e-9);
    }
}
