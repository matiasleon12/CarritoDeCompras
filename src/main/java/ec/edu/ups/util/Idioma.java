package ec.edu.ups.util;
import java.util.Locale;

public class Idioma {

    private final Locale locale;
    private final String nombreIdioma;

    public Idioma(Locale locale, String nombreIdioma) {
        this.locale = locale;
        this.nombreIdioma = nombreIdioma;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getNombreIdioma() {
        return nombreIdioma;
    }
    @Override
    public String toString() {
        return nombreIdioma;
    }
}

