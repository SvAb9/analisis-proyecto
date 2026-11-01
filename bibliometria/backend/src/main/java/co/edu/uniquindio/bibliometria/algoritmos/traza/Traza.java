package co.edu.uniquindio.bibliometria.algoritmos.traza;

import java.util.ArrayList;
import java.util.List;

/**
 * Registro de pasos intermedios. Los algoritmos reciben una Traza y llaman a registrar(...).
 * Se usa Traza.activada() solo en los casos de estudio; en ejecuciones normales y en
 * mediciones de rendimiento se pasa Traza.desactivada(), que no guarda nada.
 */
public final class Traza {

    private static final Traza DESACTIVADA = new Traza(false);

    private final boolean activa;
    private final List<Paso> pasos = new ArrayList<>();

    private Traza(boolean activa) {
        this.activa = activa;
    }

    public static Traza activada() {
        return new Traza(true);
    }

    public static Traza desactivada() {
        return DESACTIVADA;
    }

    public boolean estaActiva() {
        return activa;
    }

    /** No hace nada si la traza está desactivada. */
    public void registrar(String descripcion, String datos) {
        if (!activa) {
            return;
        }
        pasos.add(new Paso(pasos.size() + 1, descripcion, datos));
    }

    public List<Paso> pasos() {
        return List.copyOf(pasos);
    }
}
