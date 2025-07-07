package ec.edu.ups.controlador;

import ec.edu.ups.dao.CarritoDAO;
import ec.edu.ups.dao.PreguntaDAO;
import ec.edu.ups.dao.UsuarioDAO;
import ec.edu.ups.dao.impl.PreguntaDAOMemoria;
import ec.edu.ups.modelo.PreguntaSeg;
import ec.edu.ups.modelo.RespuestaSeg;
import ec.edu.ups.modelo.Usuario;
import ec.edu.ups.util.Idioma;
import ec.edu.ups.util.MensajeInternacionalizacionHandler;
import ec.edu.ups.vista.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static ec.edu.ups.modelo.Rol.USUARIO;

public class UsuarioController {

    private Usuario usuario;
    private final UsuarioDAO usuarioDAO;
    private final CarritoDAO carritoDAO;

    private final LoginView loginView;
    private final RegistrarUsuarioView registrarUsuarioView;
    private MenuPrincipalView principalView;
    private CuentaUsuarioView cuentaUsuarioView;
    private CuentaAdminView cuentaAdminView;

    private MensajeInternacionalizacionHandler mensInter;
    private PreguntaDAO preguntaDAO = new PreguntaDAOMemoria();
    private List<RespuestaSeg> respuestasSeguridadTemporales = Collections.emptyList();

    public UsuarioController(UsuarioDAO usuarioDAO, CarritoDAO carritoDAO, LoginView loginView,
                             RegistrarUsuarioView registrarUsuarioView) {
        this.usuarioDAO = usuarioDAO;
        this.carritoDAO = carritoDAO;
        this.loginView = loginView;
        this.registrarUsuarioView = registrarUsuarioView;
        this.usuario = null;

        this.mensInter = new MensajeInternacionalizacionHandler("es", "EC");

        //Cada vez que cambie el combo de idiomas en LoginView se actualiza el mih
        loginView.getComboBoxIdioma().addActionListener(e -> {
            Idioma sel = (Idioma) loginView.getComboBoxIdioma().getSelectedItem();
            mensInter.setLenguaje(sel.getLocale().getLanguage(), sel.getLocale().getCountry());
        });
        configurarEventosIniciales();
    }

    private void configurarEventosIniciales() {
        loginView.getBtnIniciarSesion().addActionListener(e -> autenticar());
        loginView.getBtnRegistrarse().addActionListener(e -> mostrarRegistrarse());
        loginView.getBtnRecuperarContra().addActionListener(e -> recuperarContrasenia());
        registrarUsuarioView.getBtnRegistrarse().addActionListener(e -> registrarUsuario());
    }

    private void mostrarRegistrarse() {
        Idioma sel = (Idioma) loginView.getComboBoxIdioma().getSelectedItem();
        this.mensInter = new MensajeInternacionalizacionHandler(
                sel.getLocale().getLanguage(), sel.getLocale().getCountry()
        );
        registrarUsuarioView.setMensInter(mensInter);
        registrarUsuarioView.actualizarTextos();
        registrarUsuarioView.setVisible(true);
    }

    private void abrirPreguntasDeSeguridadCompletas() {
        //muestran 10 preguntas del dao
        List<PreguntaSeg> todas = preguntaDAO.listarTodas();
        PreguntasSeguridad dlg = new PreguntasSeguridad(todas, mensInter, 5);
        dlg.setVisible(true);

        //se filtran las preguntas contestadas
        if (dlg.isSubmitted()) {
            List<RespuestaSeg> todasResps = dlg.getRespuestas();
            respuestasSeguridadTemporales = todasResps.stream()
                    .filter(r -> !r.getRespuesta().isBlank())
                    .collect(Collectors.toList());
        } else {
            respuestasSeguridadTemporales = Collections.emptyList();
        }
    }

    private void registrarUsuario() {
        String username = registrarUsuarioView.getTxtUsuario().getText().trim();
        String passwd   = new String(registrarUsuarioView.getPasswordField1().getPassword()).trim();
        String nombre   = registrarUsuarioView.getTextField1().getText().trim();
        String apellido = registrarUsuarioView.getTextField2().getText().trim();
        String email    = registrarUsuarioView.getTextField3().getText().trim();
        String fechaStr = registrarUsuarioView.getTextField4().getText().trim();
        String telefono = registrarUsuarioView.getTextField5().getText().trim();

        if (username.isEmpty() || passwd.isEmpty() || nombre.isEmpty() ||
                apellido.isEmpty() || email.isEmpty() || telefono.isEmpty() || fechaStr.isEmpty()) {
            registrarUsuarioView.mostrarMensaje(mensInter.get("registrar.mensaje.campos"));
            return;
        }

        //se valida fecha
        Date fechaNacimiento;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            fechaNacimiento = sdf.parse(fechaStr);
        } catch (ParseException ex) {
            registrarUsuarioView.mostrarMensaje("La fecha debe tener el formato yyyy-MM-dd.");
            return;
        }

