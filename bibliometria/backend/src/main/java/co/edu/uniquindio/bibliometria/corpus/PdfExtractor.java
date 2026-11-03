package co.edu.uniquindio.bibliometria.corpus;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Lee el texto del PDF (única parte donde se permite librería externa).
 * Solo convierte las dos primeras páginas: en los 20 artículos el abstract termina antes del final de la 2.
 */
@Component
public class PdfExtractor {

    private static final int PAGINAS = 2;

    /** Texto de las primeras páginas y metadatos internos del PDF (pueden venir en null). */
    public record ContenidoPdf(String texto, String tituloInterno, String autoresInternos) {
    }

    public ContenidoPdf extraer(Path pdf) throws IOException {
        try (PDDocument documento = Loader.loadPDF(pdf.toFile())) {
            PDFTextStripper lector = new PDFTextStripper();
            lector.setStartPage(1);
            lector.setEndPage(Math.min(PAGINAS, documento.getNumberOfPages()));
            PDDocumentInformation info = documento.getDocumentInformation();
            return new ContenidoPdf(lector.getText(documento), info.getTitle(), info.getAuthor());
        }
    }
}
