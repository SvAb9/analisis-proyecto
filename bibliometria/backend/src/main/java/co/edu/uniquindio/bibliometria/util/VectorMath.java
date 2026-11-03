package co.edu.uniquindio.bibliometria.util;

/**
 * Producto punto, norma, coseno y distancia euclidiana.
 *
 * <p>Todas las operaciones recorren los vectores una sola vez: O(d) con d la dimensión.
 * Se implementan a mano (sin librerías de álgebra) para que el cálculo sea transparente.</p>
 */
public final class VectorMath {

    private VectorMath() {}

    /** a · b = Σ a_i b_i. O(d). */
    public static double productoPunto(double[] a, double[] b) {
        validarDimension(a, b);
        double suma = 0.0;
        for (int i = 0; i < a.length; i++) {
            suma += a[i] * b[i];
        }
        return suma;
    }

    /** ||a|| = sqrt(Σ a_i²). O(d). */
    public static double norma(double[] a) {
        double suma = 0.0;
        for (double x : a) {
            suma += x * x;
        }
        return Math.sqrt(suma);
    }

    /** cos θ = a·b / (||a|| ||b||); 0 si alguno de los vectores es nulo. O(d). */
    public static double coseno(double[] a, double[] b) {
        double normas = norma(a) * norma(b);
        if (normas == 0.0) {
            return 0.0;
        }
        return productoPunto(a, b) / normas;
    }

    /** d(a, b) = sqrt(Σ (a_i - b_i)²). O(d). */
    public static double distanciaEuclidiana(double[] a, double[] b) {
        validarDimension(a, b);
        double suma = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diferencia = a[i] - b[i];
            suma += diferencia * diferencia;
        }
        return Math.sqrt(suma);
    }

    /** Copia de a escalada a norma 1 (a / ||a||). O(d). */
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

    /** Ángulo en grados entre dos vectores a partir de su coseno. */
    public static double anguloGrados(double coseno) {
        double acotado = Math.max(-1.0, Math.min(1.0, coseno));
        return Math.toDegrees(Math.acos(acotado));
    }

    /**
     * Lleva el coseno de [-1, 1] a [0, 1] recortando los negativos a 0 (vectores opuestos u
     * ortogonales se consideran sin similitud). Es monótono, así que no altera el orden entre pares.
     */
    public static double cosenoASimilitud(double coseno) {
        return Math.max(0.0, Math.min(1.0, coseno));
    }

    private static void validarDimension(double[] a, double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Dimensiones distintas: " + a.length + " y " + b.length);
        }
    }
}
