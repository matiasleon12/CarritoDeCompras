package ec.edu.ups.util;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public class Formatear {

    public static String formatearMoneda(double cantidad, Locale locale) {
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(locale);
        //dos decimales
        formatoMoneda.setMinimumFractionDigits(2);
        formatoMoneda.setMaximumFractionDigits(2);
        return formatoMoneda.format(cantidad);
    }


    public static String formatearFecha(Date fecha, Locale locale) {
            DateFormat formato = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
            return formato.format(fecha);
    }
}

