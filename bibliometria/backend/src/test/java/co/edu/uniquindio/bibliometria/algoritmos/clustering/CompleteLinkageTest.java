package co.edu.uniquindio.bibliometria.algoritmos.clustering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Distancia entre clústeres A = {0, 1} y B = {2, 3} sobre puntos en 0, 1, 5 y 6. */
class CompleteLinkageTest {

    @Test
    void calculaLaDistanciaEntreDosClusteres() {
        double[][] d = {{0, 1, 5, 6}, {1, 0, 4, 5}, {5, 4, 0, 1}, {6, 5, 1, 0}};
        CompleteLinkage criterio = new CompleteLinkage();
        assertEquals("Complete", criterio.nombre());
        assertEquals(6.0, criterio.distancia(List.of(0, 1), List.of(2, 3), d), 1e-9);
    }
}
