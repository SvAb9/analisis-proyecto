package co.edu.uniquindio.bibliometria.algoritmos.similitud;

import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.CosenoTfIdf;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.JaccardSimilitud;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.Levenshtein;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.NeedlemanWunsch;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos.TfIdfVectorizador;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.EmbeddingProvider;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.SimilitudEmbeddingsOracion;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.SimilitudEmbeddingsPalabras;
import co.edu.uniquindio.bibliometria.algoritmos.similitud.ia.SimilitudEmbeddings;
import co.edu.uniquindio.bibliometria.corpus.AbstractParser;
import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.CorpusLoader;
import co.edu.uniquindio.bibliometria.corpus.PdfExtractor;
import co.edu.uniquindio.bibliometria.corpus.preprocess.PreprocesadorTexto;
import co.edu.uniquindio.bibliometria.service.SimilitudService;
import co.edu.uniquindio.bibliometria.infra.embeddings.EmbeddingCache;
import co.edu.uniquindio.bibliometria.infra.embeddings.ModeloOracionProvider;
import co.edu.uniquindio.bibliometria.infra.embeddings.Word2VecProvider;
import co.edu.uniquindio.bibliometria.util.VectorMath;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Genera los datos del caso de estudio de R1: mvn test -Dtest=CasoEstudioR1 -Dcaso.estudio=true */
class CasoEstudioR1 {

    private static final int ID_A = 11;
    private static final int ID_B = 16;
    private static final Path SALIDA = Path.of("../docs/casos-estudio");

    private static final Map<String, String> ID = Map.of("Levenshtein", "levenshtein", "NeedlemanWunsch", "needleman-wunsch",
            "Jaccard", "jaccard", "CosenoTfIdf", "coseno-tfidf", "EmbeddingsOracion", "embeddings-oracion",
            "EmbeddingsPalabras", "embeddings-palabras");

    private static String tipo(SimilitudStrategy e) {
        return SimilitudService.esIA(e) ? "IA" : "CLASICO";
    }

    private final StringBuilder md = new StringBuilder();
    private final Map<String, Object> json = new LinkedHashMap<>();