        //valida usuario unico
        if (usuarioDAO.buscarPorUsername(username) != null) {
            registrarUsuarioView.mostrarMensaje(mensInter.get("registrar.mensaje.usuarioExiste"));
            return;
        }

        abrirPreguntasDeSeguridadCompletas();
        //se comprueba que se hayan llenado 5 preguntas
        if (respuestasSeguridadTemporales.size() != 5) {
            registrarUsuarioView.mostrarMensaje(mensInter.get("registrar.mensaje.preguntasS"));
            return;
        }

        //se crea el usuario y se guardan las respuestas
        Usuario nuevo = new Usuario(username, passwd, USUARIO,nombre, apellido, fechaNacimiento, email, telefono);
        nuevo.setRespuestasSeguridad(respuestasSeguridadTemporales);
        usuarioDAO.crear(nuevo);

        registrarUsuarioView.mostrarMensaje(mensInter.get("registrar.mensajeExito"));
        registrarUsuarioView.limpiarCampos();
        respuestasSeguridadTemporales = Collections.emptyList();
    }


    private void recuperarContrasenia() {
        // pedir usuario
        String username = JOptionPane.showInputDialog(
                loginView,
                mensInter.get("registrar.txtUsuario")
        );
        if (username == null || username.isBlank()) {
            return; // se cancela
        }

        // buscar usuario
        Usuario u = usuarioDAO.buscarPorUsername(username.trim());
        if (u == null) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.error.usuaNo"));
            return;
        }

        // obtener respuestas de seguridad guardadas
        List<RespuestaSeg> guardadas = u.getRespuestasSeguridad();
        if (guardadas == null || guardadas.size() < 3) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.error.respuestasIncom"));
            return;
        }

        // seleccionar 3 preguntas al azar
        Collections.shuffle(guardadas);
        List<PreguntaSeg> tresPreguntas = guardadas.stream()
                .limit(3)
                .map(RespuestaSeg::getPregunta)
                .collect(Collectors.toList());

        // mostrar ventana de respuestas
        PreguntasSeguridad dlg = new PreguntasSeguridad(tresPreguntas, mensInter, 3);
        dlg.setVisible(true);
        if (!dlg.isSubmitted()) {
            return;
        }

        // verificar que las respuestas no estén vacías
        List<RespuestaSeg> dadas = dlg.getRespuestas();
        boolean hayVacias = dadas.stream().anyMatch(r -> r.getRespuesta().trim().isEmpty());
        if (hayVacias) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.error.respuestasIncom"));
            return;
        }

        // validar respuestas dadas contra las guardadas
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

        // pedir nueva contraseña
        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(
                loginView, pf,
                mensInter.get("recuperarC.actualizarC"),
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        String nueva = new String(pf.getPassword()).trim();
        if (nueva.isEmpty()) {
            loginView.mostrarMensaje(mensInter.get("recuperarC.contraVacia"));
            return;
        }

        // actualizar contraseña
        u.setContrasenia(nueva);
        usuarioDAO.actualizar(u);
        loginView.mostrarMensaje(mensInter.get("recuperarC.mensajeExito"));
    }


    private void autenticar() {
        String username = loginView.getTxtUsuario().getText().trim();
        String contrasenia = new String(loginView.getTxtContrasenia().getPassword()).trim();

        usuario = usuarioDAO.autenticar(username, contrasenia);
        if (usuario == null) {
            loginView.mostrarMensaje("registrar.mensajeError");
        } else {
            // se lee el idioma que eligió el usuario
            Idioma sel = (Idioma) loginView.getComboBoxIdioma().getSelectedItem();
            String lang = sel.getLocale().getLanguage();
            String country = sel.getLocale().getCountry();

            //Creamos el handler UNA SOLA VEZ para toda la sesion
            this.mensInter = new MensajeInternacionalizacionHandler(lang, country);

            //Cerramos el login y arrancamos la principal
            loginView.dispose();
            iniciarPrincipal();
        }
    }

    private void iniciarPrincipal() {
        principalView = new MenuPrincipalView(mensInter);
        cuentaUsuarioView = new CuentaUsuarioView();
        cuentaUsuarioView.setMensajeHandler(mensInter);
        principalView.getjDesktopPane().add(cuentaUsuarioView);

        cuentaAdminView = new CuentaAdminView();
        cuentaAdminView.setMensajeHandler(mensInter);
        principalView.getjDesktopPane().add(cuentaAdminView);

        // menú Cuenta de usuario
        principalView.getMenuItemCuentaUsuario().addActionListener(e -> abrirCuentaUsuario());
        // menú Listar Usuarios (solo admin)
        principalView.getMenuItemListarUsuarios().addActionListener(e -> abrirCuentaAdmin());

        configurarEventosCuenta();
        configurarEventosCuentaAdmin();

        principalView.getMenuItemSalir().addActionListener(e -> {
            System.exit(0);
        });

        // 2) Cerrar sesión: cierro principal y muestro login
        principalView.getMenuItemSalirALogin().addActionListener(e -> {
            principalView.dispose();
            loginView.setVisible(true);
        });

        principalView.setVisible(true);
    }

    public void abrirCuentaUsuario() {
        cuentaUsuarioView.getTxtNombreUsuario().setText(usuario.getUsername());
        cuentaUsuarioView.getTextField1().setText("********");
        cuentaUsuarioView.setVisible(true);
        configurarEventosCuenta();
    }

    private void configurarEventosCuenta() {
        cuentaUsuarioView.getEditarNombreButton().addActionListener(e -> editarNombre());
        cuentaUsuarioView.getCambiarButton().addActionListener(e -> cambiarContrasenia());
        cuentaUsuarioView.getBtnEliminarCuenta().addActionListener(e -> eliminarCuenta());
        cuentaUsuarioView.getBtnCerrarSesion().addActionListener(e -> cerrarSesion());
    }

    private void editarNombre() {
        String nuevo = cuentaUsuarioView.getTxtNombreUsuario().getText().trim();
        if (nuevo.isEmpty()) {
            cuentaUsuarioView.mostrarMensaje(
                    mensInter.get("sesionUsuario.mensajeError.usuarioVacio")
            );
            return;
        }
        if (usuarioDAO.buscarPorUsername(nuevo) != null) {
            cuentaUsuarioView.mostrarMensaje(
                    mensInter.get("sesionUsuario.mensajeError.usuarioExis")
            );
            return;
        }
        //actualiza modelo y base datos
        usuario.setUsername(nuevo);
        usuarioDAO.actualizar(usuario);
        //cambios en interfaz
        cuentaUsuarioView.getTxtNombreUsuario().setText(nuevo);
        cuentaUsuarioView.mostrarMensaje(
                mensInter.get("sesionUsuario.mensajeExito")
        );
    }


    private void cambiarContrasenia() {
        JPasswordField pf = new JPasswordField();

        int ok = JOptionPane.showConfirmDialog(cuentaUsuarioView,pf, mensInter.get("sesionUsuario.mensaje.contrasenia"),
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok == JOptionPane.OK_OPTION) {
            String nueva = new String(pf.getPassword()).trim();
            if (nueva.isEmpty()) {
                JOptionPane.showMessageDialog(
                        cuentaUsuarioView,
                        mensInter.get("sesionUsuario.mensajeError.contraseniaV"),
                        mensInter.get("sesionUsuario.titulo.mensaje"),
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            usuario.setContrasenia(nueva);
            usuarioDAO.actualizar(usuario);
            JOptionPane.showMessageDialog(cuentaUsuarioView, mensInter.get("sesionUsuario.mensajeExito.modif"));
        }
    }

    private void eliminarCuenta() {
        int confirm = JOptionPane.showConfirmDialog(cuentaUsuarioView,
                mensInter.get("sesionUsuario.mensajeComprobacion"),
                mensInter.get("sesionUsuario.mensajeComprobacion.titulo"),
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            //carritoDAO.eliminarPorUsuario(usuario.getUsername());
            usuarioDAO.eliminar(usuario.getUsername());
            principalView.dispose();
            loginView.setVisible(true);
        }
    }

    private void cerrarSesion() {
        cuentaUsuarioView.dispose();
        principalView.dispose();
        loginView.setVisible(true);
    }

    private void abrirCuentaAdmin() {
        // carga todos los usuarios inicialmente
        List<String> todos = usuarioDAO.listarTodos()
                .stream().map(Usuario::getUsername)
                .collect(Collectors.toList());
        cuentaAdminView.cargarUsuarios(todos);
        cuentaAdminView.setVisible(true);
    }

    private void configurarEventosCuentaAdmin() {
        cuentaAdminView.getBtnListar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarUsuarios();
            }
        });

        cuentaAdminView.getBtnBuscar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarUsuario();
            }
        });
        cuentaAdminView.getBtnEliminar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarUsuario();
            }
        });

        cuentaAdminView.getBtnModificarNom().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarNombreUsuario();
            }
        });

        cuentaAdminView.getBtnModificarContra().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modificarContraseniaUsuario();
            }
        });

        cuentaAdminView.getBtnCerrarSesion().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesionAdmin();
            }
        });
    }

    private void listarUsuarios() {
        List<String> lista = usuarioDAO.listarTodos()
                .stream().map(Usuario::getUsername)
                .collect(Collectors.toList());
        cuentaAdminView.cargarUsuarios(lista);
    }

    private void buscarUsuario() {
        String nombre = cuentaAdminView.getTextField1().getText().trim();
        if (nombre.isEmpty()) {
            cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.mensajeIngreso"));
            return;
        }
        Usuario u = usuarioDAO.buscarPorUsername(nombre);
        if (u == null) {
            cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.mensaje.usuarioNo"));
            cuentaAdminView.cargarUsuarios(List.of());
        } else {
            cuentaAdminView.cargarUsuarios(List.of(u.getUsername()));
        }
    }

    private void eliminarUsuario() {
        int idx = cuentaAdminView.getTable1().getSelectedRow();
        if (idx < 0) {
            cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.seleccioneU"));
            return;
        }
        String nom = (String) cuentaAdminView.getTable1().getValueAt(idx, 0);
        usuarioDAO.eliminar(nom);
        abrirCuentaAdmin();
        cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.mensajeConf.usuaEli"));
    }

    private void modificarNombreUsuario() {
        int idx = cuentaAdminView.getTable1().getSelectedRow();
        if (idx < 0) {
            cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.seleccU.modif"));
            return;
        }
        String actual = (String) cuentaAdminView.getTable1().getValueAt(idx, 0);
        String prompt = mensInter.get("sesionA.nuevoNom") + ":";
        String nuevo = (String) JOptionPane.showInputDialog(cuentaAdminView,prompt,prompt,JOptionPane.PLAIN_MESSAGE,
                null,null,actual);
        if (nuevo != null && !nuevo.trim().isEmpty()) {
            if (usuarioDAO.buscarPorUsername(nuevo) != null) {
                cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.nomUso"));
                return;
            }
            Usuario u = usuarioDAO.buscarPorUsername(actual);
            u.setUsername(nuevo);
            usuarioDAO.actualizar(u);
            abrirCuentaAdmin();
            cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.mensaje.nomModif"));
        }
    }

    private void modificarContraseniaUsuario() {
        int idx = cuentaAdminView.getTable1().getSelectedRow();
        if (idx < 0) {
            cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.seleccUsu.contra"));
            return;
        }
        String nom = (String) cuentaAdminView.getTable1().getValueAt(idx, 0);
        String title = mensInter.get("sesionA.mensajeNueva.contra") + ": " + nom;
        JPasswordField pf = new JPasswordField();
        int ok = JOptionPane.showConfirmDialog(cuentaAdminView,pf,title,JOptionPane.OK_CANCEL_OPTION);

        if (ok == JOptionPane.OK_OPTION) {
            String nueva = new String(pf.getPassword()).trim();
            if (nueva.isEmpty()) {
                cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.contraVacia"));
                return;
            }
            Usuario u = usuarioDAO.buscarPorUsername(nom);
            u.setContrasenia(nueva);
            usuarioDAO.actualizar(u);
            cuentaAdminView.mostrarMensaje(mensInter.get("sesionA.contr.modif"));
        }
    }

    private void cerrarSesionAdmin() {
        cuentaAdminView.dispose();
        principalView.dispose();
        loginView.setVisible(true);
    }

    public Usuario getUsuarioAutenticado() {
        return usuario;
    }

    //Permite acceder a la ventana principal creada internamente
    public MenuPrincipalView getPrincipalView() {
        return principalView;
    }

    public MensajeInternacionalizacionHandler getMensInter() {
        return mensInter;
    }

    public void setMensInter(MensajeInternacionalizacionHandler mensInter) {
        this.mensInter = mensInter;
    }
}