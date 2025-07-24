package ec.edu.ups.controlador;

import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.dao.PreguntaDAO;
import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.dao.impl.PreguntaDAOMemoria;
import ec.edu.ups.modelo.PreguntaSeg;
import ec.edu.ups.modelo.RespuestaSeg;
import ec.edu.ups.modelo.Rol;
import ec.edu.ups.modelo.Usuario;
import ec.edu.ups.util.Idioma;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;
import ec.edu.ups.util.ValidacionException;
import ec.edu.ups.util.Validador;
import ec.edu.ups.vista.*;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador que gestiona la lógica de negocio relacionada con los usuarios.
 * Maneja el flujo de registro, autenticación, recuperación de contraseña y la
 * configuración de la cuenta de usuario. Actúa como intermediario entre las
 * vistas de usuario (Login, Registro, Cuenta) y el DAO correspondiente.
 */
public class UsuarioController {

    private Usuario usuario;
    private final UsuarioDAO usuarioDAO;
    private final CarritoDAO carritoDAO;

    // Vistas que este controlador gestiona
    private final LoginView loginView;
    private final RegistrarUsuarioView registrarUsuarioView;
    private MenuPrincipalView principalView;
    private CuentaUsuarioView cuentaUsuarioView;
    private CuentaAdminView cuentaAdminView;

    // Utilidades
    private final MensajeInternacionalizacionHandler mensInter;
    private final PreguntaDAO preguntaDAO;
    private List<RespuestaSeg> respuestasSeguridadTemporales = Collections.emptyList();

    /**
     * Constructor del controlador de usuario.
     *
     * @param usuarioDAO           El objeto de acceso a datos para usuarios.
     * @param carritoDAO           El objeto de acceso a datos para carritos.
     * @param loginView            La vista de inicio de sesión.
     * @param registrarUsuarioView La vista de registro de nuevos usuarios.
     */
    public UsuarioController(UsuarioDAO usuarioDAO, CarritoDAO carritoDAO, LoginView loginView,
                             RegistrarUsuarioView registrarUsuarioView) {
        this.usuarioDAO = usuarioDAO;
        this.carritoDAO = carritoDAO;
        this.loginView = loginView;
        this.registrarUsuarioView = registrarUsuarioView;
        this.usuario = null;

        // El controlador es el dueño del gestor de internacionalización.
        this.mensInter = new MensajeInternacionalizacionHandler("es", "EC");
        this.preguntaDAO = new PreguntaDAOMemoria(); // Usamos la implementación en memoria para las preguntas

        // Se inyecta el manejador de idioma a las vistas.
        this.loginView.setMensajeInternacionalizacionHandler(this.mensInter);
        this.registrarUsuarioView.setMensInter(this.mensInter);

        // El controlador escucha los cambios en las vistas.
        this.loginView.getComboBoxIdioma().addActionListener(e -> cambiarIdioma());
        configurarEventosIniciales();
    }

    /**
     * Centraliza la lógica para cambiar el idioma de la aplicación.
     * Se activa cuando el usuario selecciona un nuevo idioma en la LoginView.
     */
    private void cambiarIdioma() {
        Idioma seleccionado = (Idioma) loginView.getComboBoxIdioma().getSelectedItem();
        if (seleccionado != null) {
            mensInter.setLenguaje(seleccionado.getLocale().getLanguage(), seleccionado.getLocale().getCountry());
            // Ordena a las vistas que actualicen sus textos.
            loginView.actualizarTextos();
            registrarUsuarioView.actualizarTextos();
        }
    }

    /**
     * Configura los ActionListeners para los botones iniciales (login, registro).
     */
    private void configurarEventosIniciales() {
        loginView.getBtnIniciarSesion().addActionListener(e -> autenticar());
        loginView.getBtnRegistrarse().addActionListener(e -> mostrarRegistrarse());
        loginView.getBtnRecuperarContra().addActionListener(e -> recuperarContrasenia());
        registrarUsuarioView.getBtnRegistrarse().addActionListener(e -> registrarUsuario());
    }

    /**
     * Muestra la ventana de registro de usuario.
     */
    private void mostrarRegistrarse() {
        registrarUsuarioView.actualizarTextos();
        registrarUsuarioView.setVisible(true);
    }