    @Test
    void generar() throws Exception {
        assumeTrue(Boolean.getBoolean("caso.estudio"), "solo con -Dcaso.estudio=true");
        List<Articulo> articulos = new CorpusLoader(Path.of("data/corpus"), new PdfExtractor(), new AbstractParser(),
                new PreprocesadorTexto()).cargar();
        List<String> textos = new ArrayList<>();
        for (Articulo articulo : articulos) {
            textos.add(articulo.textoParaAlgoritmos());
        }
        Articulo articuloA = articulos.get(ID_A - 1);
        Articulo articuloB = articulos.get(ID_B - 1);
        String a = articuloA.textoParaAlgoritmos();
        String b = articuloB.textoParaAlgoritmos();

        ModeloOracionProvider minilmBase = new ModeloOracionProvider(Path.of("data/modelos/all-MiniLM-L6-v2"));
        Word2VecProvider gloveBase = new Word2VecProvider(Path.of("data/modelos/glove/glove.6B.100d.corpus.txt"));
        assumeTrue(minilmBase.disponible() && gloveBase.disponible(), "faltan modelos");
        EmbeddingCache minilm = new EmbeddingCache(minilmBase);
        EmbeddingCache glove = new EmbeddingCache(gloveBase);
        TfIdfVectorizador corpus = TfIdfVectorizador.ajustar(textos);

        List<SimilitudStrategy> estrategias = List.of(new Levenshtein(), new NeedlemanWunsch(), new JaccardSimilitud(),
                new CosenoTfIdf(corpus), new SimilitudEmbeddingsOracion(minilm), new SimilitudEmbeddingsPalabras(glove));

        md.append("# Caso de estudio R1: similitud textual\n\n");
        md.append("Generado por `CasoEstudioR1` con el código del proyecto. Abstract A = artículo ").append(ID_A)
                .append(", abstract B = artículo ").append(ID_B).append(".\n\n");
        md.append("- **A (").append(ID_A).append(")**: ").append(articuloA.titulo()).append("\n");
        md.append("- **B (").append(ID_B).append(")**: ").append(articuloB.titulo()).append("\n\n");
        json.put("idA", ID_A);
        json.put("idB", ID_B);
        json.put("tituloA", articuloA.titulo());
        json.put("tituloB", articuloB.titulo());
        json.put("abstractA", articuloA.abstractCrudo());
        json.put("abstractB", articuloB.abstractCrudo());

        String na = PreparadorTexto.normalizar(a);
        String nb = PreparadorTexto.normalizar(b);
        String[] ta = PreparadorTexto.tokensContenido(a);
        String[] tb = PreparadorTexto.tokensContenido(b);
        md.append("## Preparación\n\n");
        md.append("| | A | B |\n|---|---|---|\n");
        md.append("| caracteres normalizados | ").append(na.length()).append(" | ").append(nb.length()).append(" |\n");
        md.append("| tokens de contenido | ").append(ta.length).append(" | ").append(tb.length).append(" |\n\n");
        json.put("caracteresA", na.length());
        json.put("caracteresB", nb.length());
        json.put("tokensA", ta.length);
        json.put("tokensB", tb.length);
        json.put("primerosTokensA", primeros(ta, 12));
        json.put("primerosTokensB", primeros(tb, 12));

        levenshtein(na, nb);
        needleman(ta, tb);
        jaccard(a, b);
        coseno(a, b, corpus);
        ModeloOracionProvider.Detalle oa = minilmBase.vectorizarConDetalle(articuloA.abstractCrudo());
        ModeloOracionProvider.Detalle ob = minilmBase.vectorizarConDetalle(articuloB.abstractCrudo());
        embeddings("embeddings-oracion", "Embeddings de oración (all-MiniLM-L6-v2)", oa.vector(),
                oa.tokens() + " tokens WordPiece en " + oa.ventanas() + " ventana(s), mean pooling", ob.vector(),
                ob.tokens() + " tokens WordPiece en " + ob.ventanas() + " ventana(s), mean pooling", minilmBase.modelo());
        Word2VecProvider.Detalle pa = gloveBase.vectorizarConDetalle(a);
        Word2VecProvider.Detalle pb = gloveBase.vectorizarConDetalle(b);
        embeddings("embeddings-palabras", "Embeddings de palabras promediados (GloVe)", pa.vector(),
                "promedio de " + pa.palabrasConVector() + " de " + pa.palabras() + " palabras con vector", pb.vector(),
                "promedio de " + pb.palabrasConVector() + " de " + pb.palabras() + " palabras con vector", gloveBase.modelo());
        palabrasCercanas(gloveBase);
        resumen(estrategias, articulos);

        Files.createDirectories(SALIDA);
        Files.writeString(SALIDA.resolve("r1-similitud.md"), md.toString());
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(SALIDA.resolve("r1-similitud.json").toFile(), json);
    }

    private void levenshtein(String na, String nb) {
        String x = "generative";
        String y = "emergence";
        int[][] d = Levenshtein.matriz(x, y, null);
        md.append("## Levenshtein\n\nMatriz D para las palabras iniciales \"").append(x).append("\" (A) y \"").append(y)
                .append("\" (B):\n\n");
        md.append(tablaMatriz(d, x.split(""), y.split(""))).append("\n");
        md.append("D[").append(x.length()).append("][").append(y.length()).append("] = ").append(d[x.length()][y.length()])
                .append("\n\n");
        json.put("levMatriz", d);
        json.put("levX", x);
        json.put("levY", y);
        ResultadoSimilitud r = new Levenshtein().comparar(na, nb);
        md.append("Abstracts completos: distancia = ").append((int) r.valorCrudo()).append(", similitud = 1 - ")
                .append((int) r.valorCrudo()).append(" / ").append(Math.max(na.length(), nb.length())).append(" = ")
                .append(f(r.similitud())).append("\n\n");
        json.put("levDistancia", (int) r.valorCrudo());
        json.put("levSimilitud", r.similitud());
    }

