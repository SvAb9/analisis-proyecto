package co.edu.uniquindio.bibliometria.corpus;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Aísla abstract, título y autores del texto extraído.
 *
 * <p>Las revistas del corpus (Elsevier, Springer, MDPI, Emerald, Wiley) maquetan distinto, por eso el
 * abstract se delimita con reglas:</p>
 * <ul>
 *   <li>Inicio: el encabezado "A B S T R A C T" (Elsevier) o "Abstract" / "Abstract:" al comienzo de línea.</li>
 *   <li>Fin, lo primero que aparezca: una línea "Keywords", "Key words" o "K E Y W O R D S"; el título de
 *       la introducción ("1. Introduction", "Introduction", "1 | INTRODUC TION"), aunque venga pegado a
 *       la última frase; o, solo en Elsevier, la línea que cierra el párrafo (punto y tres o más espacios).</li>
 *   <li>Si justo después del encabezado viene el bloque de palabras clave (Elsevier a dos columnas), se salta.</li>
 * </ul>
 * <p>Se quitan los rótulos de los abstracts estructurados de Emerald ("Purpose –", "Findings –") y se unen
 * las palabras partidas en sílabas al final de línea ("practi-" + "tioners"). Los guiones reales se
 * conservan sin espacio ("instance- based" pasa a "instance-based"), salvo en construcciones como
 * "pre- and misconceptions".</p>
 */
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

    /** Abstract en un solo párrafo, o cadena vacía si el texto no tiene encabezado de abstract. */
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
        // Bloque de palabras clave pegado al encabezado (Elsevier a dos columnas)
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

    /**
     * Título interno del PDF; si falta o es un código del editor (por ejemplo "ET-04-2023-0117_proof"),
     * el nombre del archivo sin el número inicial ni la extensión.
     */
    public String titulo(String tituloInterno, String nombreArchivo) {
        if (tituloInterno != null) {
            String t = tituloInterno.trim();
            if (t.length() > LARGO_MINIMO_TITULO && !t.contains("_") && !t.matches(".*\\.\\w{2,4}$")) {
                return t;
            }
        }
        return nombreArchivo.replaceFirst("^\\d+\\.\\s*", "").replaceFirst("(?i)\\.pdf$", "").trim();
    }

    /** Separa una lista de autores escrita con ";", "," o " and "; vacía si no hay autores. */
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

    /** Une las líneas en un párrafo y convierte las ligaduras de los PDF ("ﬁ" pasa a "fi"). */
    private String unirLineas(List<String> lineas) {
        StringBuilder sb = new StringBuilder();
        boolean anteriorPartida = false; // la línea anterior terminó en "letra minúscula + -" sin espacio
        for (String cruda : lineas) {
            String linea = ROTULO_ESTRUCTURADO.matcher(cruda.trim()).replaceFirst("");
            if (linea.isEmpty()) {
                continue;
            }
            if (anteriorPartida && Character.isLowerCase(linea.charAt(0))) {
                sb.setLength(sb.length() - 1); // "practi-" + "tioners" -> "practitioners"
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
