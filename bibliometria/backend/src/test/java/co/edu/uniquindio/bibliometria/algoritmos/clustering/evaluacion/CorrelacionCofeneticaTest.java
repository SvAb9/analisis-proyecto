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

class CorrelacionCofeneticaTest {

    @Test
    void esUnoCuandoElArbolReproduceLaMatrizOriginal() {
        // Matriz ultramétrica: el árbol la reproduce exactamente.
        double[][] u = {{0, 2, 6, 6}, {2, 0, 6, 6}, {6, 6, 0, 4}, {6, 6, 4, 0}};
        MatrizDistancias m = new MatrizDistancias(List.of("w", "x", "y", "z"), u);
        Dendrograma dendrograma = new ClusteringJerarquico(new AverageLinkage()).agrupar(m);
        assertEquals(1.0, CorrelacionCofenetica.calcular(u, dendrograma.matrizCofenetica()), 1e-9);
    }

    @Test
    void estaEntreMenosUnoYUno() {
        double[][] d = {{0, 1, 5, 6}, {1, 0, 4, 5}, {5, 4, 0, 1}, {6, 5, 1, 0}};
        MatrizDistancias m = new MatrizDistancias(List.of("a", "b", "c", "d"), d);
        Dendrograma dendrograma = new ClusteringJerarquico(new AverageLinkage()).agrupar(m);
        double r = CorrelacionCofenetica.calcular(d, dendrograma.matrizCofenetica());
        assertTrue(r <= 1.0 && r >= -1.0);
    }
}