    private void needleman(String[] ta, String[] tb) {
        String[] x = primeros(ta, 6);
        String[] y = primeros(tb, 6);
        NeedlemanWunsch.Alineamiento al = NeedlemanWunsch.alinear(x, y, null);
        md.append("## Needleman-Wunsch\n\nMatriz F para los 6 primeros tokens de contenido de cada abstract:\n\n");
        md.append(tablaMatriz(al.matriz(), x, y)).append("\n");
        StringBuilder fila1 = new StringBuilder();
        StringBuilder fila2 = new StringBuilder();
        List<String[]> pares = new ArrayList<>();
        for (NeedlemanWunsch.ParAlineado p : al.pares()) {
            fila1.append(String.format("%-14s", p.a()));
            fila2.append(String.format("%-14s", p.b()));
            pares.add(new String[] {p.a(), p.b()});
        }
        md.append("```\n").append(fila1).append("\n").append(fila2).append("\n```\n\n");
        md.append("Puntaje = ").append(al.puntaje()).append(", coincidencias = ").append(al.coincidencias()).append(" de ")
                .append(al.pares().getCantidad()).append("\n\n");
        json.put("nwMatriz", al.matriz());
        json.put("nwX", x);
        json.put("nwY", y);
        json.put("nwPares", pares);
        json.put("nwPuntajeEjemplo", al.puntaje());
        NeedlemanWunsch.Alineamiento total = NeedlemanWunsch.alinear(ta, tb, null);
        double sim = (double) total.coincidencias() / total.pares().getCantidad();
        md.append("Abstracts completos: puntaje F[n][m] = ").append(total.puntaje()).append(", coincidencias = ")
                .append(total.coincidencias()).append(", longitud = ").append(total.pares().getCantidad())
                .append(", similitud = ").append(f(sim)).append("\n\n");
        List<String> coincidencias = new ArrayList<>();
        for (NeedlemanWunsch.ParAlineado p : total.pares()) {
            if (p.coincide()) {
                coincidencias.add(p.a());
            }
        }
        md.append("Tokens alineados como coincidencia: ").append(coincidencias).append("\n\n");
        json.put("nwPuntaje", total.puntaje());
        json.put("nwCoincidencias", total.coincidencias());
        json.put("nwLongitud", total.pares().getCantidad());
        json.put("nwSimilitud", sim);
        json.put("nwTokensCoincidentes", coincidencias);
    }

    private void jaccard(String a, String b) {
        ListaEnlazada<String> ca = JaccardSimilitud.conjunto(PreparadorTexto.tokensContenido(a));
        ListaEnlazada<String> cb = JaccardSimilitud.conjunto(PreparadorTexto.tokensContenido(b));
        ListaEnlazada<String> comunes = JaccardSimilitud.interseccion(ca, cb);
        int union = ca.getCantidad() + cb.getCantidad() - comunes.getCantidad();
        double j = new JaccardSimilitud().comparar(a, b).similitud();
        Map<String, Object> pasos = new LinkedHashMap<>();
        pasos.put("|A|", paso(ca.getCantidad(), "términos únicos de A: " + ca));
        pasos.put("|B|", paso(cb.getCantidad(), "términos únicos de B: " + cb));
        pasos.put("|A ∩ B|", paso(comunes.getCantidad(), "términos comunes: " + comunes));
        pasos.put("|A ∪ B|", paso(union, ca.getCantidad() + " + " + cb.getCantidad() + " - " + comunes.getCantidad()));
        pasos.put("resultado", paso(j, comunes.getCantidad() + " / " + union));
        md.append("## Jaccard\n\n");
        for (Map.Entry<String, Object> e : pasos.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> v = (Map<String, Object>) e.getValue();
            md.append("- ").append(e.getKey()).append(" = ").append(v.get("valor")).append(" — ")
                    .append(v.get("descripcion")).append("\n");
        }
        md.append("\n");
        json.put("jaccardPasos", pasos);
        json.put("jaccardSimilitud", j);
    }

