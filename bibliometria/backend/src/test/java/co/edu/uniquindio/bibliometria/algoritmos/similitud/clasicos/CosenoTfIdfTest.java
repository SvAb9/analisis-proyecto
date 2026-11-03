package co.edu.uniquindio.bibliometria.algoritmos.similitud.clasicos;

import co.edu.uniquindio.bibliometria.corpus.Articulo;
import co.edu.uniquindio.bibliometria.corpus.RepositorioCorpus;
import co.edu.uniquindio.bibliometria.util.estructuras.ListaEnlazada;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CosenoTfIdfTest {

    @Test
    void idfSuavizado() {
        TfIdfVectorizador v = TfIdfVectorizador.ajustar(List.of("learning analytics", "learning assessment", "chatbot"));
        assertEquals(3, v.getNumeroDocumentos());
        assertEquals(2, v.frecuenciaDocumental("learning"));
        assertEquals(Math.log(4.0 / 3.0) + 1, v.idf("learning"), 1e-12);
        assertEquals(Math.log(4.0 / 2.0) + 1, v.idf("chatbot"), 1e-12);
        assertEquals(Math.log(4.0 / 1.0) + 1, v.idf("inexistente"), 1e-12);
    }

    @Test
    void pesosTfIdfOrdenados() {
        TfIdfVectorizador v = TfIdfVectorizador.ajustar(List.of("learning analytics", "learning assessment"));
        ListaEnlazada<TfIdfVectorizador.Peso> vector = v.vectorizar(new String[] {"learning", "analytics", "learning"});
        assertEquals("analytics", vector.obtenerValor(0).termino());
        TfIdfVectorizador.Peso learning = vector.obtenerValor(1);
        assertEquals(2, learning.frecuencia());
        assertEquals(2.0 / 3.0 * (Math.log(3.0 / 3.0) + 1), learning.peso(), 1e-12);
    }

    @Test
    void cosenoCalculadoAMano() {
        double idfUnico = Math.log(1.5) + 1;
        double wComun = 0.5 * 1.0;
        double wUnico = 0.5 * idfUnico;
        double esperado = (wComun * wComun) / (wComun * wComun + wUnico * wUnico);
        double obtenido = new CosenoTfIdf().comparar("learning analytics", "learning assessment").valorCrudo();
        assertEquals(esperado, obtenido, 1e-12);
    }

    @Test
    void igualesYDisjuntos() {
        CosenoTfIdf coseno = new CosenoTfIdf();
        assertEquals(1.0, coseno.comparar("generative ai assessment", "assessment generative ai").similitud(), 1e-12);
        assertEquals(0.0, coseno.comparar("generative assessment", "simulation agents").similitud(), 1e-12);
    }

    @Test
    void conCorpusLosTerminosComunesPesanMenos() {
        TfIdfVectorizador corpus = TfIdfVectorizador.ajustar(List.of(
                "education research", "education review", "education learning", "plagiarism chatbot"));
        double conCorpus = new CosenoTfIdf(corpus).comparar("education plagiarism", "education review").similitud();
        double sinCorpus = new CosenoTfIdf().comparar("education plagiarism", "education review").similitud();
        assertTrue(conCorpus < sinCorpus, conCorpus + " debería ser menor que " + sinCorpus);
    }

    @Test
    void recibeElCorpusPorConstructor() {
        List<Articulo> articulos = List.of(
                new Articulo("1", "t", List.of(), "education research", null),
                new Articulo("2", "t", List.of(), "education review", null),
                new Articulo("3", "t", List.of(), "crudo ignorado", "education learning"),
                new Articulo("4", "t", List.of(), "plagiarism chatbot", null));
        RepositorioCorpus repo = new RepositorioCorpus() {
            public List<Articulo> todos() { return articulos; }
            public Optional<Articulo> porId(String id) { return Optional.empty(); }
        };
        CosenoTfIdf coseno = new CosenoTfIdf(repo);
        assertEquals(new CosenoTfIdf(TfIdfVectorizador.ajustar(List.of(
                "education research", "education review", "education learning", "plagiarism chatbot")))
                .comparar("education plagiarism", "education review").similitud(),
                coseno.comparar("education plagiarism", "education review").similitud(), 1e-12);
        assertEquals(4, coseno.vectorizadorDelCorpus().getNumeroDocumentos());
        assertEquals(0, coseno.vectorizadorDelCorpus().frecuenciaDocumental("crudo"), "usa el abstract procesado");
    }
}