    /**
     * Proceso de registro de un nuevo usuario, incorporando validaciones avanzadas.
     */
    private void registrarUsuario() {
        try {
            String username = registrarUsuarioView.getTxtUsuario().getText().trim();
            String passwd = new String(registrarUsuarioView.getPasswordField1().getPassword()).trim();
            String nombre = registrarUsuarioView.getTextField1().getText().trim();
            String apellido = registrarUsuarioView.getTextField2().getText().trim();
            String email = registrarUsuarioView.getTextField3().getText().trim();
            String fechaStr = registrarUsuarioView.getTextField4().getText().trim();
            String telefono = registrarUsuarioView.getTextField5().getText().trim();

            // 1. Validación de campos vacíos
            if (username.isEmpty() || passwd.isEmpty() || nombre.isEmpty() ||
                    apellido.isEmpty() || email.isEmpty() || telefono.isEmpty() || fechaStr.isEmpty()) {
                throw new ValidacionException(mensInter.get("registrar.mensaje.campos"));
            }

            // 2. Validación de cédula (username)
            if (!Validador.validarCedula(username)) {
                throw new ValidacionException(mensInter.get("registrar.error.cedulaInvalida"));
            }

            // 3. Validación de contraseña segura
            if (!Validador.validarContrasenia(passwd)) {
                throw new ValidacionException(mensInter.get("registrar.error.contraseniaInsegura"));
            }

            // 4. Validación de formato de fecha
            Date fechaNacimiento;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                fechaNacimiento = sdf.parse(fechaStr);
            } catch (ParseException ex) {
                throw new ValidacionException(mensInter.get("registrar.error.formatoFecha"));
            }

            // 5. Validar que el usuario (cédula) no exista previamente
            if (usuarioDAO.buscarPorUsername(username) != null) {
                throw new ValidacionException(mensInter.get("registrar.mensaje.usuarioExiste"));
            }

            // 6. Proceso de preguntas de seguridad
            abrirPreguntasDeSeguridadCompletas();
            if (respuestasSeguridadTemporales.size() != 5) {
                throw new ValidacionException(mensInter.get("registrar.mensaje.preguntasS"));
            }

            // 7. Si todo es válido, se crea el usuario
            Usuario nuevo = new Usuario(username, passwd, Rol.USUARIO, nombre, apellido, fechaNacimiento, email, telefono);
            nuevo.setRespuestasSeguridad(respuestasSeguridadTemporales);
            usuarioDAO.crear(nuevo);

            registrarUsuarioView.mostrarMensaje(mensInter.get("registrar.mensajeExito"));
            registrarUsuarioView.limpiarCampos();
            respuestasSeguridadTemporales = Collections.emptyList();
            registrarUsuarioView.dispose();

        } catch (ValidacionException e) {
            registrarUsuarioView.mostrarMensaje("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            registrarUsuarioView.mostrarMensaje("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    /**
     * Autentica al usuario contra el sistema de persistencia.
     */
    private void autenticar() {
        String username = loginView.getTxtUsuario().getText().trim();
        String contrasenia = new String(loginView.getTxtContrasenia().getPassword()).trim();

        usuario = usuarioDAO.autenticar(username, contrasenia);
        if (usuario == null) {
            loginView.mostrarMensaje(mensInter.get("registrar.mensajeError"));
        } else {
            // Si el login es exitoso, simplemente cerramos la ventana.
            // El listener en Main.java se encargará del resto.
            loginView.dispose();
        }
    }

    /**
     * Inicia el proceso de recuperación de contraseña.
     */
    private void recuperarContrasenia() {
        // Pedir nombre de usuario
        String username = JOptionPane.showInputDialog(loginView, mensInter.get("registrar.txtUsuario"));
        if (username == null || username.isBlank()) return;

        // Buscar usuario en la persistencia
        Usuario u = usuarioDAO.buscarPorUsername(username.trim());
        if (u == null) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.error.usuaNo"));
            return;
        }

        // Validar que tenga preguntas de seguridad
        List<RespuestaSeg> guardadas = u.getRespuestasSeguridad();
        if (guardadas == null || guardadas.size() < 3) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.error.respuestasIncom"));
            return;
        }

        // Seleccionar 3 preguntas al azar
        Collections.shuffle(guardadas);
        List<PreguntaSeg> tresPreguntas = guardadas.stream()
                .limit(3)
                .map(RespuestaSeg::getPregunta)
                .collect(Collectors.toList());

        // Mostrar diálogo para responder
        PreguntasSeguridad dlg = new PreguntasSeguridad(tresPreguntas, mensInter, 3);
        dlg.setVisible(true);
        if (!dlg.isSubmitted()) return;

        // Validar las respuestas dadas
        List<RespuestaSeg> dadas = dlg.getRespuestas();
        boolean todasBien = dadas.stream().allMatch(r ->
                guardadas.stream().anyMatch(g ->
                        g.getPregunta().equals(r.getPregunta()) &&
                                g.getRespuesta().trim().equalsIgnoreCase(r.getRespuesta().trim())
                )
        );
        if (!todasBien) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.error.respuestasIncorr"));
            return;
        }

        // Pedir y actualizar la nueva contraseña
        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(loginView, pf, mensInter.get("recuperarC.actualizarC"), JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String nueva = new String(pf.getPassword()).trim();
        if (nueva.isEmpty() || !Validador.validarContrasenia(nueva)) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.contraVacia"));
            return;
        }
        u.setContrasenia(nueva);
        usuarioDAO.actualizar(u);
        loginView.mostrarMensaje(mensInter.get("recuperarC.mensajeExito"));
    }

    // --- Métodos de la sesión de usuario (no requieren cambios mayores) ---

    private void abrirPreguntasDeSeguridadCompletas() {
        List<PreguntaSeg> todas = preguntaDAO.listarTodas();
        PreguntasSeguridad dlg = new PreguntasSeguridad(todas, mensInter, 5);
        dlg.setVisible(true);

        if (dlg.isSubmitted()) {
            respuestasSeguridadTemporales = dlg.getRespuestas().stream()
                    .filter(r -> !r.getRespuesta().isBlank())
                    .collect(Collectors.toList());
        } else {
            respuestasSeguridadTemporales = Collections.emptyList();
        }
    }

    // ... (El resto de tus métodos como iniciarPrincipal, abrirCuentaUsuario, etc., se mantienen igual) ...

    /**
     * Devuelve el usuario que ha sido autenticado exitosamente.
     * @return El objeto Usuario autenticado, o null si el login falló.
     */
    public Usuario getUsuarioAutenticado() {
        return usuario;
    }

    /**
     * Devuelve el manejador de internacionalización para que otras partes
     * de la aplicación (como Main) puedan usar la configuración de idioma correcta.
     * @return El objeto MensajeInternacionalizacionHandler.
     */
    public MensajeInternacionalizacionHandler getMensInter() {
        return mensInter;
    }
}