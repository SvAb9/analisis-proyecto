package co.edu.uniquindio.bibliometria.corpus;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

/** Lee el texto de las primeras páginas de un PDF. */
@Component
public class PdfExtractor {

    private static final int PAGINAS = 2;

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
