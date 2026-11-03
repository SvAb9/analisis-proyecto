package co.edu.uniquindio.bibliometria.algoritmos.clustering.evaluacion;

/**
 * Correlación cofenética: correlación de Pearson entre las distancias originales y las
 * distancias cofenéticas del árbol (altura donde se juntan dos artículos), sobre todos los pares.
 * Cerca de 1 = el árbol representa fielmente las distancias originales.
 */
public final class CorrelacionCofenetica {

    private CorrelacionCofenetica() {
    }

    public static double calcular(double[][] original, double[][] cofenetica) {
        int n = original.length;
        double sumaX = 0.0;
        double sumaY = 0.0;
        int pares = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                sumaX += original[i][j];
                sumaY += cofenetica[i][j];
                pares++;
            }
        }
        if (pares == 0) {
            throw new IllegalArgumentException("Se necesitan al menos 2 artículos");
        }
        double mediaX = sumaX / pares;
        double mediaY = sumaY / pares;

        double covarianza = 0.0;
        double varX = 0.0;
        double varY = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dx = original[i][j] - mediaX;
                double dy = cofenetica[i][j] - mediaY;
                covarianza += dx * dy;
                varX += dx * dx;
                varY += dy * dy;
            }
        }
        if (varX == 0.0 || varY == 0.0) {
            return 0.0;
        }
        return covarianza / Math.sqrt(varX * varY);
    }
}