    private void coseno(String a, String b, TfIdfVectorizador corpus) {
        String[] ta = PreparadorTexto.tokensContenido(a);
        String[] tb = PreparadorTexto.tokensContenido(b);
        ListaEnlazada<TfIdfVectorizador.Peso> va = corpus.vectorizar(ta);
        ListaEnlazada<TfIdfVectorizador.Peso> vb = corpus.vectorizar(tb);
        md.append("## Coseno TF-IDF\n\nIDF sobre los N = ").append(corpus.getNumeroDocumentos())
                .append(" abstracts. Términos comunes (los únicos que aportan al producto punto):\n\n");
        md.append("| término | fA | tfA | fB | tfB | df | idf | wA | wB | wA·wB |\n|---|---|---|---|---|---|---|---|---|---|\n");
        List<Map<String, Object>> filas = new ArrayList<>();
        for (TfIdfVectorizador.Peso pa : va) {
            for (TfIdfVectorizador.Peso pb : vb) {
                if (pa.termino().equals(pb.termino())) {
                    md.append("| ").append(pa.termino()).append(" | ").append(pa.frecuencia()).append(" | ").append(f(pa.tf()))
                            .append(" | ").append(pb.frecuencia()).append(" | ").append(f(pb.tf())).append(" | ")
                            .append(corpus.frecuenciaDocumental(pa.termino())).append(" | ").append(f(pa.idf()))
                            .append(" | ").append(f(pa.peso())).append(" | ").append(f(pb.peso())).append(" | ")
                            .append(f(pa.peso() * pb.peso())).append(" |\n");
                    Map<String, Object> fila = new LinkedHashMap<>();
                    fila.put("termino", pa.termino());
                    fila.put("fA", pa.frecuencia());
                    fila.put("tfA", pa.tf());
                    fila.put("fB", pb.frecuencia());
                    fila.put("tfB", pb.tf());
                    fila.put("df", corpus.frecuenciaDocumental(pa.termino()));
                    fila.put("idf", pa.idf());
                    fila.put("wA", pa.peso());
                    fila.put("wB", pb.peso());
                    fila.put("producto", pa.peso() * pb.peso());
                    filas.add(fila);
                }
            }
        }
        double punto = CosenoTfIdf.productoPunto(va, vb, null);
        double na = CosenoTfIdf.norma(va);
        double nb = CosenoTfIdf.norma(vb);
        double cos = punto / (na * nb);
        Map<String, Double> finales = new LinkedHashMap<>();
        finales.put("N", (double) corpus.getNumeroDocumentos());
        finales.put("A·B", punto);
        finales.put("||A||", na);
        finales.put("||B||", nb);
        finales.put("cos θ", cos);
        finales.put("θ", VectorMath.anguloGrados(cos));
        md.append("\n");
        for (Map.Entry<String, Double> e : finales.entrySet()) {
            md.append("- ").append(e.getKey()).append(" = ").append(f(e.getValue())).append("\n");
        }
        double conEstrategia = new CosenoTfIdf(corpus).comparar(a, b).similitud();
        double sinCorpus = new CosenoTfIdf().comparar(a, b).similitud();
        md.append("\nCosenoTfIdf.comparar(A, B) = ").append(f(conEstrategia)).append("; sin corpus (IDF solo con A y B): ")
                .append(f(sinCorpus)).append("\n\n");
        json.put("cosenoTerminos", filas);
        json.put("cosenoTerminosA", va.getCantidad());
        json.put("cosenoTerminosB", vb.getCantidad());
        json.put("cosenoPasos", finales);
        json.put("cosenoSimilitud", conEstrategia);
        json.put("cosenoSinCorpus", sinCorpus);
    }

