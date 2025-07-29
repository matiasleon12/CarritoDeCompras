package ec.edu.ups.controlador;

import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.dao.PreguntaDAO;
import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.dao.impl.PreguntaDAOMemoria;
import ec.edu.ups.dao.impl.archivo.UsuarioDAOArchivoTexto;
import ec.edu.ups.dao.impl.binario.UsuarioDAOArchivoBinario;
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

public class UsuarioController {

    private Usuario usuario;
    private final UsuarioDAO usuarioDAO; // DAO en memoria, usado como fallback
    private final CarritoDAO carritoDAO;

    private final LoginView loginView;
    private final RegistrarUsuarioView registrarUsuarioView;

    private final MensajeInternacionalizacionHandler mensInter;
    private final PreguntaDAO preguntaDAO;
    private List<RespuestaSeg> respuestasSeguridadTemporales = Collections.emptyList();

    public UsuarioController(UsuarioDAO usuarioDAO, CarritoDAO carritoDAO, LoginView loginView,
                             RegistrarUsuarioView registrarUsuarioView) {
        this.usuarioDAO = usuarioDAO;
        this.carritoDAO = carritoDAO;
        this.loginView = loginView;
        this.registrarUsuarioView = registrarUsuarioView;
        this.usuario = null;

        this.mensInter = new MensajeInternacionalizacionHandler("es", "EC");
        this.preguntaDAO = new PreguntaDAOMemoria();

        this.loginView.setMensajeInternacionalizacionHandler(this.mensInter);
        this.registrarUsuarioView.setMensInter(this.mensInter);

        this.loginView.getComboBoxIdioma().addActionListener(e -> cambiarIdioma());
        configurarEventosIniciales();
    }

    private void cambiarIdioma() {
        Idioma seleccionado = (Idioma) loginView.getComboBoxIdioma().getSelectedItem();
        if (seleccionado != null) {
            mensInter.setLenguaje(seleccionado.getLocale().getLanguage(), seleccionado.getLocale().getCountry());
            loginView.actualizarTextos();
            registrarUsuarioView.actualizarTextos();
        }
    }

    private void configurarEventosIniciales() {
        loginView.getBtnIniciarSesion().addActionListener(e -> autenticar());
        loginView.getBtnRegistrarse().addActionListener(e -> mostrarRegistrarse());
        loginView.getBtnRecuperarContra().addActionListener(e -> recuperarContrasenia());
        registrarUsuarioView.getBtnRegistrarse().addActionListener(e -> registrarUsuario());
    }

    private void mostrarRegistrarse() {
        registrarUsuarioView.actualizarTextos();
        registrarUsuarioView.setVisible(true);
    }

    /**
     * MÉTODO CLAVE: Lee la selección de la vista y crea la instancia de DAO correcta.
     */
    private UsuarioDAO obtenerDAOSeleccionado() {
        LoginView.TipoAlmacenamiento tipo = loginView.getTipoAlmacenamientoSeleccionado();
        String ruta = loginView.getRutaAlmacenamiento();

        switch (tipo) {
            case TEXTO:
                System.out.println("DEBUG: Usando UsuarioDAO de Texto para la operación.");
                return new UsuarioDAOArchivoTexto(ruta);
            case BINARIO:
                System.out.println("DEBUG: Usando UsuarioDAO Binario para la operación.");
                return new UsuarioDAOArchivoBinario(ruta);
            default: // MEMORIA
                System.out.println("DEBUG: Usando UsuarioDAO en Memoria para la operación.");
                return this.usuarioDAO;
        }
    }

    private void autenticar() {
        String username = loginView.getTxtUsuario().getText().trim();
        String contrasenia = new String(loginView.getTxtContrasenia().getPassword()).trim();

        // Autentica usando el DAO correcto basado en la selección de la vista
        UsuarioDAO daoParaAutenticar = obtenerDAOSeleccionado();

        usuario = daoParaAutenticar.autenticar(username, contrasenia);
        if (usuario == null) {
            loginView.mostrarMensaje(mensInter.get("registrar.mensajeError"));
        } else {
            loginView.dispose();
        }
    }

