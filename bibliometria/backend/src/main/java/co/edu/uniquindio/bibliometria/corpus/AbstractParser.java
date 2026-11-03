package co.edu.uniquindio.bibliometria.corpus;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Aísla el abstract, el título y los autores del texto de un PDF. */
@Component
public class AbstractParser {

    private static final Pattern INICIO_ELSEVIER = Pattern.compile("A B S T R A C T");
    private static final Pattern INICIO_GENERAL = Pattern.compile("^\\s*Abstract\\b:?\\s*(.*)$");
    private static final Pattern PALABRAS_CLAVE = Pattern.compile("^\\s*(Keywords|Key words|K E Y W O R D S)\\b.*");
    private static final Pattern INTRODUCCION =
            Pattern.compile("(?i)(^|\\.)\\s*(\\d+\\s*\\.?\\s*\\|?\\s*)?introduc ?tion\\s*$");
    private static final Pattern FIN_PARRAFO_ELSEVIER = Pattern.compile(".*[.!?)]\\s{3,}$");
    private static final Pattern ROTULO_ESTRUCTURADO = Pattern.compile(
            "^(Purpose|Design/methodology/approach|Findings|Originality/value|Research limitations/implications"
                    + "|Practical implications|Social implications)\\s*[–-]\\s*");
    private static final Pattern ESPACIO_TRAS_GUION = Pattern.compile("(?<=\\p{L})- (?!and\\b|or\\b)(?=\\p{L})");
    private static final int LARGO_MINIMO_LINEA_ABSTRACT = 60;
    private static final int LARGO_MINIMO_TITULO = 15;

    public String extraerAbstract(String texto) {
        String[] lineas = texto.split("\\r?\\n");
        int i = 0;
        boolean elsevier = false;
        String primera = null;
        for (; i < lineas.length; i++) {
            Matcher e = INICIO_ELSEVIER.matcher(lineas[i]);
            if (e.find()) {
                elsevier = true;
                primera = lineas[i].substring(e.end());
                break;
            }
            Matcher g = INICIO_GENERAL.matcher(lineas[i]);
            if (g.matches()) {
                primera = g.group(1);
                break;
            }
        }
        if (primera == null) {
            return "";
        }

        List<String> cuerpo = new ArrayList<>();
        if (!primera.isBlank()) {
            cuerpo.add(primera);
        }
        i++;
        if (cuerpo.isEmpty() && i < lineas.length && PALABRAS_CLAVE.matcher(lineas[i]).matches()) {
            i++;
            while (i < lineas.length && lineas[i].trim().length() < LARGO_MINIMO_LINEA_ABSTRACT) {
                i++;
            }
        }
        for (; i < lineas.length; i++) {
            String linea = lineas[i];
            if (PALABRAS_CLAVE.matcher(linea).matches()) {
                break;
            }
            Matcher intro = INTRODUCCION.matcher(linea);
            if (intro.find()) {
                String antes = linea.substring(0, intro.start()) + (intro.group(1).isEmpty() ? "" : ".");
                if (!antes.isBlank()) {
                    cuerpo.add(antes);
                }
                break;
            }
            cuerpo.add(linea);
            if (elsevier && FIN_PARRAFO_ELSEVIER.matcher(linea).matches()) {
                break;
            }
        }
        return unirLineas(cuerpo);
    }

    public String titulo(String tituloInterno, String nombreArchivo) {
        if (tituloInterno != null) {
            String t = tituloInterno.trim();
            if (t.length() > LARGO_MINIMO_TITULO && !t.contains("_") && !t.matches(".*\\.\\w{2,4}$")) {
                return t;
            }
        }
        return nombreArchivo.replaceFirst("^\\d+\\.\\s*", "").replaceFirst("(?i)\\.pdf$", "").trim();
    }

    public List<String> autores(String autoresInternos) {
        List<String> resultado = new ArrayList<>();
        if (autoresInternos == null) {
            return resultado;
        }
        for (String autor : autoresInternos.split(";|,|\\sand\\s")) {
            if (!autor.isBlank()) {
                resultado.add(autor.trim());
            }
        }
        return resultado;
    }

    private String unirLineas(List<String> lineas) {
        StringBuilder sb = new StringBuilder();
        boolean anteriorPartida = false;
        for (String cruda : lineas) {
            String linea = ROTULO_ESTRUCTURADO.matcher(cruda.trim()).replaceFirst("");
            if (linea.isEmpty()) {
                continue;
            }
            if (anteriorPartida && Character.isLowerCase(linea.charAt(0))) {
                sb.setLength(sb.length() - 1);
            } else if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(linea);
            int fin = cruda.length();
            anteriorPartida = fin >= 2 && cruda.charAt(fin - 1) == '-' && Character.isLowerCase(cruda.charAt(fin - 2));
        }
        String parrafo = Normalizer.normalize(sb.toString(), Normalizer.Form.NFKC).replaceAll("\\s+", " ").trim();
        return ESPACIO_TRAS_GUION.matcher(parrafo).replaceAll("-");
    }
}
