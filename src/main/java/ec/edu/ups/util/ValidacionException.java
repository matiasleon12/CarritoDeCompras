package ec.edu.ups.util;

/**
 * Excepción personalizada para errores de validación en la lógica de negocio.
 * Se utiliza para notificar a la capa de vista sobre datos incorrectos
 * ingresados por el usuario.
 */
public class ValidacionException extends Exception {

    /**
     * Constructor que acepta un mensaje de error.
     * @param mensaje El mensaje que describe el error de validación.
     */
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}