    private void registrarUsuario() {
        try {
            // Obtiene el DAO correcto (Texto o Binario) para guardar el nuevo usuario
            UsuarioDAO daoParaRegistro = obtenerDAOSeleccionado();

            String username = registrarUsuarioView.getTxtUsuario().getText().trim();
            String passwd = new String(registrarUsuarioView.getPasswordField1().getPassword()).trim();
            String nombre = registrarUsuarioView.getTextField1().getText().trim();
            String apellido = registrarUsuarioView.getTextField2().getText().trim();
            String email = registrarUsuarioView.getTextField3().getText().trim();
            String fechaStr = registrarUsuarioView.getTextField4().getText().trim();
            String telefono = registrarUsuarioView.getTextField5().getText().trim();

            if (username.isEmpty() || passwd.isEmpty() || nombre.isEmpty() ||
                    apellido.isEmpty() || email.isEmpty() || telefono.isEmpty() || fechaStr.isEmpty()) {
                throw new ValidacionException(mensInter.get("registrar.mensaje.campos"));
            }

            /*if (!Validador.validarCedula(username)) {
                throw new ValidacionException(mensInter.get("registrar.error.cedulaInvalida"));
            }*/

            if (!Validador.validarContrasenia(passwd)) {
                throw new ValidacionException(mensInter.get("registrar.error.contraseniaInsegura"));
            }

            Date fechaNacimiento;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                fechaNacimiento = sdf.parse(fechaStr);
            } catch (ParseException ex) {
                throw new ValidacionException(mensInter.get("registrar.error.formatoFecha"));
            }

            if (daoParaRegistro.buscarPorUsername(username) != null) {
                throw new ValidacionException(mensInter.get("registrar.mensaje.usuarioExiste"));
            }

            abrirPreguntasDeSeguridadCompletas();
            if (respuestasSeguridadTemporales.size() < 3) {
                throw new ValidacionException(mensInter.get("preguntaS.error.Responder"));
            }

            Usuario nuevo = new Usuario(username, passwd, Rol.USUARIO, nombre, apellido, fechaNacimiento, email, telefono);
            nuevo.setRespuestasSeguridad(respuestasSeguridadTemporales);

            // Crea el usuario usando el DAO que lee/escribe en archivo
            daoParaRegistro.crear(nuevo);

            registrarUsuarioView.mostrarMensaje(mensInter.get("registrar.mensajeExito"));
            registrarUsuarioView.limpiarCampos();
            respuestasSeguridadTemporales = Collections.emptyList();
            registrarUsuarioView.dispose();

        } catch (ValidacionException e) {
            registrarUsuarioView.mostrarMensaje("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            registrarUsuarioView.mostrarMensaje("Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void recuperarContrasenia() {
        String username = JOptionPane.showInputDialog(loginView, mensInter.get("registrar.txtUsuario"));
        if (username == null || username.isBlank()) return;

        UsuarioDAO daoParaRecuperar = obtenerDAOSeleccionado();
        Usuario u = daoParaRecuperar.buscarPorUsername(username.trim());

        if (u == null) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.error.usuaNo"));
            return;
        }

        List<RespuestaSeg> guardadas = u.getRespuestasSeguridad();
        if (guardadas == null || guardadas.size() < 3) {
            loginView.mostrarMensaje("El usuario no tiene suficientes preguntas de seguridad configuradas.");
            return;
        }

        Collections.shuffle(guardadas);
        List<PreguntaSeg> tresPreguntas = guardadas.stream().limit(3).map(RespuestaSeg::getPregunta).collect(Collectors.toList());

        PreguntasSeguridad dlg = new PreguntasSeguridad(tresPreguntas, mensInter, 3);
        dlg.setVisible(true);
        if (!dlg.isSubmitted()) return;

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

        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(loginView, pf, mensInter.get("recuperarC.actualizarC"), JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String nueva = new String(pf.getPassword()).trim();
        if (nueva.isEmpty() || !Validador.validarContrasenia(nueva)) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.contraVacia"));
            return;
        }
        u.setContrasenia(nueva);
        daoParaRecuperar.actualizar(u);
        loginView.mostrarMensaje(mensInter.get("recuperarC.mensajeExito"));
    }

    private void abrirPreguntasDeSeguridadCompletas() {
        List<PreguntaSeg> todas = preguntaDAO.listarTodas();
        PreguntasSeguridad dlg = new PreguntasSeguridad(todas, mensInter, 3);
        dlg.setVisible(true);

        if (dlg.isSubmitted()) {
            respuestasSeguridadTemporales = dlg.getRespuestas().stream()
                    .filter(r -> !r.getRespuesta().isBlank())
                    .collect(Collectors.toList());
        } else {
            respuestasSeguridadTemporales = Collections.emptyList();
        }
    }

    public Usuario getUsuarioAutenticado() {
        return usuario;
    }

    public MensajeInternacionalizacionHandler getMensInter() {
        return mensInter;
    }
}