    private void embeddings(String id, String titulo, double[] va, String notaA, double[] vb, String notaB, String modelo) {
        double punto = VectorMath.productoPunto(va, vb);
        double na = VectorMath.norma(va);
        double nb = VectorMath.norma(vb);
        double cos = VectorMath.coseno(va, vb);
        double d = VectorMath.distanciaEuclidiana(va, vb);
        Map<String, Object> pasos = new LinkedHashMap<>();
        pasos.put("modelo", paso(null, modelo + ", dimensión " + va.length));
        pasos.put("A", paso(null, notaA + "; primeras componentes " + SimilitudEmbeddings.primeras(va)));
        pasos.put("B", paso(null, notaB + "; primeras componentes " + SimilitudEmbeddings.primeras(vb)));
        pasos.put("A·B", paso(punto, "suma de a_i·b_i sobre " + va.length + " componentes"));
        pasos.put("||A||", paso(na, "raíz de la suma de a_i²"));
        pasos.put("||B||", paso(nb, "raíz de la suma de b_i²"));
        pasos.put("cos θ", paso(cos, f(punto) + " / (" + f(na) + " · " + f(nb) + ")"));
        pasos.put("θ", paso(VectorMath.anguloGrados(cos), "arccos(cos θ) en grados"));
        pasos.put("d(A, B)", paso(d, "raíz de la suma de (a_i - b_i)²"));
        md.append("## ").append(titulo).append("\n\n");
        for (Map.Entry<String, Object> e : pasos.entrySet()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> v = (Map<String, Object>) e.getValue();
            md.append("- ").append(e.getKey()).append(v.get("valor") == null ? "" : " = " + v.get("valor")).append(" — ")
                    .append(v.get("descripcion")).append("\n");
        }
        md.append("\n");
        json.put(id + "Pasos", pasos);
        json.put(id + "Similitud", VectorMath.cosenoASimilitud(cos));
    }

