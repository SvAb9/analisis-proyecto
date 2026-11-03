package co.edu.uniquindio.bibliometria.util;

/** Operaciones con vectores: producto punto, norma, coseno y distancia. */
public final class VectorMath {

    private VectorMath() {}

    public static double productoPunto(double[] a, double[] b) {
        validarDimension(a, b);
        double suma = 0.0;
        for (int i = 0; i < a.length; i++) {
            suma += a[i] * b[i];
        }
        return suma;
    }

    public static double norma(double[] a) {
        double suma = 0.0;
        for (double x : a) {
            suma += x * x;
        }
        return Math.sqrt(suma);
    }

    public static double coseno(double[] a, double[] b) {
        double normas = norma(a) * norma(b);
        if (normas == 0.0) {
            return 0.0;
        }
        return productoPunto(a, b) / normas;
    }

    public static double distanciaEuclidiana(double[] a, double[] b) {
        validarDimension(a, b);
        double suma = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diferencia = a[i] - b[i];
            suma += diferencia * diferencia;
        }
        return Math.sqrt(suma);
    }

    public static double[] normalizar(double[] a) {
        double n = norma(a);
        double[] salida = new double[a.length];
        if (n == 0.0) {
            return salida;
        }
        for (int i = 0; i < a.length; i++) {
            salida[i] = a[i] / n;
        }
        return salida;
    }

    public static double anguloGrados(double coseno) {
        double acotado = Math.max(-1.0, Math.min(1.0, coseno));
        return Math.toDegrees(Math.acos(acotado));
    }

    public static double cosenoASimilitud(double coseno) {
        return Math.max(0.0, Math.min(1.0, coseno));
    }

    private static void validarDimension(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Dimensiones distintas: " + a.length + " y " + b.length);
        }
    }
}
