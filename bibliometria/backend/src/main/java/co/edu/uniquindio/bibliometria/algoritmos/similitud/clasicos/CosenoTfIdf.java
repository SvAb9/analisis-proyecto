package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.PreparadorTexto;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ResultadoSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.SimilitudStrategy;
import co.edu.uniquindio.bibliometria.algoritmos.traza.Traza;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import co.edu.uniquindio.bibliometria.util.VectorMath;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import co.edu.uniquindio.bibliometria.util.estructuras.Nodo;

import java.util.List;
import java.util.Locale;

/** Similitud del coseno entre vectores TF-IDF. */
public class CosenoTfIdf implements SimilitudStrategy {

    private final RepositorioCorpus corpus;
    private volatile TfIdfVectorizador vectorizadorCorpus;

    public CosenoTfIdf() {
        this.corpus = null;
    }

    public CosenoTfIdf(RepositorioCorpus corpus) {
        this.corpus = corpus;
    }

    public CosenoTfIdf(TfIdfVectorizador vectorizadorCorpus) {
        this.corpus = null;
        this.vectorizadorCorpus = vectorizadorCorpus;
    }

    @Override
    public String nombre() {
        return "CosenoTfIdf";
    }

    @Override
    public ResultadoSimilitud comparar(String textoA, String textoB, Traza traza) {
        long inicio = System.nanoTime();
        String[] tokensA = PreparadorTexto.tokensContenido(textoA);
        String[] tokensB = PreparadorTexto.tokensContenido(textoB);
        TfIdfVectorizador vectorizador = vectorizadorDelCorpus();
        if (vectorizador == null) {
            vectorizador = new TfIdfVectorizador();
            vectorizador.agregarDocumento(tokensA);
            vectorizador.agregarDocumento(tokensB);
        }
        ListaEnlazada<TfIdfVectorizador.Peso> vectorA = vectorizador.vectorizar(tokensA);
        ListaEnlazada<TfIdfVectorizador.Peso> vectorB = vectorizador.vectorizar(tokensB);
        boolean conTraza = traza != null && traza.estaActiva();
        if (conTraza) {
            traza.registrar("N = " + vectorizador.getNumeroDocumentos(), "documentos usados para el IDF");
        }

        double productoPunto = productoPunto(vectorA, vectorB, conTraza ? traza : null);
        double normaA = norma(vectorA);
        double normaB = norma(vectorB);
        double coseno = normaA == 0 || normaB == 0 ? 0.0 : productoPunto / (normaA * normaB);
        long tiempo = System.nanoTime() - inicio;

        if (conTraza) {
            traza.registrar("A·B = " + formato(productoPunto), "suma de w_A(t)·w_B(t) sobre los términos comunes");
            traza.registrar("||A|| = " + formato(normaA), "raíz de la suma de w_A(t)² (" + vectorA.getCantidad() + " términos)");
            traza.registrar("||B|| = " + formato(normaB), "raíz de la suma de w_B(t)² (" + vectorB.getCantidad() + " términos)");
            traza.registrar("cos θ = " + formato(coseno),
                    formato(productoPunto) + " / (" + formato(normaA) + " · " + formato(normaB) + ")");
            traza.registrar("θ = " + String.format(Locale.ROOT, "%.2f", VectorMath.anguloGrados(coseno)) + "°",
                    "arccos(cos θ)");
        }
        return new ResultadoSimilitud(nombre(), VectorMath.cosenoASimilitud(coseno), coseno, tiempo,
                conTraza ? traza.pasos() : List.of());
    }

    public TfIdfVectorizador vectorizadorDelCorpus() {
        if (vectorizadorCorpus == null && corpus != null) {
            synchronized (this) {
                if (vectorizadorCorpus == null) {
                    ListaEnlazada<String> textos = new ListaEnlazada<>();
                    for (Articulo articulo : corpus.todos()) {
                        textos.insertarFinal(articulo.textoParaAlgoritmos());
                    }
                    if (textos.getCantidad() > 0) {
                        vectorizadorCorpus = TfIdfVectorizador.ajustar(textos);
                    }
                }
            }
        }
        return vectorizadorCorpus;
    }

    public static double productoPunto(ListaEnlazada<TfIdfVectorizador.Peso> a, ListaEnlazada<TfIdfVectorizador.Peso> b,
                                       Traza traza) {
        double suma = 0.0;
        Nodo<TfIdfVectorizador.Peso> p = a.getCabecera().getSiguiente();
        Nodo<TfIdfVectorizador.Peso> q = b.getCabecera().getSiguiente();
        while (p != null && q != null) {
            TfIdfVectorizador.Peso pa = p.getValor();
            TfIdfVectorizador.Peso pb = q.getValor();
            int comparacion = pa.termino().compareTo(pb.termino());
            if (comparacion == 0) {
                double aporte = pa.peso() * pb.peso();
                suma += aporte;
                if (traza != null && traza.estaActiva()) {
                    traza.registrar("w(" + pa.termino() + "): " + formato(pa.peso()) + " · " + formato(pb.peso())
                                    + " = " + formato(aporte),
                            "fA=" + pa.frecuencia() + ", fB=" + pb.frecuencia() + ", idf=" + formato(pa.idf()));
                }
                p = p.getSiguiente();
                q = q.getSiguiente();
            } else if (comparacion < 0) {
                p = p.getSiguiente();
            } else {
                q = q.getSiguiente();
            }
        }
        return suma;
    }

    public static double norma(ListaEnlazada<TfIdfVectorizador.Peso> vector) {
        double suma = 0.0;
        for (TfIdfVectorizador.Peso peso : vector) {
            suma += peso.peso() * peso.peso();
        }
        return Math.sqrt(suma);
    }

    private static String formato(double valor) {
        return String.format(Locale.ROOT, "%.4f", valor);
    }
}