    private static Map<String, Object> paso(Object valor, String descripcion) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("valor", valor);
        m.put("descripcion", descripcion);
        return m;
    }

    private void palabrasCercanas(Word2VecProvider glove) {
        String[][] pares = {{"assessment", "assessments"}, {"assessment", "evaluation"}, {"generative", "ai"},
                {"higher", "tertiary"}, {"students", "teachers"}, {"plagiarism", "cheating"}, {"integrity", "plagiarism"},
                {"assessment", "chatbot"}};
        md.append("## Coseno entre palabras en GloVe\n\n| palabra 1 | palabra 2 | coseno |\n|---|---|---|\n");
        List<Object[]> filas = new ArrayList<>();
        for (String[] p : pares) {
            double[] u = glove.vectorDe(p[0]);
            double[] v = glove.vectorDe(p[1]);
            if (u != null && v != null) {
                double c = VectorMath.coseno(u, v);
                md.append("| ").append(p[0]).append(" | ").append(p[1]).append(" | ").append(f(c)).append(" |\n");
                filas.add(new Object[] {p[0], p[1], c});
            }
        }
        md.append("\n");
        json.put("glovePalabras", filas);
    }

    private void resumen(List<SimilitudStrategy> estrategias, List<Articulo> articulos) {
        int n = articulos.size();
        md.append("## Resumen y contexto en el corpus\n\n");
        md.append("| algoritmo | tipo | sim(A,B) | media 190 pares | mín | máx | puesto de (A,B) | ms por par |\n");
        md.append("|---|---|---|---|---|---|---|---|\n");
        List<Map<String, Object>> filas = new ArrayList<>();
        Map<String, double[]> valoresPorAlgoritmo = new LinkedHashMap<>();
        for (SimilitudStrategy e : estrategias) {
            double[] valores = new double[n * (n - 1) / 2];
            int k = 0;
            long nanos = 0;
            double valorAB = 0;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    ResultadoSimilitud r = e.comparar(SimilitudService.textoPara(e, articulos.get(i)), SimilitudService.textoPara(e, articulos.get(j)));
                    nanos += r.tiempoNs();
                    valores[k++] = r.similitud();
                    if (i == ID_A - 1 && j == ID_B - 1) {
                        valorAB = r.similitud();
                    }
                }
            }
            double suma = 0;
            double min = 1;
            double max = 0;
            int puesto = 1;
            for (double v : valores) {
                suma += v;
                min = Math.min(min, v);
                max = Math.max(max, v);
                if (v > valorAB) {
                    puesto++;
                }
            }
            double media = suma / valores.length;
            double msPorPar = nanos / 1e6 / valores.length;
            md.append("| ").append(ID.get(e.nombre())).append(" | ").append(tipo(e)).append(" | ").append(f(valorAB)).append(" | ")
                    .append(f(media)).append(" | ").append(f(min)).append(" | ").append(f(max)).append(" | ")
                    .append(puesto).append(" de ").append(valores.length).append(" | ")
                    .append(String.format(Locale.ROOT, "%.3f", msPorPar)).append(" |\n");
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("id", ID.get(e.nombre()));
            fila.put("nombre", e.nombre());
            fila.put("tipo", tipo(e));
            fila.put("similitudAB", valorAB);
            fila.put("media", media);
            fila.put("min", min);
            fila.put("max", max);
            fila.put("puesto", puesto);
            fila.put("pares", valores.length);
            fila.put("msPorPar", msPorPar);
            filas.add(fila);
            valoresPorAlgoritmo.put(ID.get(e.nombre()), valores);
        }
        md.append("\nCorrelación de Spearman entre algoritmos sobre los 190 pares:\n\n");
        List<String> ids = new ArrayList<>(valoresPorAlgoritmo.keySet());
        md.append("| |");
        for (String id : ids) {
            md.append(" ").append(id).append(" |");
        }
        md.append("\n|---|");
        for (int i = 0; i < ids.size(); i++) {
            md.append("---|");
        }
        md.append("\n");
        double[][] spearman = new double[ids.size()][ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            md.append("| ").append(ids.get(i)).append(" |");
            for (int j = 0; j < ids.size(); j++) {
                spearman[i][j] = spearman(valoresPorAlgoritmo.get(ids.get(i)), valoresPorAlgoritmo.get(ids.get(j)));
                md.append(" ").append(String.format(Locale.ROOT, "%.2f", spearman[i][j])).append(" |");
            }
            md.append("\n");
        }
        json.put("resumen", filas);
        json.put("spearmanIds", ids);
        json.put("spearman", spearman);
    }

    private static double spearman(double[] x, double[] y) {
        double[] rx = rangos(x);
        double[] ry = rangos(y);
        double mx = 0;
        double my = 0;
        for (int i = 0; i < x.length; i++) {
            mx += rx[i];
            my += ry[i];
        }
        mx /= x.length;
        my /= y.length;
        double num = 0;
        double dx = 0;
        double dy = 0;
        for (int i = 0; i < x.length; i++) {
            num += (rx[i] - mx) * (ry[i] - my);
            dx += (rx[i] - mx) * (rx[i] - mx);
            dy += (ry[i] - my) * (ry[i] - my);
        }
        return num / Math.sqrt(dx * dy);
    }

    private static double[] rangos(double[] v) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            int menores = 0;
            int iguales = 0;
            for (double w : v) {
                if (w < v[i]) {
                    menores++;
                } else if (w == v[i]) {
                    iguales++;
                }
            }
            r[i] = menores + (iguales + 1) / 2.0;
        }
        return r;
    }

    private static String tablaMatriz(int[][] m, String[] filas, String[] columnas) {
        StringBuilder t = new StringBuilder("| | ε |");
        for (String c : columnas) {
            t.append(" ").append(c).append(" |");
        }
        t.append("\n|---|---|");
        for (int j = 0; j < columnas.length; j++) {
            t.append("---|");
        }
        t.append("\n");
        for (int i = 0; i < m.length; i++) {
            t.append("| ").append(i == 0 ? "ε" : filas[i - 1]).append(" |");
            for (int j = 0; j < m[i].length; j++) {
                t.append(" ").append(m[i][j]).append(" |");
            }
            t.append("\n");
        }
        return t.toString();
    }

    private static String[] primeros(String[] tokens, int k) {
        String[] salida = new String[Math.min(k, tokens.length)];
        System.arraycopy(tokens, 0, salida, 0, salida.length);
        return salida;
    }

    private static String f(double v) {
        return String.format(Locale.ROOT, "%.4f", v);
    }
}
