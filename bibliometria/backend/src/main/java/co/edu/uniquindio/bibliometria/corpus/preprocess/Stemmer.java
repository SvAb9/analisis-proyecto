package co.edu.uniquindio.bibliometria.corpus.preprocess;

/** Reduce las palabras a su raíz. */
public class Stemmer {

    private static final String[][] PASO_2 = {
            {"ational", "ate"}, {"tional", "tion"}, {"enci", "ence"}, {"anci", "ance"}, {"izer", "ize"},
            {"abli", "able"}, {"alli", "al"}, {"entli", "ent"}, {"eli", "e"}, {"ousli", "ous"},
            {"ization", "ize"}, {"ation", "ate"}, {"ator", "ate"}, {"alism", "al"}, {"iveness", "ive"},
            {"fulness", "ful"}, {"ousness", "ous"}, {"aliti", "al"}, {"iviti", "ive"}, {"biliti", "ble"}};

    private static final String[][] PASO_3 = {
            {"icate", "ic"}, {"ative", ""}, {"alize", "al"}, {"iciti", "ic"}, {"ical", "ic"},
            {"ful", ""}, {"ness", ""}};

    private static final String[] PASO_4 = {
            "al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "ion",
            "ou", "ism", "ate", "iti", "ous", "ive", "ize"};

    public String stem(String palabra) {
        if (palabra == null || palabra.length() <= 2) {
            return palabra;
        }
        StringBuilder w = new StringBuilder(palabra.toLowerCase());
        paso1a(w);
        paso1b(w);
        paso1c(w);
        reemplazarSiMedidaMayorQue(w, PASO_2, 0);
        reemplazarSiMedidaMayorQue(w, PASO_3, 0);
        paso4(w);
        paso5(w);
        return w.toString();
    }

    private void paso1a(StringBuilder w) {
        if (termina(w, "sses")) {
            quitar(w, 2);
        } else if (termina(w, "ies")) {
            quitar(w, 2);
        } else if (termina(w, "ss")) {
        } else if (termina(w, "s")) {
            quitar(w, 1);
        }
    }

    private void paso1b(StringBuilder w) {
        if (termina(w, "eed")) {
            if (medida(raiz(w, "eed")) > 0) {
                quitar(w, 1);
            }
            return;
        }
        boolean quitado = false;
        if (termina(w, "ed") && tieneVocal(raiz(w, "ed"))) {
            quitar(w, 2);
            quitado = true;
        } else if (termina(w, "ing") && tieneVocal(raiz(w, "ing"))) {
            quitar(w, 3);
            quitado = true;
        }
        if (!quitado) {
            return;
        }
        if (termina(w, "at") || termina(w, "bl") || termina(w, "iz")) {
            w.append('e');
        } else if (dobleConsonante(w) && !terminaEnUnaDe(w, "lsz")) {
            quitar(w, 1);
        } else if (medida(w.toString()) == 1 && consonanteVocalConsonante(w.toString())) {
            w.append('e');
        }
    }

    private void paso1c(StringBuilder w) {
        if (termina(w, "y") && tieneVocal(raiz(w, "y"))) {
            w.setCharAt(w.length() - 1, 'i');
        }
    }

    private void reemplazarSiMedidaMayorQue(StringBuilder w, String[][] reglas, int minimo) {
        for (String[] regla : reglas) {
            if (termina(w, regla[0])) {
                if (medida(raiz(w, regla[0])) > minimo) {
                    quitar(w, regla[0].length());
                    w.append(regla[1]);
                }
                return;
            }
        }
    }

    private void paso4(StringBuilder w) {
        for (String sufijo : PASO_4) {
            if (termina(w, sufijo)) {
                String raiz = raiz(w, sufijo);
                boolean ionValido = !sufijo.equals("ion") || raiz.endsWith("s") || raiz.endsWith("t");
                if (ionValido && medida(raiz) > 1) {
                    quitar(w, sufijo.length());
                }
                return;
            }
        }
    }

    private void paso5(StringBuilder w) {
        if (termina(w, "e")) {
            String raiz = raiz(w, "e");
            int m = medida(raiz);
            if (m > 1 || (m == 1 && !consonanteVocalConsonante(raiz))) {
                quitar(w, 1);
            }
        }
        if (medida(w.toString()) > 1 && dobleConsonante(w) && termina(w, "l")) {
            quitar(w, 1);
        }
    }

    private boolean termina(StringBuilder w, String sufijo) {
        int n = w.length();
        int s = sufijo.length();
        if (s > n) {
            return false;
        }
        for (int i = 0; i < s; i++) {
            if (w.charAt(n - s + i) != sufijo.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private boolean terminaEnUnaDe(StringBuilder w, String letras) {
        return letras.indexOf(w.charAt(w.length() - 1)) >= 0;
    }

    private String raiz(StringBuilder w, String sufijo) {
        return w.substring(0, w.length() - sufijo.length());
    }

    private void quitar(StringBuilder w, int cantidad) {
        w.setLength(w.length() - cantidad);
    }

    private boolean esConsonante(String s, int i) {
        char c = s.charAt(i);
        switch (c) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return false;
            case 'y':
                return i == 0 || !esConsonante(s, i - 1);
            default:
                return true;
        }
    }

    int medida(String s) {
        int n = 0;
        int i = 0;
        int largo = s.length();
        while (i < largo && esConsonante(s, i)) {
            i++;
        }
        while (i < largo) {
            while (i < largo && !esConsonante(s, i)) {
                i++;
            }
            if (i >= largo) {
                break;
            }
            while (i < largo && esConsonante(s, i)) {
                i++;
            }
            n++;
        }
        return n;
    }

    private boolean tieneVocal(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!esConsonante(s, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean dobleConsonante(StringBuilder w) {
        int n = w.length();
        return n >= 2 && w.charAt(n - 1) == w.charAt(n - 2) && esConsonante(w.toString(), n - 1);
    }

    private boolean consonanteVocalConsonante(String s) {
        int n = s.length();
        if (n < 3 || !esConsonante(s, n - 3) || esConsonante(s, n - 2) || !esConsonante(s, n - 1)) {
            return false;
        }
        char ultima = s.charAt(n - 1);
        return ultima != 'w' && ultima != 'x' && ultima != 'y';
    }
}
