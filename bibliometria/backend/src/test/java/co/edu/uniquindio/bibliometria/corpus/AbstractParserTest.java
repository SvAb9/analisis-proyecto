package co.edu.uniquindio.bibliometria.corpus;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Pruebas de AbstractParser. */
class AbstractParserTest {

    private final AbstractParser parser = new AbstractParser();

    @Test
    void elsevierTerminaEnElCierreDelParrafo() {
        String texto = "Título\nA B S T R A C T   \nPrimera linea \nsegunda linea.   \nSince Alan Turing first\n";
        assertEquals("Primera linea segunda linea.", parser.extraerAbstract(texto));
    }

    @Test
    void encabezadoPegadoAlFinalDeLaLineaAnterior() {
        String texto = "nc-nd/4.0/).A B S T R A C T\nTexto del resumen.1. Introduction\nCuerpo\n";
        assertEquals("Texto del resumen.", parser.extraerAbstract(texto));
    }

    @Test
    void saltaLasPalabrasClaveDespuesDelEncabezado() {
        String largo = "The rapid expansion of Learning Analytics offers new scalable and data intensive systems";
        String texto = "A R T I C L E I N F O A B S T R A C T\nKeywords:\nAI in education\nEthics\n"
                + largo + "\n1. Introduction\n";
        assertEquals(largo, parser.extraerAbstract(texto));
    }

    @Test
    void terminaEnPalabrasClave() {
        String texto = "Abstract: Un resumen\ncorto.\nKeywords: uno; dos\n1. Introduction\n";
        assertEquals("Un resumen corto.", parser.extraerAbstract(texto));
    }

    @Test
    void terminaEnPalabrasClaveEspaciadasDeWiley() {
        String texto = "Abstract\nUn resumen.\nK E Y W O R D S\nuno\n";
        assertEquals("Un resumen.", parser.extraerAbstract(texto));
    }

    @Test
    void terminaEnIntroduccionDeWiley() {
        String texto = "Abstract\nUn resumen.\n1  | INTRODUC TION\nCuerpo\n";
        assertEquals("Un resumen.", parser.extraerAbstract(texto));
    }

    @Test
    void laPalabraIntroductionDentroDeUnaFraseNoCorta() {
        String texto = "Abstract\nDescribes the introduction\nof new tools.\nKeywords: x\n";
        assertEquals("Describes the introduction of new tools.", parser.extraerAbstract(texto));
    }

    @Test
    void quitaRotulosEstructurados() {
        String texto = "Abstract\nPurpose – El propósito.\nFindings – Los hallazgos.\nOriginality/value – El valor.\nKeywords x\n";
        assertEquals("El propósito. Los hallazgos. El valor.", parser.extraerAbstract(texto));
    }

    @Test
    void uneSilabasYConservaGuionesReales() {
        String texto = "Abstract\nresearchers and practi-\ntioners use instance- \nbased and AI-\ndriven tools.\nKeywords x\n";
        assertEquals("researchers and practitioners use instance-based and AI-driven tools.",
                parser.extraerAbstract(texto));
    }

    @Test
    void conservaElEspacioEnConstruccionesConAnd() {
        assertEquals("pre- and misconceptions of well-defined terms.",
                parser.extraerAbstract("Abstract\npre- and misconceptions of well- defined terms.\nKeywords x\n"));
    }

    @Test
    void convierteLigaduras() {
        assertEquals("scientific", parser.extraerAbstract("Abstract\nscientiﬁc\nKeywords x\n"));
    }

    @Test
    void sinEncabezadoDevuelveVacio() {
        assertEquals("", parser.extraerAbstract("Un texto sin resumen.\n1. Introduction\n"));
    }

    @Test
    void tituloUsaElArchivoSiElInternoEsUnCodigo() {
        assertEquals("AI for education a review", parser.titulo("ET-04-2023-0117_proof", "13. AI for education a review.pdf"));
        assertEquals("AI for education a review", parser.titulo(null, "13. AI for education a review.pdf"));
        assertEquals("Un título interno válido", parser.titulo(" Un título interno válido ", "1. otro.pdf"));
    }

    @Test
    void separaAutores() {
        assertEquals(List.of("Ana Uno", "Luis Dos", "Eva Tres"), parser.autores("Ana Uno; Luis Dos; Eva Tres"));
        assertEquals(List.of("Ana Uno", "Luis Dos", "Eva Tres"), parser.autores("Ana Uno, Luis Dos and Eva Tres"));
        assertEquals(List.of(), parser.autores(null));
    }
}
