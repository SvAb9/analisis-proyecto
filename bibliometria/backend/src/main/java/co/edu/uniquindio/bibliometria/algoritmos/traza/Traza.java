package co.edu.uniquindio.bibliometria.algoritmos.traza;

import java.util.ArrayList;
import java.util.List;

/** Registra los pasos de un algoritmo cuando está activa. */
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
