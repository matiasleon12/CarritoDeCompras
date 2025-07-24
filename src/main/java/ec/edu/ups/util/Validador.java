package ec.edu.ups.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase de utilidad para realizar validaciones de datos comunes en el sistema,
 * como la cédula ecuatoriana y la fortaleza de las contraseñas.
 */
public class Validador {

    /**
     * Valida una contraseña según los siguientes criterios:
     * - Mínimo 6 caracteres.
     * - Al menos una letra mayúscula (A-Z).
     * - Al menos una letra minúscula (a-z).
     * - Al menos uno de los siguientes caracteres especiales: @, _, -, .
     *
     * @param contrasenia La contraseña a validar.
     * @return true si la contraseña es válida, false en caso contrario.
     */
    public static boolean validarContrasenia(String contrasenia) {
        if (contrasenia == null || contrasenia.length() < 6) {
            return false;
        }
        // Expresión regular para validar la contraseña
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@_\\-.]).{6,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(contrasenia);
        return matcher.matches();
    }

    /**
     * Valida una cédula ecuatoriana utilizando el algoritmo de módulo 10.
     *
     * @param cedula El número de cédula de 10 dígitos como una cadena.
     * @return true si la cédula es válida, false en caso contrario.
     * @throws ValidacionException si la cédula no tiene 10 dígitos o contiene caracteres no numéricos.
     */
    public static boolean validarCedula(String cedula) throws ValidacionException {
        if (cedula == null || cedula.length() != 10) {
            throw new ValidacionException("La cédula debe tener 10 dígitos.");
        }
        if (!cedula.matches("\\d{10}")) {
            throw new ValidacionException("La cédula solo puede contener números.");
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Integer.parseInt(String.valueOf(cedula.charAt(i)));
            int producto = digito * coeficientes[i];
            if (producto >= 10) {
                producto -= 9;
            }
            suma += producto;
        }

        int digitoVerificadorCalculado = (suma % 10 == 0) ? 0 : 10 - (suma % 10);
        int ultimoDigito = Integer.parseInt(String.valueOf(cedula.charAt(9)));

        return ultimoDigito == digitoVerificadorCalculado;
    }
}